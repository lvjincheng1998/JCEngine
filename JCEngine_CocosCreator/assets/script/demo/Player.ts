import { JCEngineCore } from "../lib/JCEngine";
import TestComp from "./TestComp";

export default class Player extends JCEngineCore.JCEntity {
    public static ins;

    public onLoad(): void {
        Player.ins = this;
        console.log("onLoad", "与服务端建立连接");

        //注册自定义组件，里面的函数可被服务端调用
        this.components.set("TestComp", TestComp.ins);
    }

    public onDestroy(): void {
        console.log("onDestroy", "与服务端断开连接");
        console.log("reboot", "发起重连");
        JCEngineCore.JCEngine.reboot(this);
    }

    public onReload(): void {
        console.log("onReload", "与服务端再次建立连接（断线重连）");
    }

    public onMiss(): void {
        console.log("onMiss", "与服务端无法建立连接，请检查网络或服务器");
        console.log("reboot", "发起重连");
        JCEngineCore.JCEngine.reboot(this);
    }

    //自定义函数（可被后端调用）
    public showMessage1(arg0) {
        console.log("showMessage1", arg0);
    }

    //自定义函数（可被后端调用）
    public showMessage2(arg0, arg1) {
        console.log("showMessage2", arg0, arg1);
    }
}
