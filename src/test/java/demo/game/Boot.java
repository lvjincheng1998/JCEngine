package demo.game;

import pers.jc.engine.JCEngine;

public class Boot {

    public static void main(String[] args) {
        JCEngine.scanPackage("demo.game.component");
        JCEngine.boot(9831, "/JCEngine-demo", Player.class);
    }
}
