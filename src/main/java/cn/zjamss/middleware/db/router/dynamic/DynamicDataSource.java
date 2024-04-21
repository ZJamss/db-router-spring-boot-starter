package cn.zjamss.middleware.db.router.dynamic;

import cn.zjamss.middleware.db.router.DataBaseContextHolder;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据库
 * @author ZJamss
 * @date 2024/4/21
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String key  = DataBaseContextHolder.getDBKey();
        return "db" + key;
    }
}
