# JCEngine
### 一个基于（实体关联、远程调用）的Java游戏服务器框架！

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
		this.call("sayHelloByServer", "吕某某", "21");
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
        this.call("sayHelloByClient", ["吕某某", 21]);
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
## API 说明
#### 服务端的 JCEntity 

可用属性 | 补充说明
---|---
id | 自动生成的唯一标识。
isValid | 该实体是否有效。

可用方法 | 不同说明
---|---
call | 用于远程调用客户端方法。
onLoad | 该实体被创建时自动调用。
onDestroy | 该实体被销毁时自动调用。

#### 客户端的 JCEntity 

可用属性 | 补充说明
---|---
id | 自动生成的唯一标识。
isValid | 该实体是否有效。

可用方法 | 不同说明
---|---
call | 用于远程调用服务端方法。
onLoad | 该实体被创建时自动调用。
onDestroy | 该实体被销毁时自动调用。

---
## 重要说明
1. 服务端的 JCEntity 的自定义方法的参数类型只允许使用 Integer, Long, Boolean, String。
2. 客户端的 JCEntity 的自定义方法的参数类型可以使用任意类型，包括 Array 和 JSONObject 。










