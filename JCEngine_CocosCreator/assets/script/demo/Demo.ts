import { JCEngineCore } from "../lib/JCEngine";
import Player from "./Player";

const {ccclass, property} = cc._decorator;

@ccclass
export default class Demo extends cc.Component {
    @property(cc.Node)
    panel_left: cc.Node = null;

    protected onLoad(): void {
        JCEngineCore.JCEngine.boot("ws://127.0.0.1:9831/JCEngine-demo", new Player());
    }

    protected start(): void {
        this.setOnClick(this.panel_left, 0, "hello", (funcName) => {
            return Player.ins.call(funcName, [100, Date.now(), 1.1, 1.2345678999, true, "heihei"]);
        });
        this.setOnClick(this.panel_left, 1, "testComp.test1", (funcName) => {
            return Player.ins.call(funcName, ["aihei", 111, {a: 1, b: "bb"}, [1, 2, 3]], (a, b) => {
                console.warn(`收到${funcName}回调`, a, b);
            });
        });
        this.setOnClick(this.panel_left, 2, "testComp.test2", (funcName) => {
            return Player.ins.call(funcName, ["aihei", 111, {a: 1, b: "bb"}, [1, 2, 3]], (a, b) => {
                console.warn(`收到${funcName}回调`, a, b);
            });
        });
    }
    
    setOnClick(panel: cc.Node, childIndex: number, funcName: string, callback: (funcName: string) => boolean) {
        if (!panel.children[childIndex]) {
            let item = cc.instantiate(panel.children[0]);
            item.off(cc.Node.EventType.TOUCH_END);
            panel.addChild(item);
        }
        panel.children[childIndex].getComponentInChildren(cc.Label).string = funcName;
        panel.children[childIndex].on(cc.Node.EventType.TOUCH_END, () => {
            let sendRes = callback(funcName);
            if (sendRes) {
                console.log(`调用${funcName}成功`);
            } else {
                console.error(`调用${funcName}失败`);
            }
        });
    }
}
