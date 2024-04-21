package cn.zjamss.middleware.db.router;

/**
 * 数据库分库分表上下文
 *
 * @author ZJamss
 * @date 2024/4/21
 */
public class DataBaseContextHolder {

    private static final ThreadLocal<String> DBKey = new ThreadLocal<>();

    private static final ThreadLocal<String> TBKey = new ThreadLocal<>();

    public static String getDBKey() {
        return DBKey.get();
    }

    public static String getTBKey() {
        return TBKey.get();
    }

    public static void setDBKey(String key) {
        DBKey.set(key);
    }

    public static void setTBKey(String key) {
        TBKey.set(key);
    }

    public static void clearDBKey() {
        DBKey.remove();
    }

    public static void clearTBKey() {
        TBKey.remove();
    }


}
