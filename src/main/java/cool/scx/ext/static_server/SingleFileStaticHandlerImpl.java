package cool.scx.ext.static_server;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.net.impl.URIDecoder;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.LRUCache;
import io.vertx.ext.web.impl.Utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.*;


class SingleFileStaticHandlerImpl implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(SingleFileStaticHandlerImpl.class);
    private static final Pattern RANGE = Pattern.compile("^bytes=(\\d+)-(\\d*)$");
    private final String singleFile;
    private final String defaultContentEncoding = StandardCharsets.UTF_8.name();
    private final FSTune tune = new FSTune();
    private final Map<String, CacheEntry> cache = new LRUCache<>(StaticHandler.DEFAULT_MAX_CACHE_SIZE);


    /**
     * Default constructor with DEFAULT_WEB_ROOT and
     * relative file access only
     */
    public SingleFileStaticHandlerImpl(Path root) {
        this.singleFile = root.toString();
    }

    /**
     * Create all required header so content can be cache by Caching servers or
     * Browsers
     *
     * @param request base HttpServerRequest
     * @param props   file properties
     */
    private void writeCacheHeaders(HttpServerRequest request, FileProps props) {

        MultiMap headers = request.response().headers();

        // We use cache-control and last-modified
        // We *do not use* etags and expires (since they do the same thing - redundant)
        // One day
        long maxAgeSeconds = StaticHandler.DEFAULT_MAX_AGE_SECONDS;
        Utils.addToMapIfAbsent(headers, HttpHeaders.CACHE_CONTROL, "public, immutable, max-age=" + maxAgeSeconds);
        Utils.addToMapIfAbsent(headers, HttpHeaders.LAST_MODIFIED, Utils.formatRFC1123DateTime(props.lastModifiedTime()));
        // We send the vary header (for intermediate caches)
        // (assumes that most will turn on compression when using static handler)
        boolean sendVaryHeader = StaticHandler.DEFAULT_SEND_VARY_HEADER;
        if (sendVaryHeader && request.headers().contains(HttpHeaders.ACCEPT_ENCODING)) {
            Utils.addToMapIfAbsent(headers, HttpHeaders.VARY, "accept-encoding");
        }
        // date header is mandatory
        headers.set("date", Utils.formatRFC1123DateTime(System.currentTimeMillis()));
    }

    @Override
    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();

        if (request.method() != HttpMethod.GET && request.method() != HttpMethod.HEAD) {
            if (LOG.isTraceEnabled()) LOG.trace("Not GET or HEAD so ignoring request");
            context.next();
        } else {
            if (!request.isEnded()) {
                request.pause();
            }
            // decode URL path
            String uriDecodedPath = URIDecoder.decodeURIComponent(context.normalizedPath(), false);
            // if the normalized path is null it cannot be resolved
            if (uriDecodedPath == null) {
                LOG.warn("Invalid path: " + context.request().path());
                context.next();
                return;
            }


            // Access fileSystem once here to be safe
            FileSystem fs = context.vertx().fileSystem();

            sendStatic(context, fs);
        }
    }

    /**
     * Can be called recursive for index pages
     */
    private void sendStatic(RoutingContext context, FileSystem fileSystem) {
        var path = "single-file";

        // Look in cache
        final CacheEntry entry = cache.get(path);

        if (entry != null) {
            if (!entry.isOutOfDate()) {
                // a cache entry can mean 2 things:
                // 1. a miss
                // 2. a hit

                // a miss signals that we should continue the chain
                if (entry.isMissing()) {
                    if (!context.request().isEnded()) {
                        context.request().resume();
                    }
                    context.next();
                    return;
                }

                // a hit needs to be verified for freshness
                final long lastModified = Utils.secondsFactor(entry.props.lastModifiedTime());

                if (Utils.fresh(context, lastModified)) {
                    context.response().setStatusCode(NOT_MODIFIED.code()).end();
                    return;
                }
            }
        }

        final boolean dirty = entry != null;
        final String localFile = singleFile;


        // verify if the file exists
        fileSystem.exists(localFile, exists -> {
            if (exists.failed()) {
                if (!context.request().isEnded()) {
                    context.request().resume();
                }
                context.fail(exists.cause());
                return;
            }

            // file does not exist, continue...
            if (!exists.result()) {
                cache.put(path, null);
                if (!context.request().isEnded()) {
                    context.request().resume();
                }
                context.next();
                return;
            }

            // Need to read the props from the filesystem
            getFileProps(fileSystem, localFile, res -> {
                if (res.succeeded()) {
                    FileProps fprops = res.result();
                    if (fprops == null) {
                        // File does not exist
                        if (dirty) {
                            cache.remove(path);
                        }
                        if (!context.request().isEnded()) {
                            context.request().resume();
                        }
                        context.next();
                    } else if (fprops.isRegularFile()) {
                        CacheEntry now = new CacheEntry(fprops, StaticHandler.DEFAULT_CACHE_ENTRY_TIMEOUT);
                        cache.put(path, now);

                        if (Utils.fresh(context, Utils.secondsFactor(fprops.lastModifiedTime()))) {
                            context.response().setStatusCode(NOT_MODIFIED.code()).end();
                            return;
                        }
                        sendFile(context, localFile, fprops);
                    }
                } else {
                    if (!context.request().isEnded()) {
                        context.request().resume();
                    }
                    context.fail(res.cause());
                }
            });
        });
    }

    private void getFileProps(FileSystem fileSystem, String file, Handler<AsyncResult<FileProps>> resultHandler) {
        if (tune.useAsyncFS()) {
            fileSystem.props(file, resultHandler);
        } else {
            // Use synchronous access - it might well be faster!
            try {
                final boolean tuneEnabled = tune.enabled();
                final long start = tuneEnabled ? System.nanoTime() : 0;
                FileProps props = fileSystem.propsBlocking(file);
                if (tuneEnabled) {
                    tune.update(start, System.nanoTime());
                }
                resultHandler.handle(Future.succeededFuture(props));
            } catch (RuntimeException e) {
                resultHandler.handle(Future.failedFuture(e.getCause()));
            }
        }
    }

    private void sendFile(RoutingContext context, String file, FileProps fileProps) {
        final HttpServerRequest request = context.request();
        final HttpServerResponse response = context.response();

        Long offset = null;
        Long end = null;
        MultiMap headers = null;

        if (response.closed()) return;

        // check if the client is making a range request
        String range = request.getHeader("Range");
        // end byte is length - 1
        end = fileProps.size() - 1;

        if (range != null) {
            Matcher m = RANGE.matcher(range);
            if (m.matches()) {
                try {
                    String part = m.group(1);
                    // offset cannot be empty
                    offset = Long.parseLong(part);
                    // offset must fall inside the limits of the file
                    if (offset < 0 || offset >= fileProps.size()) {
                        throw new IndexOutOfBoundsException();
                    }
                    // length can be empty
                    part = m.group(2);
                    if (part != null && part.length() > 0) {
                        // ranges are inclusive
                        end = Math.min(end, Long.parseLong(part));
                        // end offset must not be smaller than start offset
                        if (end < offset) {
                            throw new IndexOutOfBoundsException();
                        }
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    context.response().putHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileProps.size());
                    if (!context.request().isEnded()) {
                        context.request().resume();
                    }
                    context.fail(REQUESTED_RANGE_NOT_SATISFIABLE.code());
                    return;
                }
            }
        }

        // notify client we support range requests
        headers = response.headers();
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        // send the content length even for HEAD requests
        headers.set(HttpHeaders.CONTENT_LENGTH, Long.toString(end + 1 - (offset == null ? 0 : offset)));

        writeCacheHeaders(request, fileProps);

        if (request.method() == HttpMethod.HEAD) {
            response.end();
        } else {
            if (offset != null) {
                // must return content range
                headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + offset + "-" + end + "/" + fileProps.size());
                // return a partial response
                response.setStatusCode(PARTIAL_CONTENT.code());

                final long finalOffset = offset;
                final long finalLength = end + 1 - offset;
                // guess content type
                String contentType = MimeMapping.getMimeTypeForFilename(file);
                if (contentType != null) {
                    if (contentType.startsWith("text")) {
                        response.putHeader(HttpHeaders.CONTENT_TYPE, contentType + ";charset=" + defaultContentEncoding);
                    } else {
                        response.putHeader(HttpHeaders.CONTENT_TYPE, contentType);
                    }
                }

                response.sendFile(file, finalOffset, finalLength, res2 -> {
                    if (res2.failed()) {
                        if (!context.request().isEnded()) {
                            context.request().resume();
                        }
                        context.fail(res2.cause());
                    }
                });
            } else {
                // guess content type
                String extension = getFileExtension(file);
                String contentType = MimeMapping.getMimeTypeForExtension(extension);

                if (contentType != null) {
                    if (contentType.startsWith("text")) {
                        response.putHeader(HttpHeaders.CONTENT_TYPE, contentType + ";charset=" + defaultContentEncoding);
                    } else {
                        response.putHeader(HttpHeaders.CONTENT_TYPE, contentType);
                    }
                }

                response.sendFile(file, res2 -> {
                    if (res2.failed()) {
                        if (!context.request().isEnded()) {
                            context.request().resume();
                        }
                        context.fail(res2.cause());
                    }
                });
            }
        }
    }


    private String getFileExtension(String file) {
        int li = file.lastIndexOf(46);
        if (li != -1 && li != file.length() - 1) {
            return file.substring(li + 1);
        } else {
            return null;
        }
    }

    private static final class CacheEntry {
        final long createDate = System.currentTimeMillis();

        final FileProps props;
        final long cacheEntryTimeout;

        private CacheEntry(FileProps props, long cacheEntryTimeout) {
            this.props = props;
            this.cacheEntryTimeout = cacheEntryTimeout;
        }

        boolean isOutOfDate() {
            return System.currentTimeMillis() - createDate > cacheEntryTimeout;
        }

        public boolean isMissing() {
            return props == null;
        }
    }

    private static class FSTune {
        // These members are all related to auto tuning of synchronous vs asynchronous
        // file system access
        private static final int NUM_SERVES_TUNING_FS_ACCESS = 1000;

        // these variables are read often and should always represent the
        // real value, no caching should be allowed
        private volatile boolean enabled = true;
        private volatile boolean useAsyncFS;

        private long totalTime;
        private long numServesBlocking;
        private long nextAvgCheck = NUM_SERVES_TUNING_FS_ACCESS;

        boolean enabled() {
            return enabled;
        }

        boolean useAsyncFS() {
            return useAsyncFS;
        }

        synchronized void update(long start, long end) {
            long dur = end - start;
            totalTime += dur;
            numServesBlocking++;
            if (numServesBlocking == Long.MAX_VALUE) {
                // Unlikely.. but...
                reset();
            } else if (numServesBlocking == nextAvgCheck) {
                double avg = (double) totalTime / numServesBlocking;
                long maxAvgServeTimeNanoSeconds = StaticHandler.DEFAULT_MAX_AVG_SERVE_TIME_NS;
                if (avg > maxAvgServeTimeNanoSeconds) {
                    useAsyncFS = true;
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Switching to async file system access in static file server as fs access is slow! (Average access time of " + avg + " ns)");
                    }
                    enabled = false;
                }
                nextAvgCheck += NUM_SERVES_TUNING_FS_ACCESS;
            }
        }

        synchronized void reset() {
            nextAvgCheck = NUM_SERVES_TUNING_FS_ACCESS;
            totalTime = 0;
            numServesBlocking = 0;
        }
    }

}
