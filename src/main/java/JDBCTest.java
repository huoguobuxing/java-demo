import org.h2.Driver;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.PooledConnection;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

// 主要测试jdbc相关功能
public class JDBCTest {

    static String url = "";
    static String user = "";
    static String password = "";

    // 创建一个H2数据库，填充一些数据，用于测试
    private static void init() {
        try {
            InputStream resourceAsStream = JDBCTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");

            Connection conn = DriverManager.getConnection(url, user, password);
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
    }

    // 测试使用 DriverManager 获取连接
    private static void test1() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn instanceof JdbcConnection);
        System.out.println(conn.isClosed());
        PreparedStatement preparedStatement = conn.prepareStatement("select * from USER_INFO");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }
        conn.close();
        System.out.println(conn.isClosed());
    }

    // 测试使用 DataSource 获取连接
    private static void test2() throws SQLException {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(url);
        jdbcDataSource.setUser(user);
        jdbcDataSource.setPassword(password);
        Connection conn = jdbcDataSource.getConnection();
        System.out.println(conn instanceof JdbcConnection);
        PreparedStatement preparedStatement = conn.prepareStatement("select * from USER_INFO");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }
        conn.close();
        System.out.println(conn.isClosed());
    }

    // 测试使用 PooledDataSource 获取连接
    private static void test3() throws SQLException {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(url);
        jdbcDataSource.setUser(user);
        jdbcDataSource.setPassword(password);
        PooledConnection conn = jdbcDataSource.getPooledConnection();
        PreparedStatement preparedStatement = conn.getConnection().prepareStatement("select * from USER_INFO");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }
        // poolConnection如果关闭，conn.getConnection()将返回null
        conn.getConnection().close();
        System.out.println(conn.getConnection().isClosed());
    }

    // 使用 Driver 直接获取连接
    private static void test4() throws SQLException, IOException {
        Driver load = Driver.load();

        InputStream resourceAsStream = JDBCTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);

        Connection connect = load.connect(url, properties);
        System.out.println(connect.isClosed());
        connect.close();
        System.out.println(connect.isClosed());
    }


    public static void main(String[] args) {
        init();
        try {
//            test1();
//            test2();
//            test3();
            test4();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
