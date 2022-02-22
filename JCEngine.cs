using System;
using System.Collections.Generic;
using BestHTTP.WebSocket;
using Newtonsoft.Json;

public class JCEngine {
    public static string url;
    public static Type entityClass;

    public static void boot(string url, Type entityClass) {
        JCEngine.url = url;
        JCEngine.entityClass = entityClass;
        new JCEngineCore.WebSocketServer(url, null);
    }

    public static void reBoot(JCEntity entity) {
        new JCEngineCore.WebSocketServer(url, entity);
    }
}

public class JCEntity {
    public int id;
    public JCEngineCore.Channel channel;
    public bool isValid;
    public bool loaded;
    public Dictionary<string, object> components = new Dictionary<string, object>();

    public virtual void onLoad() {}

    public virtual void onReload() {}

    public virtual void onDestroy() {}

    public virtual void onMiss() {}

    public bool call(string func, params object[] args) {
        return call(func, args, null);
    }

    public bool call(string func, object[] args = null, Delegate callback = null) {
        if (this.isValid) {
            string uuid = "";
            int type = Convert.ToInt32(JCEngineCore.DataType.FUNCTION);
            if (func.IndexOf(".") > -1) {
                type = Convert.ToInt32(JCEngineCore.DataType.METHOD);
                uuid = JCEngineCore.CallbackHandler.addCallback(callback);
            }
            if (args == null) {
                args = new object[]{};
            }
            JCEngineCore.Data data = new JCEngineCore.Data();
            data.uuid = uuid;
            data.type = type;
            data.func = func;
            data.args = args;
            this.channel.writeAndFlush(JsonConvert.SerializeObject(data));
            return true;
        }
        return false;
    }
}

namespace JCEngineCore {
    
    public class Channel {
        private WebSocket webSocket;

        public Channel (WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        public void writeAndFlush(string text) {
            this.webSocket.Send(text);
        }

        public void close() {
            this.webSocket.Close();
        }
    }
    class WebSocketServer {
        private WebSocket webSocket;
        private JCEntity tempEntity;
    
        public WebSocketServer(string url, JCEntity entity) {
            this.webSocket = new WebSocket(new Uri(url));
            this.webSocket.StartPingThread = true;
            this.tempEntity = entity != null ? entity : JCEngine.entityClass.Assembly.CreateInstance(JCEngine.entityClass.FullName) as JCEntity;

            this.webSocket.OnOpen += delegate(WebSocket webSocket) {
                this.call("loadTempEntity");
            };
    
            this.webSocket.OnClosed = delegate(WebSocket webSocket, UInt16 code, string message) {
                this.destroyTempEntity();
            };

            this.webSocket.OnError = delegate(WebSocket webSocket, Exception ex) {
                this.destroyTempEntity();
            };
    
            this.webSocket.OnMessage = delegate(WebSocket webSocket, string message) {
                this.invoke(JsonConvert.DeserializeObject<Data>(message));            
            };

            this.webSocket.Open();
        }
    
        private void call(string func, object[] args = null) {
            if (args == null) {
                args = new object[]{};
            }
            Data data = new Data();
            data.uuid = "";
            data.type = Convert.ToInt32(DataType.EVENT);
            data.func = func;
            data.args = args;
            this.webSocket.Send(JsonConvert.SerializeObject(data));
        }
    
        private void invoke(Data data) {
            DataType dataType = (DataType) data.type;
            if (dataType == DataType.EVENT) {
                System.Reflection.MethodInfo method = this.GetType().GetMethod(data.func);
                Utility.formatArgsType(data.args, method.GetParameters());
                method.Invoke(this, data.args);
                return;
            }
            if (dataType == DataType.FUNCTION) {
                if (this.tempEntity.isValid) {
                    string func = data.func;
                    object context = this.tempEntity;;
                    int pointIndex = func.LastIndexOf(".");
                    if (pointIndex > -1) {
                        context = null;
                        string key = func.Substring(0, pointIndex);
                        object matchContext;
                        this.tempEntity.components.TryGetValue(key, out matchContext);
                        if (matchContext != null) {
                            string[] arr = func.Split(new char[]{'.'});
                            func = arr[arr.Length - 1];
                            context = matchContext;
                        }
                    }
                    if (context != null) {
                        System.Reflection.MethodInfo method = context.GetType().GetMethod(func);
                        Utility.formatArgsType(data.args, method.GetParameters());
                        method.Invoke(context, data.args);
                    }
                }
                return;
            }
            if (dataType == DataType.METHOD) {
                CallbackHandler.handleCallback(data);
            }
        }
    
        public void loadTempEntity(int id) {
            this.tempEntity.id = id;
            this.tempEntity.channel = new Channel(this.webSocket);
            this.tempEntity.isValid = true;
            try {
                if (this.tempEntity.loaded) {
                    this.tempEntity.onReload();
                } else {
                    this.tempEntity.onLoad();
                }
            } catch (Exception) {}
            this.tempEntity.loaded = true;
        }
    
        public void destroyTempEntity() {
            if (this.tempEntity.isValid) {
                this.tempEntity.isValid = false;
                this.tempEntity.onDestroy();            
            } else {
                this.tempEntity.onMiss();
            }
        }
    }

    class CallbackHandler {
        private static int nextID = 0;
        private static Dictionary<string, CallbackInfo> mapper = new Dictionary<string, CallbackInfo>();
    
        private static string uuid() {
            nextID++;
            return nextID.ToString();
        }
    
        public static string addCallback(Delegate callback) {
            string uuid = CallbackHandler.uuid();
            if (callback != null) {
                CallbackInfo callbackInfo = new CallbackInfo();
                callbackInfo.callback = callback;
                callbackInfo.deadTime = Utility.GetTimestamp() + 10 * 1000;
                mapper.Add(uuid, callbackInfo);
            }
            return uuid;
        }
    
        public static void handleCallback(Data data) {
            if (mapper.Count > 10) {
                long now = Utility.GetTimestamp();
                LinkedList<string> outKeys = new LinkedList<string>();
                foreach (var item in mapper) {
                    if (now >= item.Value.deadTime) {
                        outKeys.AddLast(item.Key);
                    }
                }
                foreach (var item in outKeys) {
                    mapper.Remove(item);
                }
            }
            CallbackInfo callbackInfo;
            mapper.TryGetValue(data.uuid, out callbackInfo);
            if (callbackInfo != null) {
                mapper.Remove(data.uuid);
                object target = callbackInfo.callback.Target;
                System.Reflection.MethodInfo method = callbackInfo.callback.Method;
                Utility.formatArgsType(data.args, method.GetParameters());
                method.Invoke(target, data.args);
            }
        }
    }

    class Utility {
        public static long GetTimestamp() {
            TimeSpan ts = DateTime.Now.ToUniversalTime() - new DateTime(1970, 1, 1);
            return (long)ts.TotalMilliseconds;
        }
        public static void formatArgsType(object[] args, System.Reflection.ParameterInfo[] parameters) {
            int i = 0;
            foreach (var param in parameters) {
                args[i] = Convert.ChangeType(args[i], param.ParameterType);
                i++;
            }
        }
    }

    class CallbackInfo {
        public Delegate callback;
        public long deadTime;
    }
    
    class Data {
        public string uuid;
        public int type;
        public string func;
        public object[] args;
    }
    
    enum DataType {
        EVENT,
        FUNCTION,
        METHOD
    }
}
