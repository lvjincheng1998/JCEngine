package demo.util;

import pers.jc.util.JCHttp;

public class Test_JCHttp {

    public static void main(String[] args) {
        String resGet = JCHttp.get("https://www.baidu.com");
        System.out.println("===get-success===");
        System.out.println(resGet.replace("\n", ""));

        String resPost = JCHttp.post("https://www.baidu.com", "param1=value1&param2=value2");
        System.out.println("===post-success===");
        System.out.println(resPost.replace("\n", ""));
    }
}
