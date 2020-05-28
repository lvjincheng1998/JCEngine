# JCEngine
### 一个基于（实体关联、远程调用）的 Java 游戏服务端框架。目前提供 TypeScript 语言的客户端 SDK ，在 Cocos Creator 中测试成功。

## 设计思路
> 1、当客户端成功连接服务端时，客户端和服务端分别生成关联实体A和B，A可以远程调用B的方法，B也可以远程调用A的方法。
远程调用API书写格式为：call(方法名, 参数1, 参数2, ......)

> 2、进一步拓展，A除了可以调用B的方法外，还可以调用服务端中其他类的方法，这些类注解为Controller类。
远程调用API书写格式为：call(方法名, [参数1, 参数1, ......], 回调处理函数)

> 3、因为A、B实体都保存了用户信息，当A请求调用Controller类的方法时，中间由B做中转，所以Controller类的方法被调用时，能得知调用者的信息，这样有助于身份识别。 

---
#### 服务端使用说明
1. 把导出的 jar 包引入服务端项目。
2. 编写一个继承 JCEntity 的 Player 类。
```
public class Player extends JCEntity {
	
	@Override
	public void onLoad() {
		System.out.println("与客户端建立连接，自动创建一个Player实体！");
		
		//远程调用客户端Player实体的内部方法
		this.call("clientMethod", "张三", 18);
	}
	
	@Override
	public void onDestroy() {
		System.out.println("与客户端断开连接，自动销毁一个Player实体！");
	}
	
	public void serverMethod(String nickName, Integer age) {
		String msg = "该方法被客户端远程调用，打印hello信息：\t"
			+ "昵称：" + nickName + "\t" 
			+ "年龄：" + age;
 		System.out.println(msg);
	}
}
```
3. 在 Main 函数内启动 JCEngine 。

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

        //远程调用服务端Player实体的内部方法
        this.call("serverMethod", ["李四", 20]);
    }

    onDestroy() {
        console.log("与服务端断开连接，自动销毁一个Player实体！");
    }

    clientMethod(nickName: string, age: number) {
		let msg:string = "该方法被服务端远程调用，打印hello信息：\t"
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

#### 服务端@Controller注解用法
用@Controller注解的类，其内部方法都可以被客户端远程调用。
```
package test;

import pers.jc.mvc.Controller;

@Controller
public class TestController {
	
	//可以在方法的任意参数位置插入JCEntity类型的参数，这样有助于身份识别，而且该参数不需要手动传入。    
	public String testMethod(Player player, String a, int b) {
	    System.out.println(player.id);
		String res = a + b;
		return res;
	}
}

```
@Controller注解类需要被扫描才会生效，可以在服务启动前进行扫描。

```
//扫描test包
JCEngine.scanPackage("test");
//服务启动
JCEngine.boot("ws://127.0.0.1:9888/jce", Player);
```

#### 客户端调用@Contoler注解类的方法

```
export default class Player extends JCEntity {

    onLoad() {
        //远程调用服务端@Contoler注解类的内部方法
        this.call("TestController.testMethod", ["hello", 123], (res) => {
            //输出方法返回结果, 如果是void方法则输出null
            console.log(res);
        });
    }
}
```

---
## 重要 API 说明
#### 服务端的 JCEntity 

可用属性 | 补充说明
---|---
id | 自动生成的唯一标识。
isValid | 该实体是否有效。
channel | 可写入数据和关闭连接。

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
channel | 可写入数据和关闭连接。

可用方法 | 补充说明
---|---
call | 用于远程调用服务端方法。
onLoad | 该实体被创建时自动调用。
onDestroy | 该实体被销毁时自动调用。

#### 补充说明
1. 服务端的 JCEntity的自定义方法的参数类型只允许使用 Integer、Long、Double、Boolean、String、JSONObject、JSONArray。
2. 客户端的 JCEntity的自定义方法的参数类型可以使用任意类型，包括 Array 和 JSONObject 。
3. 服务端的@Controller注解类的自定义方法的参数类型可以使用int、Integer、long、Long、double、Double、float、Float、boolean、Boolean、String、JSONObject、JSONArray、Java对象、数组(上述类型均可)、各种Java常用集合类。方法返回类型支持上述类型或void。
4. 调用JCEntity内部方法不支持回调，调用@Controller注解类的内部方法成功后会发起回调。


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















