import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

// 即使用上了连接池，也仅仅是解决了性能问题，要想提高开发效率，还是得让开发人员专心写业务逻辑
// 少处理 connection , statement ,resultset , 释放资源等工作
// SpringJdbc的JdbcTemplate实际采用了模板方法设计模式，把整个请求过程进行了封装，仅把很少写sql的地方暴露给开发人员
// 减轻了开发人员开发无关的工作
public class JDBCTest4 {

    static String url = "";
    static String user = "";
    static String password = "";

    // 引入spring-jdbc仅仅加入该依赖即可
    // <!-- https://mvnrepository.com/artifact/org.springframework/spring-jdbc -->
    //        <dependency>
    //            <groupId>org.springframework</groupId>
    //            <artifactId>spring-jdbc</artifactId>
    //            <version>5.3.8</version>
    //        </dependency>
    private static void init() {
        Utils.outputStartSeparator("init");
        try {
            InputStream resourceAsStream = JDBCTest1.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");

            HikariDataSource basicDataSource = new HikariDataSource();
            basicDataSource.setJdbcUrl(url);
            basicDataSource.setUsername(user);
            basicDataSource.setPassword(password);

            JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
            //如果存在USER_INFO表就先删除USER_INFO表
            jdbcTemplate.execute("DROP TABLE IF EXISTS USER_INFO");
            //创建USER_INFO表
            jdbcTemplate.execute("CREATE TABLE USER_INFO(id VARCHAR(36) PRIMARY KEY,name VARCHAR(100),sex VARCHAR(4))");
            //新增
            jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','大日如来','男')");
            jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','青龙','男')");
            jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','白虎','男')");
            jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','朱雀','女')");
            jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','玄武','男')");
            jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','苍狼','男')");

        } catch (Exception e) {
            System.out.println("初始化失败." + e.getMessage());
        }
        System.out.println("初始化完成");
        Utils.outputEndSeparator("init");
    }

    public static void test1() {

    }

    public static void main(String[] args) {
        init();
        try {
            test1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
