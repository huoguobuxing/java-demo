import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.springframework.transaction.TransactionDefinition.*;

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

    // 测试下事务传播策略 PROPAGATION_SUPPORTS , 不在乎外层有没有事务，有就用，没有就不用
    public static void test2() throws SQLException {
        HikariDataSource basicDataSource = new HikariDataSource();
        basicDataSource.setJdbcUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);

        PlatformTransactionManager transactionManager = new JdbcTransactionManager(basicDataSource);
        TransactionTemplate transactionTemplate1 = new TransactionTemplate(transactionManager);
        transactionTemplate1.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        transactionTemplate1.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                System.out.println(status);
                try {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
                    jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','test1','男')");
                    int i = 0;
                    TransactionTemplate transactionTemplate2 = new TransactionTemplate(transactionManager);
                    transactionTemplate2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
                    transactionTemplate2.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            System.out.println(status);
                            try {
                                JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
                                jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','test2','男')");
                                int i = 0;
                                int j = 10 / i;
                            } catch (Exception e) {
                                status.setRollbackOnly();
                            }
                        }
                    });

                } catch (Exception e) {
                    status.setRollbackOnly();
                }
            }
        });

        JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM USER_INFO WHERE name = 'test1' or name = 'test1'");
        for (Map<String, Object> map : maps) {
            System.out.println(map.get("name"));
        }
    }

    // 测试下正常事务，使用事务时，发生除零异常后，数据没有插入成功，符合预期
    public static void test1() throws SQLException {
        HikariDataSource basicDataSource = new HikariDataSource();
        basicDataSource.setJdbcUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);

        PlatformTransactionManager transactionManager = new JdbcTransactionManager(basicDataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
                    jdbcTemplate.execute("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','test','男')");
                    int i = 0;
                    int j = 10 / i;
                } catch (Exception e) {
                    status.setRollbackOnly();
                }
            }
        });
        JdbcTemplate jdbcTemplate = new JdbcTemplate(basicDataSource);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM USER_INFO WHERE name = 'test'");
        for (Map<String, Object> map : maps) {
            System.out.println(map.get("name"));
        }
    }

    public static void main(String[] args) {
        init();
        try {
//            test1();
            test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
