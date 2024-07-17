# db-router
用于Java操作数据库分库分表的迷你库

# 教程
## 
克隆代码到本地，然后通过Maven安装
## 依赖
```xml
     <dependency>
                <groupId>cn.zjamss.middleware</groupId>
                <artifactId>db-router-spring-boot-starter</artifactId>
                <version>1.0-SNAPSHOT</version>
     </dependency>
```
## 案例
### UserMapper.java
```java
/**
* 开启分库
*/
@DataBaseStrategyRouter(splitTable = true)
@Mapper
public interface UserMapper {
    /**
     * 此方法基于uId进行分库
     */
    @DataBaseRouter(column = "uId")
    void insert(UserPO user);
}
```
### application.xml
```yaml
db-router:
  dbCount: 2
  tbCount: 4
  default: db00
  names: db01,db02
  db00:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/db?useUnicode=true
    username: root
    password: 123456
  db01:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/db_01?useUnicode=true
    username: root
    password: 123456
  db02:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/db_02?useUnicode=true
    username: root
    password: 123456
```
