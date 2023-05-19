package cool.scx.ext.cms.test.other;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class WatchFileHelper {

    public static void watchFile(String strPath, ScxTreeVisitor scxTreeVisitor) throws IOException {
        new WatchFileTask(strPath, scxTreeVisitor, true).start();
    }

    public static void watchDirectory(String strPath, ScxTreeVisitor scxTreeVisitor) throws IOException {
        new WatchFileTask(strPath, scxTreeVisitor, false).start();
    }

    public static void main(String[] args) throws IOException {
        var file = "";
        WatchFileHelper.watchFile(file, new ScxTreeVisitor() {

        });
    }

    public interface ScxTreeVisitor {
        default void onCreate() {
            System.out.println("onCreate");
        }

        default void onModify() {
            System.out.println("onModify");
        }

        default void onDelete() {
            System.out.println("onDelete");
        }
    }

    public static class WatchFileTask {

        final WatchService watcher;
        final Path dir;
        final String fileName;
        final Thread watchedThread;

        public WatchFileTask(String strPath, ScxTreeVisitor scxTreeVisitor, boolean isFile) throws IOException {
            this.watcher = FileSystems.getDefault().newWatchService();
            var file = new File(strPath);
            if (isFile) {
                this.dir = file.toPath().getParent();
                this.fileName = file.getName();
                this.dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                this.watchedThread = new Thread(() -> {
                    while (true) {
                        WatchKey key;
                        try {
                            key = watcher.take();
                        } catch (ClosedWatchServiceException ex) {
                            // The watch service has been closed, we are done
                            break;
                        } catch (InterruptedException ex) {
                            // tolerate an interrupt
                            continue;
                        }

                        for (WatchEvent<?> pollEvent : key.pollEvents()) {
                            if (pollEvent.context().toString().equals(fileName)) {
                                if (pollEvent.kind() == ENTRY_CREATE) {
                                    scxTreeVisitor.onCreate();
                                } else if (pollEvent.kind() == ENTRY_MODIFY) {
                                    scxTreeVisitor.onModify();
                                } else if (pollEvent.kind() == ENTRY_DELETE) {
                                    scxTreeVisitor.onDelete();
                                }
                            }
                        }

                        boolean valid = key.reset();
                        if (!valid) {

                            break;
                        }
                    }
                });
            } else {
                this.dir = file.toPath();
                this.fileName = file.getName();
                this.dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                this.watchedThread = new Thread(() -> {
                    while (true) {
                        WatchKey key;
                        try {
                            key = watcher.take();
                        } catch (ClosedWatchServiceException ex) {

                            break;
                        } catch (InterruptedException ex) {

                            continue;
                        }

                        for (WatchEvent<?> pollEvent : key.pollEvents()) {
                            System.out.println(pollEvent.kind());
                        }

                        boolean valid = key.reset();
                        if (!valid) {

                            break;
                        }
                    }
                });
            }

        }

        public void start() {
            watchedThread.start();
        }
    }

}
