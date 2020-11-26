import entity.Player;
import pers.jc.engine.JCEngine;

/**
 * 启动类
 */
public class Boot {

    public static void main(String[] args) {
        //数据库初始化
        DataBase.init();;
        //扫描并注册指定包的组件
        JCEngine.scanPackage("component");
        //启动游戏引擎
        JCEngine.boot(9831, "/JCEngineDemo", Player.class);
    }
}
