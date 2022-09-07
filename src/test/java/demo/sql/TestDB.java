package demo.sql;

import com.alibaba.fastjson.JSON;
import demo.sql.table.UserInfo;
import pers.jc.sql.CURD;
import pers.jc.sql.SQL;
import pers.jc.util.JCLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDB {

    public static void main(String[] args) throws Exception {
        /**
         * 在测试前需要先把数据库准备好
         * 1. 先用/src/test/resources/sql/create_db.sql导入数据库
         * 2. 再用/src/test/resources/sql/test_jc_engine.sql创建表格
         * 3. 检查以下数据库config是否正确。
         */
        Map<String, Object> config = new HashMap<>();
        config.put("host", "127.0.0.1");
        config.put("username", "root");
        config.put("password", "123456");
        config.put("database", "test_jc_engine");
        CURD curd = new CURD(config);

        testInsertUpdateSelectDelete(curd);
    }

    /**增删改查测试 */
    public static void testInsertUpdateSelectDelete(CURD curd) {
        List<String> userIDList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setNickname("玩家");
            if (curd.insertAndGenerateKeys(userInfo) == 1) {
                JCLogger.info("插入用户数据成功", "自增id=" + userInfo.getUserID());
                userInfo.setNickname(userInfo.getNickname() + userInfo.getUserID());
                if (curd.update(userInfo) == 1) {
                    JCLogger.info("更新用户数据成功", JSON.toJSONString(userInfo));
                    userIDList.add(String.valueOf(userInfo.getUserID()));
                }
            }
        }

        List<UserInfo> userInfoList = null;
        if (userIDList.size() > 0) {
            userInfoList = curd.select(UserInfo.class, new SQL(){{
                WHERE("userID in (" + String.join(",", userIDList) + ")");
            }});
            if (userInfoList != null) {
                JCLogger.info("读取用户数据成功");
                userInfoList.forEach(userInfo -> System.out.println(JSON.toJSONString(userInfo)));
            }
        }

        if (userInfoList != null && userInfoList.size() > 0) {
            int delCount = curd.delete(userInfoList.toArray());
            JCLogger.info("删除用户数据，删除数量=", delCount);
        }


        int delCount = curd.delete(new SQL(){{
            DELETE_FROM(UserInfo.class);
        }});
        JCLogger.info("删除全部用户数据，删除数量=", delCount);

        curd.update("alter table `user_info` AUTO_INCREMENT=0;");
        JCLogger.info("数据库的自增ID已重置");
    }
}
