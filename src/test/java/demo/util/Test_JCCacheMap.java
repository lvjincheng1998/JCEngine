package demo.util;

import pers.jc.util.JCCacheMap;

public class Test_JCCacheMap {
    private static JCCacheMap cacheMap = new JCCacheMap();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("===加入map===");
        cacheMap.put("a", 1);
        cacheMap.put("b", 2, System.currentTimeMillis() + 100);
        System.out.println("map-size=" + cacheMap.size());

        System.out.println("===查看map==");
        System.out.println(cacheMap.get("a"));
        System.out.println(cacheMap.get("b"));

        Thread.sleep(100);

        System.out.println("===100ms后查看map===");
        System.out.println(cacheMap.get("a", Integer.class)); //可以声明返回类型
        System.out.println(cacheMap.get("b"));

        System.out.println("===严格移除===");
        System.out.println("map-size=" + cacheMap.size());
        boolean removeSuccess = cacheMap.remove("a", 2); //校验值与实际不同
        if (!removeSuccess) System.out.println("移除失败");
        System.out.println("map-size=" + cacheMap.size());
        cacheMap.remove("a", 1); //校验值与实际相同
        System.out.println("map-size=" + cacheMap.size());

        System.out.println("===查看移除的元素");
        cacheMap.put("a", 1);
        Object obj = cacheMap.remove("a");
        System.out.println(obj);
        cacheMap.put("b", 2);
        Integer intVal = cacheMap.remove("b", Integer.class);  //可以声明返回类型
        System.out.println(intVal);
    }
}
