# JCEngine
### 一个基于（实体关联、远程调用）的 Java 游戏服务端框架。目前提供 TypeScript 语言的客户端 SDK 。在 Cocos Creator 中测试成功。

## 设计思路
> 当客户端成功连接服务端时，客户端和服务端同时各创建一个实体，用 A 和 B 表示，它们之间互相关联，A 可以远程调用 B 的方法，同样 B 也可以远程调用 A 的方法。 

---
#### 服务端使用说明
1. 把项目工程打包成 runnable jar 。
2. 把 jar 包引入服务端项目。
3. 编写一个继承 JCEntity 的 Player 类。
```
public class Player extends JCEntity {
	
	@Override
	public void onLoad() {
		System.out.println("与客户端建立连接，自动创建一个Player实体！");
		
		//远程调用客户端方法
		this.call("sayHelloByServer", "张三", 18);
	}
	
	@Override
	public void onDestroy() {
		System.out.println("与客户端断开连接，自动销毁一个Player实体！");
	}
	
	public void sayHelloByClient(String nickName, Integer age) {
		String msg = "收到来自客户端的hello信息：\t"
			+ "昵称：" + nickName + "\t" 
			+ "年龄：" + age;
 		System.out.println(msg);
	}
}
```
4. 在 Main 函数内启动 JCEngine 。

```
public static void main(String[] args) throws Exception {
        //启动服务，通过 ws://127.0.0.1:9888/jce 进行访问。
        JCEngine.boot(9888, "/jce", Player.class);
    }
```

---
#### 客户端使用说明
1. 该例子在 Cocos Creator 中测试。
2. 把 JCEngine.ts 引入客户端项目。
3. 编写一个继承 JCEntity 的 Player 类。

```
export default class Player extends JCEntity {

    onLoad() {
        console.log("与服务端建立连接，自动创建一个Player实体！");

        //远程调用服务端方法
        this.call("sayHelloByClient", ["李四", 20]);
    }

    onDestroy() {
        console.log("与服务端断开连接，自动销毁一个Player实体！");
    }

    sayHelloByServer(nickName: string, age: number) {
		let msg:string = "收到来自服务端的hello信息：\t"
			+ "昵称：" + nickName + "\t" 
			+ "年龄：" + age;
 		console.log(msg);
	}
}
```
4. 启动 JCEngine 。

```
//启动服务，对 ws://127.0.0.1:9888/jce 进行访问。
JCEngine.boot("ws://127.0.0.1:9888/jce", Player);
```

---
## 重要 API 说明
#### 服务端的 JCEntity 

可用属性 | 补充说明
---|---
id | 自动生成的唯一标识。
isValid | 该实体是否有效。

可用方法 | 补充说明
---|---
call | 用于远程调用客户端方法。
onLoad | 该实体被创建时自动调用。
onDestroy | 该实体被销毁时自动调用。

#### 客户端的 JCEntity 

可用属性 | 补充说明
---|---
id | 自动生成的唯一标识。
isValid | 该实体是否有效。

可用方法 | 补充说明
---|---
call | 用于远程调用服务端方法。
onLoad | 该实体被创建时自动调用。
onDestroy | 该实体被销毁时自动调用。

#### 补充说明
1. 服务端的 JCEntity 的自定义方法的参数类型只允许使用 Integer, Long, Boolean, String。
2. 客户端的 JCEntity 的自定义方法的参数类型可以使用任意类型，包括 Array 和 JSONObject 。

## 其它 API 说明
- 日志系统 JCLogger

```
//日记等级排列：DEBUG < INFO < WARN < ERROR
//日志等级默认为：INFO
JCLogger.setLevel(JCLogger.LEVEL_DEBUG);
JCLogger.debug("debug");
JCLogger.info("info");
JCLogger.warn("warn");
JCLogger.error("error");
```

- 定时器 JCInterval

```
//开启一个的定时器
public static int time = 3;
//每秒执行一次
new JCInterval(1) {
	
	@Override
	public void run() {
		//倒计时一次
		time--;
		if (time == 0) {
			//取消定时器
			cancel();
		}
	}
};
```

- 延时器 JCTimeout 

```
//开启一个延时器
//3秒后执行
JCTimeout timer = new JCTimeout(3) {
	
	@Override
	public void run() {
		System.out.println("延时输出 ");
	}
};
//取消延时器
timer.cancel();
```

- 省略......















