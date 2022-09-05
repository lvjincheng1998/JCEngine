import { JCEngine } from "../lib/JCEngine";
import Player from "./Player";

const {ccclass, property} = cc._decorator;

@ccclass
export default class MainView extends cc.Component {

    protected onLoad(): void {
        JCEngine.boot("ws://127.0.0.1:9831/JCEngine-demo", new Player());

        let panel_left = this.node.getChildByName("Panel-Left");
        this.setOnClick(panel_left, 0, "hello", () => {
            Player.ins.call("hello", [100, Date.now(), 1.1, 1.2345678999, true, "heihei"]);
        });
        this.setOnClick(panel_left, 1, "testComp.test1", () => {
            Player.ins.call("testComp.test1", ["aihei", 111, {a: 1, b: "bb"}, [1, 2, 3]], (a, b) => {
                console.log("testComp.test1-callback", a, b);
            });
        });
        this.setOnClick(panel_left, 2, "testComp.test2", () => {
            Player.ins.call("testComp.test2", [], (a, b) => {
                console.log("testComp.test2-callback", a, b);
            });
        });
        this.setOnClick(panel_left, 3, "testComp.test3", () => {
            Player.ins.call("testComp.test3");
        });
    }
    
    setOnClick(panel: cc.Node, childIndex: number, labelName: string, callback: Function) {
        if (!panel.children[childIndex]) {
            let item = cc.instantiate(panel.children[0]);
            item.off(cc.Node.EventType.TOUCH_END);
            panel.addChild(item);
        }
        panel.children[childIndex].getComponentInChildren(cc.Label).string = labelName;
        panel.children[childIndex].on(cc.Node.EventType.TOUCH_END, callback);
    }
}
