import entity.Player;
import entity.table.Prop;
import pers.jc.sql.CURD;
import pers.jc.sql.SQL;
import java.util.HashMap;

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

    public static int addProp(Player player, int prop_id) {
        Prop prop = new Prop();
        prop.setPlayer_id(player.id);
        prop.setProp_id(prop_id);
        DataBase.curd.insertAndGenerateKeys(prop);
        return prop.getAuto_id();
    }

    public static int selectPropCount(Player player, int prop_id) {
        Prop prop = DataBase.curd.selectOne(Prop.class, new SQL(){{
            WHERE("player_id = " + PARAM(player.id));
            WHERE("prop_id = " + PARAM(prop_id));
        }});
        if (prop != null) {
            return prop.getProp_count();
        }
        return -1;
    }
}
