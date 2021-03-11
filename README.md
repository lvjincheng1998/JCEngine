# JCEngine

### 功能特点
- 提供基于HTTP的MVC模式。
- 提供基于WebSocket的远程调用模式。
- 提供文件上传功能。
- 提供基于JDBC的ORM数据库操作。
- 自动生成基于Table对象的后台管理系统。

### 应用指南
#### 1. 使用MVC模式 (基于HTTP)

```
@HttpComponent("/httpDemo")
public class HttpDemoComp {

    @HttpGet("/test1")
    public String test1(String text) {
        JCLogger.info(text);
        return "返回内容";
    }

    @HttpPost("/test2")
    public RequestResult test2(int a, long b, float c, double d, boolean e) {
        JCLogger.info(a, b, c, d, e);
        //返回一个对象，最终序列化为JSON对象
        RequestResult requestResult = new RequestResult();
        requestResult.setCode(0);
        requestResult.setData("内容");
        requestResult.setMsg("消息");
        return requestResult;
    }
}

class RequestResult {
    private int code;
    private Object data;
    private String msg;
    // Getter 和 Setter 部分省略
}
```

- 访问 GET: 项目访问路径/httpDemo/test1?text=hello
- 访问 POST: 项目访问路径/httpDemo/test2

#### 2. 使用远程调用模式 (基于WebSocket)

##### Ⅰ. 远程方法调用 (特点：模块化、定义在组件内)

```
//服务端 Java 代码
@SocketComponent("socketDemo")
public class SocketDemoComp {

    @SocketMethod
    public String test(
        Player player, // 调用者实体对象，可选接收
        String a, int b, long c, float d, double e, boolean f, JSONObject g, JSONArray h
    ) {
        JCLogger.info(player.id, a, b, c, d, e, f, g, h);
        return "可返回任意类型数据";
    }
}

//客户端 TypeScript 代码
class Player extends JCEntity {

    onLoad() {
        //客户端调用服务端方法
        this.call(
            "socketDemo.test", //类的自定义别名+方法名 
            ["hello", 1, 2, 3.3, 4.4, true, {text:hello}, [1,2,3]], //参数对应顺序类型
            (res) => { //回调函数
                console.log("返回内容", res);
            }
        );
    }
}
```

##### Ⅱ. 远程函数调用 (特点：高效便捷、定义在实体内)

```
//服务端 Java 代码
public class Player extends JCEntity {

    @Override
    public void onLoad() {
        JCLogger.info("玩家", this.id, "登录");

        //服务端调用客户端函数
        this.call("testClient", "hello", "i am server", 233);
    }

    @Override
    public void onDestroy() {
        JCLogger.info("玩家", this.id, "退出");
    }

    @SocketFunction
    public void testServer(String a, String b, Integer c) {
        JCLogger.info(a, b, c);
    }
}

//客户端 TypeScript 代码
class Player extends JCEntity {

    onLoad() {
        //客户端调用服务端方法
        this.call("testServer", ["hello", "i am client", 233]);
    }

    testClient(a, b, c) {
        console.log(a, b, c)
    }
}
```

#### 3. 启动服务

```
//服务端 Java 代码

//1.创建一个继承JCEntity的类
public class Player extends JCEntity {
    //自定义内容省略
}

//2.在main函数中启动服务
public static void main(String[] args) {
    //如果使用了HttpComponent或SocketComponent组件,需要扫描对应包进行注册
    JCEngine.scanPackage("test.component");
    //启动服务
    JCEngine.boot(9831, "/JCEngineDemo", Player.class); 
}
```
- 项目访问路径：127.0.0.1:9831/JCEngineDemo

```
//客户端 TypeScript 代码

class Player extends JCEntity {
    ////自定义内容省略
}

JCEngine.boot("ws://127.0.0.1:9831/JCEngineDemo", Player);
```
- 客户端需要引入 JCEngine.ts 。

#### 4. 文件上传

```
@HttpComponent("/file")
public class FileController {

    //上传文件
    @HttpPost("/upload")
    public String upload(FileUpload fileUpload) {
        try {
            String catalogPath = new File("").getCanonicalPath() + File.separator + "upload";
            File catalogFile = new File(catalogPath);
            if (!catalogFile.exists()) {
                if (!catalogFile.mkdir()) {
                    throw new Exception("创建目录失败 " + catalogPath);
                }
            }
            String oldFileName = fileUpload.getFilename();
            String newFileName = JCUtil.uuid() + oldFileName.substring(oldFileName.lastIndexOf("."));
            String newFilePath = catalogPath + File.separator + newFileName;
            File newFile = new File(newFilePath);
            fileUpload.renameTo(newFile);
            JCLogger.info("保存目录", newFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "上传成功";
    }

    //访问项目下的静态资源
    @HttpGet("/getResource")
    public HttpResource getResource() {
        return new HttpResource("/project.json");
    }

    //重定向，如果前缀没有http，会自动拼接项目基础路径
    @HttpGet("/doRedirect")
    public HttpRedirect doRedirect() {
        return new HttpRedirect("http://www.baidu.com");
    }
}
```

#### 5.数据库操作
##### Ⅰ. 定义Table类

```
//数据库MySQL语句
CREATE TABLE `prop` (
  `auto_id` int NOT NULL AUTO_INCREMENT,
  `player_id` int NOT NULL,
  `prop_id` int NOT NULL,
  `prop_count` int NOT NULL,
  PRIMARY KEY (`auto_id`)
) ENGINE=InnoDB AUTO_INCREMENT=322 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

//服务端Java代码
@Table(value = "prop", title = "玩家道具数据")
public class Prop {
    @Id(title = "自增ID")
    @AutoIncrement
    private int auto_id;
    @Column(title = "玩家ID")
    private int player_id;
    @Column(title = "道具ID")
    private int prop_id;
    @Column(title = "道具数量")
    private int prop_count;
    //省略 Getter 和 Setter
}
```

##### Ⅱ. 启动服务

```
public class DataBase {
    public static CURD curd;

    public static void init() {
        HashMap<String, Object> config = new HashMap<>();
        //以下字段都有默认值，可选填
        config.put("host", "127.0.0.1");
        config.put("port", 3306);
        config.put("username", "root");
        config.put("password", "123456");
        config.put("database", "test");
        config.put("minIdle",5);
        config.put("maxActive", 20);
        config.put("clearInterval", 3000);
        curd = new CURD(config);
    }
}
```

##### Ⅲ. 增删改查

```
//1.插入操作
Prop prop = new Prop();
prop.setPlayer_id(10);
prop.setProp_id(100);
DataBase.curd.insertAndGenerateKeys(prop);
System.out.println(prop.getAuto_id());//打印插入后数据库生成的自增ID
//2.修改操作
prop.setProp_count(11);
DataBase.curd.update(prop);
//3.查询操作
List<Prop> props = DataBase.curd.select(Prop.class, new SQL(){{
    WHERE("auto_id = " + PARAM(prop.getAuto_id()));
}});
System.out.println(JSONObject.toJSONString(props.get(0)));
//4.删除操作
DataBase.curd.delete(prop);
//其它API自行探索吧，同时也可以自定义SQL构建，弥补ORM操作的的不足。
```


#### 6. 开启后台管理系统

```
//扫描自定义Table所在的包
DataView.scanPackage("test.table");
//设置数据库的操作实例
DataView.setCURD(DataBase.curd);
//设置登录账号密码
DataView.setLoginVerify("admin", "123456");
//正式启动
DataView.enable();
```