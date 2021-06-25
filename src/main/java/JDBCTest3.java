import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariProxyConnection;
import com.zaxxer.hikari.pool.ProxyConnection;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.DelegatingConnection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

// 光使用了dbcp，在测试下 HikariCP
public class JDBCTest3 {

    static String url = "";
    static String user = "";
    static String password = "";


    // 使用HikariCP连接池，只需引入一个依赖
    // <!-- https://mvnrepository.com/artifact/com.zaxxer/HikariCP -->
    //<dependency>
    //    <groupId>com.zaxxer</groupId>
    //    <artifactId>HikariCP</artifactId>
    //    <version>3.4.5</version>
    //</dependency>
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

            Connection conn = basicDataSource.getConnection();
            Statement stmt = conn.createStatement();
            //如果存在USER_INFO表就先删除USER_INFO表
            stmt.execute("DROP TABLE IF EXISTS USER_INFO");
            //创建USER_INFO表
            stmt.execute("CREATE TABLE USER_INFO(id VARCHAR(36) PRIMARY KEY,name VARCHAR(100),sex VARCHAR(4))");
            //新增
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','大日如来','男')");
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','青龙','男')");
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','白虎','男')");
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','朱雀','女')");
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','玄武','男')");
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','苍狼','男')");

            //释放资源 这里不能释放资源，对于内存数据库，一旦链接关闭，内存数据库就不存在了
            conn.close();
            conn = null;

        } catch (Exception e) {
            System.out.println("初始化失败." + e.getMessage());
        }
        System.out.println("初始化完成");
        Utils.outputEndSeparator("init");
    }

    //测试连接池有1个连接时，多次获取确实返回的同一个连接
    private static void test1() throws SQLException {
        HikariDataSource basicDataSource = new HikariDataSource();
        basicDataSource.setJdbcUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);
        basicDataSource.setMaximumPoolSize(1);
        //不设置无法访问到最底层的 connection

        Connection con1 = basicDataSource.getConnection();
        System.out.println(con1 instanceof ProxyConnection);
        System.out.println(((ProxyConnection) con1).unwrap(Connection.class));
        //close并非真的关闭了连接，而是放回了连接池
        con1.close();

        Connection con2 = basicDataSource.getConnection();
        System.out.println(con2 instanceof ProxyConnection);
        System.out.println(((ProxyConnection) con2).unwrap(Connection.class));
        con2.close();

        //通过输出，可见两个底层的connection是一样的，说明确实使用了连接池
        basicDataSource.close();
    }

    //测试连接池满了后，再次获取连接将会失败
    private static void test2() throws SQLException {
        HikariDataSource basicDataSource = new HikariDataSource();
        basicDataSource.setJdbcUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);
        basicDataSource.setMaximumPoolSize(1);
        basicDataSource.setConnectionTimeout(5000);

        Connection con1 = basicDataSource.getConnection();
        System.out.println(con1 instanceof ProxyConnection);
        System.out.println(((ProxyConnection) con1).unwrap(Connection.class));

        Connection con2 = basicDataSource.getConnection();
        System.out.println(con2 instanceof ProxyConnection);
        System.out.println(((ProxyConnection) con2).unwrap(Connection.class));
        con2.close();

        //通过输出，可见两个底层的connection是一样的，说明确实使用了连接池
        basicDataSource.close();
    }

    public static void main(String[] args) {
        init();
        try {
            test1();
            test2();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
