package cool.scx.ext.cms.test.bb;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;
import cool.scx.data.Query;
import cool.scx.data.query.WhereOption;

import java.util.List;

@ScxService
public class BBService extends BaseModelService<BB> {

    public void test() {
        var bb = new BB();
        bb.name = "123123";
        var uu1 = new BB.UU();
        uu1.phoneNumber = new String[]{"150", "190"};
        uu1.name = "666";
        var uu2 = new BB.UU();
        uu2.phoneNumber = new String[]{"180", "130"};
        bb.uus = new BB.UU[]{uu1, uu2};
        bb.uu = uu1;
        this.add(bb);
        var aaa = new BB.UU();
//        aaa.phoneNumber=new String[]{"150"};
        aaa.name = "666";
        List<BB> list = this.list(new Query().jsonContains("uus", "{\"name\":\"666\"}", WhereOption.USE_ORIGINAL_VALUE));
        List<BB> list1 = this.list(new Query().jsonContains("uu.name", "666"));
        List<BB> list2 = this.list(new Query().jsonContains("uus", aaa));
        List<BB> list3 = this.list(new Query().jsonContains("uus[0].phoneNumber[0]", "150"));
    }

}
