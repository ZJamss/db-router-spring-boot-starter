package cn.zjamss.middleware.db.router;

import cn.zjamss.middleware.db.router.annotation.DataBaseRouter;
import cn.zjamss.middleware.db.router.strategy.IDataBaseRouterStrategy;
import java.lang.reflect.Field;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ZJamss
 * @date 2024/4/21
 */
@Aspect
public class DataBaseRouterJoinPoint {

    private final Logger logger = LoggerFactory.getLogger(DataBaseRouterJoinPoint.class);

    private DataBaseRouterConfig dataBaseRouterConfig;

    private IDataBaseRouterStrategy dataBaseStrategy;

    public DataBaseRouterJoinPoint(DataBaseRouterConfig dataBaseRouterConfig,
                                   IDataBaseRouterStrategy dataBaseStrategy) {

        this.dataBaseRouterConfig = dataBaseRouterConfig;
        this.dataBaseStrategy = dataBaseStrategy;
    }

    @Pointcut("@annotation(cn.zjamss.middleware.db.router.annotation.DataBaseRouter)")
    public void aopPoint() {
    }


    @Around("aopPoint() && @annotation(router)")
    public Object around(ProceedingJoinPoint joinPoint, DataBaseRouter router) throws Throwable {
        String column = router.column();
        if (StringUtils.isBlank(column)) {
            throw new RuntimeException("@DataBaseRouter column is blank！");
        }
        String columnVal = getAttrValue(column, joinPoint.getArgs());
        dataBaseStrategy.doRouter(columnVal);
        try {
            return joinPoint.proceed();
        } finally {
            dataBaseStrategy.clear();
        }
    }

    public String getAttrValue(String attr, Object[] args) {
        if (1 == args.length) {
            Object arg = args[0];
            if (arg instanceof String) {
                return arg.toString();
            }
        }

        String filedValue = null;
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                // filedValue = BeanUtils.getProperty(arg, attr);
                // fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                logger.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     *
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     * @author tang
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     * @author tang
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }


}
