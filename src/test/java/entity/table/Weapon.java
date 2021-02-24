package entity.table;

import pers.jc.sql.Column;
import pers.jc.sql.Id;
import pers.jc.sql.Table;

@Table("weapon")
public class Weapon {
    @Id
    private int id;
    @Column
    private int player_id;
    @Column
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