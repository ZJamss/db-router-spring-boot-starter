package cn.zjamss.middleware.db.router.strategy.impl;

import cn.zjamss.middleware.db.router.DataBaseContextHolder;
import cn.zjamss.middleware.db.router.DataBaseRouterConfig;
import cn.zjamss.middleware.db.router.strategy.IDataBaseRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 哈希路由实现
 *
 * @author ZJamss
 * @date 2024/4/21
 */
public class HashDataBaseRouterStrategy implements IDataBaseRouterStrategy {

    private Logger logger = LoggerFactory.getLogger(HashDataBaseRouterStrategy.class);

    private DataBaseRouterConfig dataBaseRouterConfig;

    public HashDataBaseRouterStrategy(DataBaseRouterConfig dbRouterConfig) {
        this.dataBaseRouterConfig = dbRouterConfig;
    }


    @Override
    public void doRouter(String val) {
        int size = dataBaseRouterConfig.getDbCount() * dataBaseRouterConfig.getTbCount();

        int idx = (size - 1) & (val.hashCode() ^ (val.hashCode() >>> 16));

        int dbIdx = idx / dataBaseRouterConfig.getTbCount() + 1;
        int tbIdx = idx % dataBaseRouterConfig.getTbCount();

        DataBaseContextHolder.setDBKey(String.format("%02d", dbIdx));
        DataBaseContextHolder.setTBKey(String.format("%03d", tbIdx));
        logger.debug("数据库路由 dbIdx：{} tbIdx：{}", dbIdx, tbIdx);

    }

    @Override
    public void setDBKey(int dbIdx) {
        DataBaseContextHolder.setDBKey(String.format("%02d", dbIdx));
    }

    @Override
    public void setTBKey(int tbIdx) {
        DataBaseContextHolder.setTBKey(String.format("%03d", tbIdx));
    }

    @Override
    public int dbCount() {
        return dataBaseRouterConfig.getDbCount();
    }

    @Override
    public int tbCount() {
        return dataBaseRouterConfig.getTbCount();
    }

    @Override
    public void clear() {
        DataBaseContextHolder.clearDBKey();
        DataBaseContextHolder.clearTBKey();
    }
}
