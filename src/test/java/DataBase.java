import com.alibaba.fastjson.JSONObject;
import entity.table.Prop;
import pers.jc.sql.CURD;
import pers.jc.sql.SQL;
import java.util.HashMap;
import java.util.List;

public class DataBase {
    public static CURD curd;

    public static void init() {
        HashMap<String, Object> config = new HashMap<>();
        config.put("host", "127.0.0.1");
        config.put("port", 3306);
        config.put("username", "root");
        config.put("password", "123456");
        config.put("database", "test");
        config.put("minIdle", 0);
        config.put("maxActive", 10);
        curd = new CURD(config);
    }

    public static void test() {
        //1.插入操作
        Prop prop = new Prop();
        prop.setPlayer_id(10);
        prop.setProp_id(100);
        DataBase.curd.insertAndGenerateKeys(prop);
        System.out.println(prop.getAuto_id());//打印插入后数据库生成的自增ID
        //2.修改操作
        prop.setProp_count(11);
        DataBase.curd.update(prop);
        //3.查询操作
        List<Prop> props = DataBase.curd.select(Prop.class, new SQL(){{
            WHERE("auto_id = " + PARAM(prop.getAuto_id()));
        }});
        System.out.println(JSONObject.toJSONString(props.get(0)));
        //4.删除操作
        DataBase.curd.delete(prop);
        //其它API自行探索吧，同时也可以自定义SQL构建，弥补ORM操作的的不足。
    }
}
