import org.h2.Driver;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.PooledConnection;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

// 主要测试jdbc如何获得连接，并基于连接做一些操作
// 由于只连接h2，所以只需要一个依赖
// <dependency>
//            <groupId>com.h2database</groupId>
//            <artifactId>h2</artifactId>
//            <version>1.4.200</version>
//</dependency>
public class JDBCTest1 {

    static String url = "";
    static String user = "";
    static String password = "";

    public static void main(String[] args) {
        init();
        try {
//            test1();
//            test2();
//            test3();
//            test4();
//            test5();
//            test6();
            test7();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 测试下 savePoint 用法
    public static void test7() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        Savepoint sp1 = null;
        Savepoint sp2 = null;
        try {
            //新增
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','test1','男')");
            sp1 = conn.setSavepoint("sp1");
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','test2','男')");
            sp2 = conn.setSavepoint("sp2");
            int i = 0;
            int j = 10 / i;
            conn.commit();
        } catch (Exception e) {
            conn.rollback(sp1);
            // 存在
            ResultSet resultSet1 = stmt.executeQuery("SELECT * FROM USER_INFO WHERE name = 'test1'");
            while (resultSet1.next()) {
                System.out.println(resultSet1.getString("name"));
            }

            //不存在
            ResultSet resultSet2 = stmt.executeQuery("SELECT * FROM USER_INFO WHERE name = 'test2'");
            while (resultSet2.next()) {
                System.out.println(resultSet2.getString("name"));
            }
        }
    }

    // 测试下正常事务，使用事务时，发生除零异常后，数据没有插入成功，符合预期
    public static void test6() throws SQLException {

        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        try {
            //新增
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','test','男')");

            int i = 0;
            int j = 10 / i;
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM USER_INFO WHERE name = 'test'");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }
        }
    }

    // 测试下正常事务，不使用事务时，发生除零异常后，数据依然插入成功
    public static void test5() throws SQLException {

        Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        try {
            //新增
            stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID() + "','test','男')");

            int i = 0;
            int j = 10 / i;
        } catch (Exception e) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM USER_INFO WHERE name = 'test'");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }
        }
    }


    // 创建一个H2数据库，填充一些数据，用于测试
    private static void init() {
        Utils.outputStartSeparator("init");
        try {
            InputStream resourceAsStream = JDBCTest1.class.getClassLoader().getResourceAsStream("jdbc.properties");
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
        Utils.outputEndSeparator("init");
    }

    // 使用 Driver 直接获取连接
    private static void test4() throws SQLException, IOException {
        Utils.outputStartSeparator("test4");
        Driver load = Driver.load();

        InputStream resourceAsStream = JDBCTest1.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);

        Connection connect = load.connect(url, properties);
        System.out.println(connect.isClosed());
        connect.close();
        System.out.println(connect.isClosed());
        Utils.outputEndSeparator("test4");
    }

    // 测试使用 DriverManager 获取连接
    private static void test1() throws SQLException {
        Utils.outputStartSeparator("test1");
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
        Utils.outputEndSeparator("test1");
    }

    // 测试使用 DataSource 获取连接
    private static void test2() throws SQLException {
        Utils.outputStartSeparator("test2");
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
        Utils.outputEndSeparator("test1");
    }

    // 测试使用 PooledDataSource 获取连接
    private static void test3() throws SQLException {
        Utils.outputStartSeparator("test3");
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
        Utils.outputEndSeparator("test3");
    }

}
