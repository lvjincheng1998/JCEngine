export class JCEngine {
    public static entityClass:new()=>JCEntity;
    public static entity:JCEntity;

    public static bootQuickly(ip:string, entityClass:new()=>JCEntity){
        JCEngine.boot("ws://" + ip + ":9888/jce", entityClass);
    }

    public static boot(url:string, entityClass:new()=>JCEntity) {
        JCEngine.entityClass = entityClass;
        WebSocketServer.run(url);
    }
}
export class JCEntity {
    public id:number;
    public isValid:boolean;

    public onLoad() {}

    public onDestroy() {}

    public init(id:number) {
        this.id = id;
        this.onLoad();
    }

    public call(funcName:string, args?:any[]) {
        if(this.isValid){
            let jcData = {funcName: funcName, args: undefined};
            if (args === undefined) {
                jcData.args = [];
            } else {
                jcData.args = args;
            }
            WebSocketServer.send(JSON.stringify(jcData));
        }
    }

    public invoke(msg:string) {
        if(this.isValid){
            let jcData:JCData = JSON.parse(msg);
            this[jcData.funcName].apply(this, jcData.args); 
        }
    }
}
class WebSocketServer {
    public static webSocket:WebSocket;

    public static run(url:string) {
        if(WebSocketServer.webSocket && WebSocketServer.webSocket.OPEN){
            WebSocketServer.webSocket.close();
        }
        WebSocketServer.webSocket = new WebSocket(url);

        WebSocketServer.webSocket.onopen = () => {
            JCEngine.entity = new JCEngine.entityClass();
            JCEngine.entity.isValid = true;
            JCEngine.entity.call("init");
        }

        WebSocketServer.webSocket.onclose = () => {
            JCEngine.entity.isValid = false;
            JCEngine.entity.onDestroy();    
            JCEngine.entity = undefined;
        }

        WebSocketServer.webSocket.onmessage = (event:MessageEvent) => {
            JCEngine.entity.invoke(event.data);
        }
    }

    public static send(msg:string) {
        WebSocketServer.webSocket.send(msg);
    }
}
interface JCData {
    funcName:string;
    args:any[];
}
declare global {
    interface Window{
        JCEngine:JCEngine
    }
}
window.JCEngine = JCEngine;