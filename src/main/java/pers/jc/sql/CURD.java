package pers.jc.sql;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CURD {
	private Access access;
	
	public CURD(Map<String, Object> config) {
		access = new Access(config);
	}

	public Access getAccess() {
		return access;
	}

	public ArrayList<JSONObject> select(SQL sql) {
		return Handle.select(access, JSONObject.class, sql.toString());
	}

	public ArrayList<JSONObject> select(String sql) {
		return Handle.select(access, JSONObject.class, sql);
	}
	
	public <T> ArrayList<T> select(Class<T> modelClass, SQL sql) {
		sql.SELECT_FROM(modelClass);
		return Handle.select(access, modelClass, sql.toString());
	}
	
	public <T> T selectOne(Class<T> modelClass, SQL sql) {
		sql.SELECT_FROM(modelClass);
		sql.LIMIT("1");
		ArrayList<T> list = Handle.select(access, modelClass, sql.toString());
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public <T> ArrayList<T> selectAll(Class<T> modelClass) {
		return Handle.select(access, modelClass, new SQL(){{
			SELECT_FROM(modelClass);
		}}.toString());
	}
	
	@SuppressWarnings("unchecked")
	public <T> int insert(T... models) {
		return Handle.insert(access, false, false, Statement.NO_GENERATED_KEYS, models);
	}

	@SuppressWarnings("unchecked")
	public <T> int insertIgnore(T... models) {
		return Handle.insert(access, true, false, Statement.NO_GENERATED_KEYS, models);
	}

	@SuppressWarnings("unchecked")
	public <T> int replace(T... models) {
		return Handle.insert(access, false, true, Statement.NO_GENERATED_KEYS, models);
	}
	
	public int insert(SQL sql) {
		return Handle.executeUpdate(access, sql.toString(), Statement.NO_GENERATED_KEYS);
	}

	public int insert(String sql) {
		return Handle.executeUpdate(access, sql, Statement.NO_GENERATED_KEYS);
	}
	
	@SuppressWarnings("unchecked")
	public <T> int insertAndGenerateKeys(T... models) {
		return Handle.insert(access, false, false, Statement.RETURN_GENERATED_KEYS, models);
	}

	@SuppressWarnings("unchecked")
	public <T> int insertIgnoreAndGenerateKeys(T... models) {
		return Handle.insert(access, true, false, Statement.RETURN_GENERATED_KEYS, models);
	}

	@SuppressWarnings("unchecked")
	public <T> int replaceAndGenerateKeys(T... models) {
		return Handle.insert(access, false, true, Statement.RETURN_GENERATED_KEYS, models);
	}

	public int insertAndReturnKey(SQL sql) {
		return Handle.executeUpdate(access, sql.toString(), Statement.RETURN_GENERATED_KEYS);
	}

	public int insertAndReturnKey(String sql) {
		return Handle.executeUpdate(access, sql, Statement.RETURN_GENERATED_KEYS);
	}
	
	@SuppressWarnings("unchecked")
	public <T> int update(T... models) {
		return Handle.update(access, false, models);
	}

	@SuppressWarnings("unchecked")
	public <T> int updateIgnore(T... models) {
		return Handle.update(access, true, models);
	}
	
	public int update(SQL sql) {
		return Handle.executeUpdate(access, sql.toString(), Statement.NO_GENERATED_KEYS);
	}

	public int update(String sql) {
		return Handle.executeUpdate(access, sql, Statement.NO_GENERATED_KEYS);
	}
	
	@SuppressWarnings("unchecked")
	public <T> int delete(T... models) {
		return Handle.delete(access, false, models);
	}

	@SuppressWarnings("unchecked")
	public <T> int deleteIgnore(T... models) {
		return Handle.delete(access, true, models);
	}
	
	public int delete(SQL sql) {
		return Handle.executeUpdate(access, sql.toString(), Statement.NO_GENERATED_KEYS);
	}

	public int delete(String sql) {
		return Handle.executeUpdate(access, sql, Statement.NO_GENERATED_KEYS);
	}

	public int getRowCount(Class<?> modelClass) {
		return getRowCount(modelClass, new SQL());
	}

	public int getRowCount(String tableName) {
		return getRowCount(tableName, new SQL());
	}
	
	public int getRowCount(Class<?> modelClass, SQL sql) {
		String tableName = null;
		try {
			tableName = Handle.getTableInfo(modelClass).tableName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getRowCount(tableName, sql);
	}

	public int getRowCount(String tableName, SQL sql) {
		sql.SELECT("COUNT(*) AS res");
		sql.FROM(tableName);
		return Handle.getRowCount(access, sql.toString());
	}

	public static Map<String, String> getDictionary(Class<?> modelClass) {
		Map<String, String> dictionary = new HashMap<>();
		for (Field field : modelClass.getDeclaredFields()) {
			Description description = field.getAnnotation(Description.class);
			if (description != null) {
				dictionary.put(field.getName(), description.value());
			}
		}
		return dictionary;
	}
}
