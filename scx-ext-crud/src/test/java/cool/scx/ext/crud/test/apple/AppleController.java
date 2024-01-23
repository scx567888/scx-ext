package cool.scx.ext.crud.test.apple;

import cool.scx.ext.crud.BaseCRUDController;
import cool.scx.mvc.annotation.ScxRoute;

@ScxRoute("api/apple")
public class AppleController extends BaseCRUDController<AppleService, Apple> {

    public AppleController(AppleService service) {
        super(service);
    }

}
