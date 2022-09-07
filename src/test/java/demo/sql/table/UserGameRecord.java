package demo.sql.table;

import pers.jc.sql.Column;
import pers.jc.sql.Id;
import pers.jc.sql.Table;

import java.sql.Timestamp;

@Table("user_game_record")
public class UserGameRecord {
    @Id
    private int userID;
    @Column
    private Timestamp dateTime;
    @Column
    private int gameType;
    @Column
    private int duration;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
