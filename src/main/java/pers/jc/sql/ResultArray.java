package pers.jc.sql;

import com.alibaba.fastjson.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class ResultArray {

    protected static void parseToArrayList(ResultSet resultSet, ArrayList list) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < columnCount; i++) {
                String columnName = metaData.getColumnName(i + 1);
                switch (metaData.getColumnType(i + 1)) {
                    case java.sql.Types.ARRAY:
                        jsonObject.put(columnName, resultSet.getArray(columnName));
                        break;
                    case java.sql.Types.BIGINT:
                    case java.sql.Types.INTEGER:
                    case java.sql.Types.TINYINT:
                    case java.sql.Types.SMALLINT:
                        jsonObject.put(columnName, resultSet.getInt(columnName));
                        break;
                    case java.sql.Types.BOOLEAN:
                        jsonObject.put(columnName, resultSet.getBoolean(columnName));
                        break;
                    case java.sql.Types.BLOB:
                        jsonObject.put(columnName, resultSet.getBlob(columnName));
                        break;
                    case java.sql.Types.DOUBLE:
                        jsonObject.put(columnName, resultSet.getDouble(columnName));
                        break;
                    case java.sql.Types.FLOAT:
                        jsonObject.put(columnName, resultSet.getFloat(columnName));
                        break;
                    case java.sql.Types.NVARCHAR:
                        jsonObject.put(columnName, resultSet.getNString(columnName));
                        break;
                    case java.sql.Types.VARCHAR:
                        jsonObject.put(columnName, resultSet.getString(columnName));
                        break;
                    case java.sql.Types.DATE:
                        jsonObject.put(columnName, resultSet.getDate(columnName));
                        break;
                    case java.sql.Types.TIMESTAMP:
                        jsonObject.put(columnName, resultSet.getTimestamp(columnName));
                        break;
                    default:
                        jsonObject.put(columnName, resultSet.getObject(columnName));
                        break;
                }
            }
            list.add(jsonObject);
        }
    }
}
