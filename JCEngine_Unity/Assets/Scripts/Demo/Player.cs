using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Player : JCEngineCore.JCEntity
{
    public static Player ins;

    public override void onLoad() {
        Player.ins = this;
        Debug.Log("onLoad 与服务端建立连接");

        //注册自定义组件，里面的函数可被服务端调用
        this.components.Add("TestComp", TestComp.ins);
    }
 
    public override void onDestroy() {
        Debug.Log("onDestroy 与服务端断开连接");
        Debug.Log("reboot 发起重连");
        JC.Unity.CoroutineStarter.Start(ReconnenctServer());
    }
 
    public override void onReload() {
        Debug.Log("onReload 与服务端再次建立连接（断线重连）");
    }
 
    public override void onMiss() {
        Debug.Log("onMiss 与服务端无法建立连接，请检查网络或服务器");
        Debug.Log("reboot 发起重连");
        JC.Unity.CoroutineStarter.Start(ReconnenctServer());
    }

    //之所以做成协程延迟触发，是因为用编辑器调试时，停止运行后会触发断线重连，就会造成游戏停止调试了，但socket还连接的现象。
    IEnumerator ReconnenctServer() {
        yield return new WaitForSecondsRealtime(0.1f);
        JCEngineCore.JCEngine.reboot(this);
    }
     
    //自定义函数（可被后端调用）
    public void showMessage1(string arg0) {
        Debug.Log("showMessage1 " + arg0);
    }
 
    //自定义函数（可被后端调用）
    public void showMessage2(string arg0, int arg1) {
        Debug.Log("showMessage2 " + arg0 + " " + arg1);
    }
}
