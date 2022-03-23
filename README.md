### JCEngine

#### 特点
1. 基于Netty实现多线程通信，提高通信效率。
2. 提供单线程GameLogic，避免多线程安全问题。
3. 提供函数形式的远程调用功能，方便简洁。
4. 提供数据库MySQL的ORM操作接口。
5. 提供CocosCreator、Unity的客户端SDK。

#### 快速启动

##### Java服务端

```
//通信实体类
public class Player extends JCEntity {
    public static Set<Player> players = new HashSet<>();

    @Override
    public void onLoad() {
        players.add(this);
        JCLogger.info("一个用户登入，当前用户数量：", players.size());
        call("say", "I am ok"); //调用客户端接口
    }

    @Override
    public void onDestroy() {
        players.remove(this);
        JCLogger.info("一个用户退出，当前用户数量：", players.size());
    }

    @SocketFunction //声明该函数可以被客户端调用，还有public关键字是必须的
    public void hello(String content1, int content2, boolean content3) {
        //异步打印
        JCLogger.info("收到来自客户端的hello信息：", content1, content2, content3);
    }
}
```

```
//启动入口
public class Boot {

    public static void main(String[] args) {
        //分别为：监听端口、访问路径、指定通信实体类
        JCEngine.boot(9831, "/JCEngine", Player.class);
    }
}
```

##### CocosCreator客户端（需要引入JCEngine.ts）

```
//通信实体类
export default class Player extends JCEntity {
    public onLoad(): void { //载入监听，与服务端已建立连接
        console.log("onLoad");
        
        this.call("hello", ["haha", 233, true]); //调用服务端接口
    }
    public onDestroy(): void { //销毁监听，与服务端已断开连接
        console.log("onDestroy");
        JCEngine.reboot(this); //重新连接服务端
    }
    public onReload(): void { //重载入监听，与服务端再次建立连接（断线重连）
        console.log("onReload");
    }
    public onMiss(): void { //迷失监听，与服务端无法建立连接
        console.log("onMiss");
        JCEngine.reboot(this); //重新连接服务端
    }

    //声明一些接口供服务端调用
    public say(content: string) {
        console.log("收到来自服务端的hello信息：", content);
    }
}
```

```
//启动入口
JCEngine.boot("ws://127.0.0.1:9831/JCEngine", Player);
```
