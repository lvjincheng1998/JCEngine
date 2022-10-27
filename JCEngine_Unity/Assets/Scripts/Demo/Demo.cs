using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Newtonsoft.Json.Linq;

public class Demo : MonoBehaviour
{
    [SerializeField] RectTransform panel_left = null;

    void Awake()
    {
        JCEngineCore.JCEngine.boot("ws://127.0.0.1:9831/JCEngine-demo", new Player());
    }

    void Start()
    {
        this.SetOnClick(this.panel_left, 0, "hello", (funcName) => {
            return Player.ins.call(funcName, 100, DateTime.Now.Millisecond, 1.1, 1.2345678999, true, "heihei");
        });
        this.SetOnClick(this.panel_left, 1, "testComp.test1", (funcName) => {
            Action<string, int> cb = (string a, int b) => {
                Debug.LogWarning($"收到{funcName}回调 {a} {b}");
            };
            JObject jo = new JObject();
            jo.Add("a", 1);
            jo.Add("b", "bb");
            return Player.ins.call(funcName, new object[]{"aihei", 111, jo, new int[]{1, 2, 3}}, cb);
        });
        this.SetOnClick(this.panel_left, 2, "testComp.test2", (funcName) => {
            Action<string, int> cb = (string a, int b) => {
                Debug.LogWarning($"收到{funcName}回调 {a} {b}");
            };
            JObject jo = new JObject();
            jo.Add("a", 1);
            jo.Add("b", "bb");
            return Player.ins.call(funcName, new object[]{"aihei", 111, jo, new int[]{1, 2, 3}}, cb);
        });
    }

    void SetOnClick(RectTransform panel, int childIndex, string funcName, Func<string, bool> callback) {
        if (panel.childCount <= childIndex) {
            Instantiate(panel.GetChild(0), panel);
        }
        panel.GetChild(childIndex).GetComponentInChildren<Text>().text = funcName;
        panel.GetChild(childIndex).GetComponent<Button>().onClick.AddListener(() => {
            bool sendRes = callback(funcName);
            if (sendRes) {
                Debug.Log($"调用{funcName}成功");
            } else {
                Debug.LogWarning($"调用{funcName}失败");
            }
        });
    }
}
