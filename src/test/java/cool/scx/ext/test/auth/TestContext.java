package cool.scx.ext.test.auth;

import cool.scx.core.ScxContext;

public class TestContext {

    public static TestUser getCurrentUser() {
        return ScxContext.getBean(TestAuthHandler.class).getCurrentUser();
    }

}