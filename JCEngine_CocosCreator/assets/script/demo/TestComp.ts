export default class TestComp {
    public static ins = new TestComp();

    //自定义函数（可被后端调用）
    public doSomeThing(arg0) {
        console.log("doSomeThing", arg0);
    }
}
