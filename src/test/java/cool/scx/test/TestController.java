package cool.scx.test;


import cool.scx.ScxContext;
import cool.scx.annotation.FromQuery;
import cool.scx.annotation.ScxMapping;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;
import cool.scx.enumeration.HttpMethod;
import cool.scx.enumeration.RawType;
import cool.scx.ext.office.Excel;
import cool.scx.ext.office.QRCodeUtils;
import cool.scx.ext.organization.User;
import cool.scx.ext.organization.UserService;
import cool.scx.ext.organization.auth.OrganizationPerms;
import cool.scx.test.car.Car;
import cool.scx.util.CryptoUtils;
import cool.scx.util.HttpUtils;
import cool.scx.util.RandomUtils;
import cool.scx.util.digest.DigestUtils;
import cool.scx.util.zip.IVirtualFile;
import cool.scx.util.zip.VirtualDirectory;
import cool.scx.util.zip.VirtualFile;
import cool.scx.util.zip.ZipAction;
import cool.scx.vo.*;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 简单测试
 *
 * @author scx567888
 * @version 0.3.6
 * @since 1.3.14
 */
@ScxMapping("/")
public class TestController {

    private final UserService userService;

    private final BaseService<Car> carService = new BaseService<>(Car.class);

    /**
     * TestController
     *
     * @param userService a
     */
    public TestController(UserService userService) {
        this.userService = userService;
    }

    @ScxMapping(method = HttpMethod.GET)
    public static void TestTransaction(RoutingContext ctx) throws Exception {
        var sb = new StringBuilder();
        UserService bean = ScxContext.beanFactory().getBean(UserService.class);
        try {
            ScxContext.sqlRunner().autoTransaction((con) -> {
                sb.append("事务开始前数据库中 数据条数 : ").append(bean.list(con).size()).append("</br>");
                sb.append("现在插入 1 数据条数").append("</br>");
                var u = new User();
                u.password = CryptoUtils.encryptPassword("123456");
                u.username = "唯一name";
                bean.save(con, u);
                sb.append("现在数据库中数据条数 : ").append(bean.list(con).size()).append("</br>");
                bean.save(con, u);
            });
        } catch (Exception e) {
            sb.append("出错了 后滚后数据库中数据条数 : ").append(bean.list().size());
        }
        Html.ofString(sb.toString()).handle(ctx);
    }

    /**
     * 测试!!!
     *
     * @return a {@link cool.scx.vo.Html} object
     */
    @OrganizationPerms()
    @ScxMapping(value = "/baidu", method = HttpMethod.GET)
    public Html TestHttpUtils() throws IOException, InterruptedException {
        HttpResponse<String> stringHttpResponse = HttpUtils.get("https://www.baidu.com/", new HashMap<>());
        return Html.ofString(stringHttpResponse.body());
    }

    /**
     * 测试!!!
     *
     * @return a {@link cool.scx.vo.Download} object
     */
    @ScxMapping(value = "/download", method = HttpMethod.GET)
    public Download TestDownload() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 9999; i++) {
            s.append("download ").append(i);
        }
        return new Download(s.toString().getBytes(StandardCharsets.UTF_8), "测试中 + - ~!文 a😊😂 🤣 ghj ❤😍😒👌.txt");
    }

    /**
     * 测试!!!
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(value = "/binary", method = HttpMethod.GET)
    public BaseVo TestBinary() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 9999; i++) {
            s.append("download ").append(i);
        }
        return new Raw(s.toString().getBytes(StandardCharsets.UTF_8), RawType.TXT);
    }

    /**
     * 测试!!!
     *
     * @return a {@link java.lang.String} object
     */
    @ScxMapping(value = "/md5", method = HttpMethod.GET)
    public String TestMd5() {
        return DigestUtils.md5("123");
    }

    /**
     * 测试!!!
     *
     * @return a {@link java.lang.String} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public String getRandomCode() {
        return RandomUtils.getRandomString(9999, true);
    }

    /**
     * 测试!!!
     *
     * @return a {@link BaseVo} object
     * @throws java.lang.Exception if any.
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo bigJson() throws Exception {
        var users = userService.list();
        return Json.ok().put("items", users);
    }

    /**
     * 测试!!!
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo a() {
        return Json.ok().put("items", "a");
    }

    /**
     * 测试!!!
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(value = "a", method = HttpMethod.GET)
    public BaseVo b() {
        return Json.ok().put("items", "b");
    }

    /**
     * 测试!!!
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(value = "/v/:aaa", method = HttpMethod.GET)
    public BaseVo c() {
        return Json.ok().put("items", "b");
    }

    /**
     * 测试!!!
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(value = "/v/:bbb", method = HttpMethod.GET)
    public BaseVo d() {
        return Json.ok().put("items", "b");
    }

    /**
     * 测试!!!
     *
     * @return a {@link BaseVo} object
     * @throws java.lang.Exception if any.
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo testSelectJson() throws Exception {

        var count = carService.count();
        if (count < 100) {
            var list = new ArrayList<Car>();
            for (int i = 0; i < 100; i++) {
                Car car = new Car();
                car.name = "小汽车" + i;
                car.tags = List.of("tag" + i, "tag" + (i + 1));
                list.add(car);
            }
            carService.save(list);
        }

        var q = new Query();
        //方法1
        q.jsonContains("tags", "tag97,tag98");
        //方法2
//        var s = new HashSet<String>();
//        s.add("tag97");
//        q.jsonContains("tags", s);
        //方法3
//        q.jsonContains("tags", new String[]{"tag97"});
//        q.in("id", new User());
        var carList = carService.list(q);

        return Json.ok().put("items", carList);
    }


    /**
     * <p>excel.</p>
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo excel() throws IOException {
        Excel excel = Excel.get07Excel("测试1", 1000);
        for (int i = 0; i < 999; i++) {
            for (int j = 0; j < 99; j++) {
                excel.setCellValue(i, j, "测试数据" + i + "-" + j);
            }
        }
        excel.mergedRegion(2, 2, 1, 10);
        excel.setCellValue(2, 2, "这是合并的单元格");

        return new Download(excel.toBytes(), "测试 Excel 😎👀😃✨😜.xlsx");
    }

    /**
     * <p>qrcode.</p>
     *
     * @param value a {@link java.lang.String} object
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo qrcode(@FromQuery String value) {
        if (value == null || "".equals(value)) {
            value = RandomUtils.getUUID() + " : 前面的是UUID";
        }
        //这里返回的是一个 png 的 图片 byte 数组
        byte[] qrCode = QRCodeUtils.getQRCode(value, 300);

        return new Raw(qrCode, RawType.PNG);
    }

    /**
     * <p>qrcode.</p>
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo zip() throws Exception {
        var virtualDirectory = VirtualDirectory.of("第一个目录");
        virtualDirectory.put("第二个目录", VirtualFile.of("第二个目录中的文件.txt", "文件内容".getBytes(StandardCharsets.UTF_8)));
        virtualDirectory.getOrCreate("这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录");
        IVirtualFile orCreate = virtualDirectory.getOrCreate("这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录");
        if (orCreate instanceof VirtualDirectory a) {
            a.put(VirtualFile.of("一个二维码图片.png", QRCodeUtils.getQRCode("一个二维码图片", 300)));
        }
        byte[] bytes = ZipAction.toZipFileByteArray(virtualDirectory);
        return new Download(bytes, "测试压缩包.zip");
    }

}
