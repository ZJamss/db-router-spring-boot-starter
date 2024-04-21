package cn.zjamss.middleware.db.router.strategy;

/**
 * 路由策略接口
 *
 * @author ZJamss
 * @date 2024/4/21
 */
public interface IDataBaseRouterStrategy {
    /**
     * 路由计算
     *
     * @param val 路由字段值
     */
    void doRouter(String val);

    /**
     * 手动设置分库路由
     *
     * @param dbIdx 路由库，需要在配置范围内
     */
    void setDBKey(int dbIdx);

    /**
     * 手动设置分表路由
     *
     * @param tbIdx 路由表，需要在配置范围内
     */
    void setTBKey(int tbIdx);

    /**
     * 获取分库数
     *
     * @return 数量
     */
    int dbCount();

    /**
     * 获取分表数
     *
     * @return 数量
     */
    int tbCount();

    /**
     * 清除路由
     */
    void clear();
}
