# db-router
a mini library about sharding database for java

# Tutorial
## install
clone the code to your local environment and then install it by Maven.
## dependency
```xml
     <dependency>
                <groupId>cn.zjamss.middleware</groupId>
                <artifactId>db-router-spring-boot-starter</artifactId>
                <version>1.0-SNAPSHOT</version>
     </dependency>
```
## Example
### UserMapper.java
```java
/**
* Sharding tables based on the column of the @DataBaseRouter in this Class.
*/
@DataBaseStrategyRouter(splitTable = true)
@Mapper
public interface UserMapper {
    /**
     * Sharding database based on the "uId" field of the User.
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
