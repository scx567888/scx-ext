package cool.scx.ext.redirects.test;

import cool.scx.mvc.annotation.ScxRoute;

@ScxRoute
public class HelloWorld {

    @ScxRoute("")
    public String helloWorld() {
        return "Hello World";
    }

}
