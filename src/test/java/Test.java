import entity.table.Prop;

public class Test {

    public static void main(String[] args) {
        DataBase.init();
        for (int i = 100; i < 200; i++) {
            Prop prop = new Prop();
            prop.setProp_id(i);
            prop.setPlayer_id(i);
            prop.setProp_count(i);
            DataBase.curd.insert(prop);
        }
    }
}
