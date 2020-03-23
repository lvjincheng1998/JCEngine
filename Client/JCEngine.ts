export class JCEngine {
    public static entityClass: new() => JCEntity;

    public static boot(url: string, entityClass: new() => JCEntity) {
        JCEngine.entityClass = entityClass;
        WebSocketServer.run(url);
    }
}

export class JCEntity {
    public id: number;
    public channel: Channel;
    public isValid: boolean;

    public onLoad() {}

    public onDestroy() {}

    public call(func: string, args?: any[]) {
        if (this.isValid) {
            let data = {id: this.id, type: JCDataType.FUNCTION, func: func, args: undefined};
            if (args == undefined) {
                data.args = [];
            } else {
                data.args = args;
            }
            this.channel.writeAndFlush(JSON.stringify(data));
        }
    }
}

export class Channel {
    private webSocket: WebSocket;

    constructor(webSocket: WebSocket) {
        this.webSocket = webSocket;
    }

    public writeAndFlush(text: string) {
        this.webSocket.send(text);
    }

    public close() {
        this.webSocket.close();
    }
}

export class WebSocketServer {
    private static webSocket: WebSocket;
    private static tempEntity: JCEntity;

    public static run(url: string) {
        if (this.webSocket && this.webSocket.OPEN) {
            this.webSocket.close();
        }
        this.webSocket = new WebSocket(url);

        this.webSocket.onopen = () => {
            this.call("loadTempEntity");
        }

        this.webSocket.onclose = () => {
            this.destroyTempEntity();
        }

        this.webSocket.onmessage = (event: MessageEvent) => {
            this.invoke(JSON.parse(event.data));            
        }
    }

    private static call(func: string, args?: any[]) {
        let data:JCData = {id: -1, type: JCDataType.EVENT, func: func, args: undefined};
        if (args == undefined) {
            data.args = [];
        } else {
            data.args = args;
        }
        this.webSocket.send(JSON.stringify(data));
    }

    private static invoke(data: JCData) {
        if (data.type == JCDataType.EVENT) {
            this[data.func].apply(this, data.args);
            return;
        }
        if (data.type == JCDataType.FUNCTION) {
            if (this.tempEntity.isValid) {
                this.tempEntity[data.func].apply(this.tempEntity, data.args); 
            }
            return;
        }
    }

    public static loadTempEntity(id: number) {
        this.tempEntity = new JCEngine.entityClass();
        this.tempEntity.id = id;
        this.tempEntity.channel = new Channel(this.webSocket);
        this.tempEntity.isValid = true;
        this.tempEntity.onLoad();
    }

    public static destroyTempEntity() {
        this.tempEntity.isValid = false;
        this.tempEntity.onDestroy(); 
    }
}

export interface JCData {
    id: number;
    type: number;
    func: string;
    args: any[];
}

export enum JCDataType {
    EVENT,
    FUNCTION
}