package cool.scx.ext.cms.test.bb;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;
import cool.scx.data.query.WhereOption;

import java.util.List;

import static cool.scx.data.query.WhereBody.jsonContains;

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
        List<BB> list = this.find(jsonContains("uus", "{\"name\":\"666\"}", WhereOption.USE_ORIGINAL_VALUE));
        List<BB> list1 = this.find(jsonContains("uu.name", "666"));
        List<BB> list2 = this.find(jsonContains("uus", aaa));
        List<BB> list3 = this.find(jsonContains("uus[0].phoneNumber[0]", "150"));
    }

}
