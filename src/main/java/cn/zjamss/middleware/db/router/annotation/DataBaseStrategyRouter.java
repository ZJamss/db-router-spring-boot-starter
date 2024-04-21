package cn.zjamss.middleware.db.router.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否开启分表
 * @author ZJamss
 * @date 2024/4/21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DataBaseStrategyRouter {
    boolean splitTable() default false;
}
