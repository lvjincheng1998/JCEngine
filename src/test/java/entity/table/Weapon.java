package entity.table;

import pers.jc.sql.AutoIncrement;
import pers.jc.sql.Column;
import pers.jc.sql.Id;
import pers.jc.sql.Table;

@Table(value = "weapon", title = "玩家武器数据")
public class Weapon {
    @Id(title = "自增ID")
    @AutoIncrement
    private int id;
    @Column(title = "玩家ID")
    private int player_id;
    @Column(title = "武器ID")
    private int weapon_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public int getWeapon_id() {
        return weapon_id;
    }

    public void setWeapon_id(int weapon_id) {
        this.weapon_id = weapon_id;
    }
}