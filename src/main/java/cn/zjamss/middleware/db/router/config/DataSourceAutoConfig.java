package cn.zjamss.middleware.db.router.config;

import cn.zjamss.middleware.db.router.DataBaseRouterConfig;
import cn.zjamss.middleware.db.router.DataBaseRouterJoinPoint;
import cn.zjamss.middleware.db.router.dynamic.DynamicDataSource;
import cn.zjamss.middleware.db.router.dynamic.DynamicMybatisPlugin;
import cn.zjamss.middleware.db.router.strategy.IDataBaseRouterStrategy;
import cn.zjamss.middleware.db.router.strategy.impl.HashDataBaseRouterStrategy;
import cn.zjamss.middleware.db.router.util.PropertyUtil;
import cn.zjamss.middleware.db.router.util.StringUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * @author ZJamss
 * @date 2024/4/21
 */
@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    private final Map<String, Map<String, Object>> dataSourceGroup = new HashMap<>();

    private Map<String, Object> defaultDataSourceConfig;

    private Integer dbCount;
    private Integer tbCount;


    @Bean
    public DataBaseRouterConfig dataBaseRouterConfig() {
        return new DataBaseRouterConfig(dbCount, tbCount);
    }

    @Bean
    public IDataBaseRouterStrategy dataBaseStrategy() {
        return new HashDataBaseRouterStrategy(dataBaseRouterConfig());
    }

    @Bean(name = "db-router-point")
    @ConditionalOnMissingBean
    public DataBaseRouterJoinPoint point(DataBaseRouterConfig dbRouterConfig,
                                         IDataBaseRouterStrategy dbRouterStrategy) {
        return new DataBaseRouterJoinPoint(dbRouterConfig, dbRouterStrategy);
    }


    @Bean
    public Interceptor plugin() {
        return new DynamicMybatisPlugin();
    }

    private DataSource createDataSource(Map<String, Object> attributes) {
        try {
            DataSourceProperties dataSourceProperties = new DataSourceProperties();
            dataSourceProperties.setUrl(attributes.get("url").toString());
            dataSourceProperties.setUsername(attributes.get("username").toString());
            dataSourceProperties.setPassword(attributes.get("password").toString());

            String driverClassName =
                attributes.get("driver-class-name") == null ? "com.zaxxer.hikari.HikariDataSource" :
                    attributes.get("driver-class-name").toString();
            dataSourceProperties.setDriverClassName(driverClassName);

            String typeClassName =
                attributes.get("type-class-name") == null ? "com.zaxxer.hikari.HikariDataSource" :
                    attributes.get("type-class-name").toString();
            DataSource ds = dataSourceProperties.initializeDataSourceBuilder()
                .type((Class<DataSource>) Class.forName(typeClassName)).build();

            MetaObject dsMeta = SystemMetaObject.forObject(ds);
            Map<String, Object> poolProps =
                (Map<String, Object>) (attributes.containsKey("pool") ? attributes.get("pool") :
                    Collections.EMPTY_MAP);
            for (Map.Entry<String, Object> entry : poolProps.entrySet()) {
                // 中划线转驼峰
                String key = StringUtils.middleScoreToCamelCase(entry.getKey());
                if (dsMeta.hasSetter(key)) {
                    dsMeta.setValue(key, entry.getValue());
                }
            }
            return ds;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("can not find datasource type class by class name",
                e);
        }
    }

    @Bean
    public DataSource dataSource() {
        // 创建数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String name : dataSourceGroup.keySet()) {
            Map<String, Object> objMap = dataSourceGroup.get(name);
            DataSource ds = createDataSource(objMap);
            targetDataSources.put(name, ds);
        }

        // 设置数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(createDataSource(defaultDataSourceConfig));
        return dynamicDataSource;
    }

    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager =
            new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }


    @Override
    public void setEnvironment(Environment environment) {
        final String prefix = "db-router.";

        dbCount = environment.getProperty(prefix + "dbCount", Integer.class);
        tbCount = environment.getProperty(prefix + "tbCount", Integer.class);

        String dataSources = environment.getProperty(prefix + "names", String.class);
        for (String name : dataSources.split(",")) {
            Map<String, Object> dataSourceProps =
                PropertyUtil.handle(environment, prefix + name, Map.class);
            dataSourceGroup.put(name, dataSourceProps);
        }

        // 默认数据源
        String defaultDataSource = environment.getProperty(prefix + "default");
        defaultDataSourceConfig =
            PropertyUtil.handle(environment, prefix + defaultDataSource, Map.class);
    }
}
