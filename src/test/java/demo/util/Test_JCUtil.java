package demo.util;

import pers.jc.util.JCUtil;

public class Test_JCUtil {

    public static void main(String[] args) {
        String uuid = JCUtil.uuid();
        System.out.println("uuid=" + uuid + ", len=" + uuid.length());
    }
}
