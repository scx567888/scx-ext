package cool.scx.ext.test.other;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Test01 {

    public static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    public static void main(String[] args) {
        f1();
        foo(() -> f1());
    }

    public static void f1() {
        Object o = THREAD_LOCAL.get();
        if (o == null) {
            System.out.println("not in foo");
        } else {
            System.out.println("in foo " + o);
        }
    }

    public static void foo(Handler handler) {
        try {
            CompletableFuture.runAsync(() -> {
                THREAD_LOCAL.set("Some Data");
                handler.handle();
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static <T> T foo(HandlerR<T> handler) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                THREAD_LOCAL.set("Some Data");
                return handler.handle();
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Handler {
        void handle();
    }

    public interface HandlerR<R> {
        R handle();
    }

}
