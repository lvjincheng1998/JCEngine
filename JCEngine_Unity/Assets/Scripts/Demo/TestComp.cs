using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestComp
{
   public static TestComp ins = new TestComp();
 
    //自定义函数（可被后端调用）
    public void doSomeThing(string arg0) {
        Debug.Log("doSomeThing " + arg0);
    }
}
