package demo.entity.table;

import pers.jc.sql.AutoIncrement;
import pers.jc.sql.Column;
import pers.jc.sql.Id;
import pers.jc.sql.Table;

@Table("prop")
public class Prop {
    @Id
    @AutoIncrement
    private int auto_id;
    @Column
    private int player_id;
    @Column
    private int prop_id;
    @Column
    private int prop_count;

    public int getAuto_id() {
        return auto_id;
    }

    public void setAuto_id(int auto_id) {
        this.auto_id = auto_id;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public int getProp_id() {
        return prop_id;
    }

    public void setProp_id(int prop_id) {
        this.prop_id = prop_id;
    }

    public int getProp_count() {
        return prop_count;
    }

    public void setProp_count(int prop_count) {
        this.prop_count = prop_count;
    }
}
