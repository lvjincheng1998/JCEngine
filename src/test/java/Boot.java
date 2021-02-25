import entity.Player;
import pers.jc.engine.JCEngine;
import pers.jc.sql.DataView;

/**
 * 启动类
 */
public class Boot {

    public static void main(String[] args) {
        //数据库初始化
        DataBase.init();
        //启动数据视图（类似后台管理）
        DataView.scanPackage("entity");
        DataView.setCURD(DataBase.curd);
        DataView.enable();
        //扫描并注册指定包的组件
        JCEngine.scanPackage("component");
        //启动游戏引擎
        JCEngine.boot(9831, "/JCEngineDemo", Player.class);
    }
}