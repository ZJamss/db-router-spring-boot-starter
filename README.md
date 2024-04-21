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
