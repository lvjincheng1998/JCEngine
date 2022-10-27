using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace JC.Unity {
    // 协程启动器
    public class CoroutineStarter : MonoBehaviour
    {

        private static CoroutineStarter _instance;

        private static CoroutineStarter Instance {
            get {
                if ( _instance == null ) {
                    GameObject obj = new GameObject("CoroutineStarter");
                    obj.AddComponent<CoroutineStarter>();
                }
                return _instance;
            }
        }

        void Awake() {
            _instance = this;
            DontDestroyOnLoad(gameObject);
        }

        /// <summary>
        /// 开启协程
        /// </summary>
        /// <param name="enumerator"></param>
        public static Coroutine Start(IEnumerator enumerator) {
            return Instance.StartCoroutine(enumerator);
        }

        public static void Stop(Coroutine coroutine) {
            Instance.StopCoroutine(coroutine);
        }
    }
}