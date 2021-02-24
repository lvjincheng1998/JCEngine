package pers.jc.sql;

import com.alibaba.fastjson.JSONObject;
import org.reflections.Reflections;
import pers.jc.engine.JCEngine;
import pers.jc.network.HttpComponent;
import pers.jc.network.HttpGet;
import java.util.*;

@HttpComponent("/dataView")
public class DataView {
    private static HashMap<String, Class<?>> tableMap = new HashMap<>();
    private static CURD curd;
    public static boolean enabled;

    public static void scanPackage(String targetPackage) {
        Reflections reflections = new Reflections(targetPackage);
        Set<Class<?>> tables = reflections.getTypesAnnotatedWith(Table.class);
        for (Class<?> table : tables) {
            tableMap.put(table.getName(), table);
        }
    }

    public static void enable(CURD curd) {
        DataView.curd = curd;
        JCEngine.addComponent(DataView.class);
        enabled = true;
    }

    @HttpGet("/getTableList")
    public HashMap<String, String> getTableList() {
        HashMap<String, String> tableList = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry : tableMap.entrySet()) {
            Class<?> tableClass = entry.getValue();
            Table tableAnnotation = tableClass.getAnnotation(Table.class);
            if (tableAnnotation.value().isEmpty()) {
                tableList.put(entry.getKey(), tableClass.getSimpleName());
            } else {
                tableList.put(entry.getKey(), tableAnnotation.value());
            }
        }
        return tableList;
    }

    @HttpGet("/getTableCols")
    public ArrayList<JSONObject> getTableCols(String tableKey) throws Exception {
        Class<?> tableClass = tableMap.get(tableKey);
        TableInfo tableInfo = Handle.getTableInfo(tableClass);
        ArrayList<JSONObject> tableCols = new ArrayList<>();
        for (FieldInfo fieldInfo : tableInfo.fieldInfos) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("field", fieldInfo.columnLabel);
            jsonObject.put("title", fieldInfo.columnLabel);
            jsonObject.put("isKey", fieldInfo.isIdColumn);
            jsonObject.put("autoIncrement", fieldInfo.autoIncrement);
            jsonObject.put("align", "center");
            jsonObject.put("sort", true);
            tableCols.add(jsonObject);
        }
        return tableCols;
    }

    @HttpGet("/showTable")
    public JSONObject showTable(String tableKey, int page, int limit) {
        Class<?> tableClass = tableMap.get(tableKey);
        int count = curd.getRowCount(tableClass);
        ArrayList<?> data = curd.select(tableClass, new SQL(){{
            LIMIT((page - 1) * limit, limit);
        }});
        JSONObject response = new JSONObject();
        response.put("code", 0);
        response.put("msg", "success");
        response.put("count", count);
        response.put("data", data);
        return response;
    }
}
