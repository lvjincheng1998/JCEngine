package demo.sql.table;

import pers.jc.sql.AutoIncrement;
import pers.jc.sql.Column;
import pers.jc.sql.Id;
import pers.jc.sql.Table;

@Table("user_info")
public class UserInfo {
    @Id
    @AutoIncrement
    private int userID;
    @Column
    private String nickname;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
