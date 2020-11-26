package pers.jc.sql;

import com.alibaba.fastjson.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQL extends AbstractSQL<SQL> {
	
	public SQL SELECT_FROM(Class<?> modelClass) {
		try {
			TableInfo tableInfo = Handle.getTableInfo(modelClass);
			for (FieldInfo fieldInfo : tableInfo.fieldInfos) {
				SELECT(fieldInfo.columnLabel);
			}
			FROM(tableInfo.tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.getSelf();
	}
	
	public SQL INSERT_INTO(Class<?> modelClass) {
		try {
			TableInfo tableInfo = Handle.getTableInfo(modelClass);
			INSERT_INTO(tableInfo.tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.getSelf();
	}
	
	public SQL UPDATE(Class<?> modelClass) {
		try {
			TableInfo tableInfo = Handle.getTableInfo(modelClass);
			UPDATE(tableInfo.tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.getSelf();
	}
	
	public SQL DELETE_FROM(Class<?> modelClass) {
		try {
			TableInfo tableInfo = Handle.getTableInfo(modelClass);
			DELETE_FROM(tableInfo.tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.getSelf();
	}
	
	public SQL LIMIT(int index, int count) {
		super.LIMIT(String.valueOf(index));
		super.LIMIT(String.valueOf(count));
		return this.getSelf();
	}
	
	public String PARAM(Object parameter) {
		Class<?> type = parameter.getClass();
		if (type == String.class
				|| type == Date.class
				|| type == Time.class
				|| type == Timestamp.class) {
			return "'" + String.valueOf(parameter).replace("\\", "\\\\").replace("'", "\\'") + "'";
		}
		if (type == Boolean.class || type == boolean.class) {
			if (parameter.equals(true)) {
				return String.valueOf(1);
			} else {
				return String.valueOf(0);
			}
		}
		if (type == int.class || type == Integer.class 
				|| type == long.class || type == Long.class 
				|| type == float.class || type == Float.class 
				|| type == double.class || type == Double.class) {
			return parameter.toString();
		}
		if (type == JSONObject.class) {
		    return "'" + parameter.toString() + "'";
        }
		return "";
	}

	@Override
    public SQL getSelf() {
        return this;
    }
}

abstract class AbstractSQL<T> {
    private static final String AND = ") \nAND (";
    private static final String OR = ") \nOR (";
    private final AbstractSQL.SQLStatement sql = new AbstractSQL.SQLStatement();

    public abstract T getSelf();
    
    public T UPDATE(String table) {
        this.sql().statementType = AbstractSQL.SQLStatement.StatementType.UPDATE;
        this.sql().tables.add(table);
        return this.getSelf();
    }

    public T SET(String sets) {
        this.sql().sets.add(sets);
        return this.getSelf();
    }

    public T SET(String... sets) {
        this.sql().sets.addAll(Arrays.asList(sets));
        return this.getSelf();
    }

    public T INSERT_INTO(String tableName) {
        this.sql().statementType = AbstractSQL.SQLStatement.StatementType.INSERT;
        this.sql().tables.add(tableName);
        return this.getSelf();
    }

    public T VALUES(String columns, String values) {
        this.sql().columns.add(columns);
        this.sql().values.add(values);
        return this.getSelf();
    }

    public T INTO_COLUMNS(String... columns) {
        this.sql().columns.addAll(Arrays.asList(columns));
        return this.getSelf();
    }

    public T INTO_VALUES(String... values) {
        this.sql().values.addAll(Arrays.asList(values));
        return this.getSelf();
    }

    public T SELECT(String column) {
        this.sql().statementType = AbstractSQL.SQLStatement.StatementType.SELECT;
        if (!this.sql().select.contains(column)) {
        	this.sql().select.add(column);
		}
        return this.getSelf();
    }

    public T SELECT(String... columns) {
        this.sql().statementType = AbstractSQL.SQLStatement.StatementType.SELECT;
        for (String column : columns) {
        	this.SELECT(column);
		}
        return this.getSelf();
    }

    public T SELECT_DISTINCT(String column) {
        this.sql().distinct = true;
    	this.sql().select.remove(column);
    	this.sql().select.add(0, column);
        return this.getSelf();
    }

    public T SELECT_DISTINCT(String... columns) {
    	this.sql().distinct = true;
    	this.SELECT_DISTINCT(columns[0]);
        for (int i = 1; i < columns.length; i++) {
        	this.SELECT(columns[i]);
		}
        return this.getSelf();
    }

    public T DELETE_FROM(String table) {
        this.sql().statementType = AbstractSQL.SQLStatement.StatementType.DELETE;
        this.sql().tables.add(table);
        return this.getSelf();
    }

    public T FROM(String table) {
        this.sql().tables.add(table);
        return this.getSelf();
    }

    public T FROM(String... tables) {
        this.sql().tables.addAll(Arrays.asList(tables));
        return this.getSelf();
    }

    public T JOIN(String join) {
        this.sql().join.add(join);
        return this.getSelf();
    }

    public T JOIN(String... joins) {
        this.sql().join.addAll(Arrays.asList(joins));
        return this.getSelf();
    }

    public T INNER_JOIN(String join) {
        this.sql().innerJoin.add(join);
        return this.getSelf();
    }

    public T INNER_JOIN(String... joins) {
        this.sql().innerJoin.addAll(Arrays.asList(joins));
        return this.getSelf();
    }

    public T LEFT_OUTER_JOIN(String join) {
        this.sql().leftOuterJoin.add(join);
        return this.getSelf();
    }

    public T LEFT_OUTER_JOIN(String... joins) {
        this.sql().leftOuterJoin.addAll(Arrays.asList(joins));
        return this.getSelf();
    }

    public T RIGHT_OUTER_JOIN(String join) {
        this.sql().rightOuterJoin.add(join);
        return this.getSelf();
    }

    public T RIGHT_OUTER_JOIN(String... joins) {
        this.sql().rightOuterJoin.addAll(Arrays.asList(joins));
        return this.getSelf();
    }

    public T OUTER_JOIN(String join) {
        this.sql().outerJoin.add(join);
        return this.getSelf();
    }

    public T OUTER_JOIN(String... joins) {
        this.sql().outerJoin.addAll(Arrays.asList(joins));
        return this.getSelf();
    }

    public T WHERE(String conditions) {
        this.sql().where.add(conditions);
        this.sql().lastList = this.sql().where;
        return this.getSelf();
    }

    public T WHERE(String... conditions) {
        this.sql().where.addAll(Arrays.asList(conditions));
        this.sql().lastList = this.sql().where;
        return this.getSelf();
    }

    public T OR() {
        this.sql().lastList.add(OR);
        return this.getSelf();
    }

    public T AND() {
        this.sql().lastList.add(AND);
        return this.getSelf();
    }

    public T GROUP_BY(String columns) {
        this.sql().groupBy.add(columns);
        return this.getSelf();
    }

    public T GROUP_BY(String... columns) {
        this.sql().groupBy.addAll(Arrays.asList(columns));
        return this.getSelf();
    }

    public T HAVING(String conditions) {
        this.sql().having.add(conditions);
        this.sql().lastList = this.sql().having;
        return this.getSelf();
    }

    public T HAVING(String... conditions) {
        this.sql().having.addAll(Arrays.asList(conditions));
        this.sql().lastList = this.sql().having;
        return this.getSelf();
    }

    public T ORDER_BY(String columns) {
        this.sql().orderBy.add(columns);
        return this.getSelf();
    }

    public T ORDER_BY(String... columns) {
        this.sql().orderBy.addAll(Arrays.asList(columns));
        return this.getSelf();
    }
    
    public T LIMIT(String limit) {
    	this.sql().limit.add(limit);
    	return this.getSelf();
    }

    public T IGNORE() {
        this.sql().ignore = true;
        return this.getSelf();
    }

    public T REPLACE() {
        this.sql().replace = true;
        return this.getSelf();
    }

    public T ON_DUPLICATE_KEY_UPDATE(String sets) {
        this.sql().onDuplicateKeyUpdates.add(sets);
        return this.getSelf();
    }

    private AbstractSQL.SQLStatement sql() {
        return this.sql;
    }

    public <A extends Appendable> A usingAppender(A a) {
        this.sql().sql(a);
        return a;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.sql().sql(sb);
        return sb.toString();
    }

    private static class SQLStatement {
        AbstractSQL.SQLStatement.StatementType statementType;
        List<String> sets = new ArrayList<String>();
        List<String> select = new ArrayList<String>();
        List<String> tables = new ArrayList<String>();
        List<String> join = new ArrayList<String>();
        List<String> innerJoin = new ArrayList<String>();
        List<String> outerJoin = new ArrayList<String>();
        List<String> leftOuterJoin = new ArrayList<String>();
        List<String> rightOuterJoin = new ArrayList<String>();
        List<String> where = new ArrayList<String>();
        List<String> having = new ArrayList<String>();
        List<String> groupBy = new ArrayList<String>();
        List<String> orderBy = new ArrayList<String>();
        List<String> lastList = new ArrayList<String>();
        List<String> columns = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        List<String> limit = new ArrayList<String>();
        List<String> onDuplicateKeyUpdates = new ArrayList<>();
        boolean ignore;
        boolean replace;
        boolean distinct;

        private void sqlClause(AbstractSQL.SafeAppendable builder, String keyword, List<String> parts, String open, String close, String conjunction) {
            if (!parts.isEmpty()) {
                if (!builder.isEmpty()) {
                    builder.append("\n");
                }

                builder.append(keyword);
                builder.append(" ");
                builder.append(open);
                String last = "________";
                int i = 0;

                for(int n = parts.size(); i < n; ++i) {
                    String part = (String)parts.get(i);
                    if (i > 0 && !part.equals(AND) && !part.equals(OR) && !last.equals(AND) && !last.equals(OR)) {
                        builder.append(conjunction);
                    }

                    builder.append(part);
                    last = part;
                }

                builder.append(close);
            }

        }

        private String selectSQL(AbstractSQL.SafeAppendable builder) {
            if (this.distinct) {
                this.sqlClause(builder, "SELECT DISTINCT", this.select, "", "", ", ");
            } else {
                this.sqlClause(builder, "SELECT", this.select, "", "", ", ");
            }

            this.sqlClause(builder, "FROM", this.tables, "", "", ", ");
            this.joins(builder);
            this.sqlClause(builder, "WHERE", this.where, "(", ")", " AND ");
            this.sqlClause(builder, "GROUP BY", this.groupBy, "", "", ", ");
            this.sqlClause(builder, "HAVING", this.having, "(", ")", " AND ");
            this.sqlClause(builder, "ORDER BY", this.orderBy, "", "", ", ");
            this.sqlClause(builder, "LIMIT", this.limit, "", "", ", ");
            return builder.toString();
        }

        private void joins(AbstractSQL.SafeAppendable builder) {
            this.sqlClause(builder, "JOIN", this.join, "", "", "\nJOIN ");
            this.sqlClause(builder, "INNER JOIN", this.innerJoin, "", "", "\nINNER JOIN ");
            this.sqlClause(builder, "OUTER JOIN", this.outerJoin, "", "", "\nOUTER JOIN ");
            this.sqlClause(builder, "LEFT OUTER JOIN", this.leftOuterJoin, "", "", "\nLEFT OUTER JOIN ");
            this.sqlClause(builder, "RIGHT OUTER JOIN", this.rightOuterJoin, "", "", "\nRIGHT OUTER JOIN ");
        }

        private String insertSQL(AbstractSQL.SafeAppendable builder) {
            String mul_sql = "INSERT INTO";
            if (this.ignore) {
                mul_sql = "INSERT IGNORE INTO";
            } else if (this.replace) {
                mul_sql = "REPLACE INTO";
            }
            this.sqlClause(builder, mul_sql, this.tables, "", "", "");
            this.sqlClause(builder, "", this.columns, "(", ")", ", ");
            this.sqlClause(builder, "VALUES", this.values, "(", ")", ", ");
            if (onDuplicateKeyUpdates.size() > 0) {
                this.sqlClause(builder, "ON DUPLICATE KEY UPDATE", this.onDuplicateKeyUpdates, "", "", ", ");
            }
            return builder.toString();
        }

        private String deleteSQL(AbstractSQL.SafeAppendable builder) {
            String mul_sql = this.ignore ? "DELETE IGNORE FROM" : "DELETE FROM";
            this.sqlClause(builder, mul_sql, this.tables, "", "", "");
            this.sqlClause(builder, "WHERE", this.where, "(", ")", " AND ");
            return builder.toString();
        }

        private String updateSQL(AbstractSQL.SafeAppendable builder) {
            String mul_sql = this.ignore ? "UPDATE IGNORE" : "UPDATE";
            this.sqlClause(builder, mul_sql, this.tables, "", "", "");
            this.joins(builder);
            this.sqlClause(builder, "SET", this.sets, "", "", ", ");
            this.sqlClause(builder, "WHERE", this.where, "(", ")", " AND ");
            return builder.toString();
        }

        public String sql(Appendable a) {
            AbstractSQL.SafeAppendable builder = new AbstractSQL.SafeAppendable(a);
            if (this.statementType == null) {
                return null;
            } else {
                String answer;
                switch(this.statementType) {
                case DELETE:
                    answer = this.deleteSQL(builder);
                    break;
                case INSERT:
                    answer = this.insertSQL(builder);
                    break;
                case SELECT:
                    answer = this.selectSQL(builder);
                    break;
                case UPDATE:
                    answer = this.updateSQL(builder);
                    break;
                default:
                    answer = null;
                }

                return answer;
            }
        }

        public static enum StatementType {
            DELETE,
            INSERT,
            SELECT,
            UPDATE;
        }
    }

    private static class SafeAppendable {
        private final Appendable a;
        private boolean empty = true;

        public SafeAppendable(Appendable a) {
            this.a = a;
        }

        public AbstractSQL.SafeAppendable append(CharSequence s) {
            try {
                if (this.empty && s.length() > 0) {
                    this.empty = false;
                }

                this.a.append(s);
                return this;
            } catch (Exception var3) {
                throw new RuntimeException(var3);
            }
        }

        public boolean isEmpty() {
            return this.empty;
        }
    }
}