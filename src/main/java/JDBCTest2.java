import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.DelegatingConnection;

import javax.sql.PooledConnection;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

//连接获取到了，接下来需要考虑性能了，引出连接池 dbcp
public class JDBCTest2 {

    static String url = "";
    static String user = "";
    static String password = "";


    // 使用dhcp连接池，只需引入一个依赖
    // <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2 -->
    //<dependency>
    //    <groupId>org.apache.commons</groupId>
    //    <artifactId>commons-dbcp2</artifactId>
    //    <version>2.1.1</version>
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

            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(url);
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
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);
        basicDataSource.setMaxTotal(1);
        basicDataSource.setMaxIdle(1);
        //不设置无法访问到最底层的 connection
        basicDataSource.setAccessToUnderlyingConnectionAllowed(true);
        System.out.println(basicDataSource.getMaxTotal());

        Connection con1 = basicDataSource.getConnection();
        System.out.println(con1 instanceof DelegatingConnection);
        System.out.println(((DelegatingConnection) con1).getDelegate());
        //close并非真的关闭了连接，而是放回了连接池
        con1.close();

        Connection con2 = basicDataSource.getConnection();
        System.out.println(con2 instanceof DelegatingConnection);
        System.out.println(((DelegatingConnection) con2).getDelegate());
        con2.close();

        //通过输出，可见两个底层的connection是一样的，说明确实使用了连接池
        basicDataSource.close();
    }

    //测试连接池满了后，再次获取连接将会失败
    private static void test2() throws SQLException {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);
        basicDataSource.setMaxTotal(1);
        basicDataSource.setMaxIdle(1);
        //不设置无法访问到最底层的 connection
        basicDataSource.setAccessToUnderlyingConnectionAllowed(true);
        System.out.println(basicDataSource.getMaxWaitMillis());
        //当连接池里的连接都在使用时，再次获取新链接时，超过等待时间将会失败，设置该属性为了尽快失败
        basicDataSource.setMaxWaitMillis(5000);
        System.out.println(basicDataSource.getMaxTotal());

        Connection con1 = basicDataSource.getConnection();
        System.out.println(con1 instanceof DelegatingConnection);
        System.out.println(((DelegatingConnection) con1).getDelegate());

        Connection con2 = basicDataSource.getConnection();
        System.out.println(con2 instanceof DelegatingConnection);
        System.out.println(((DelegatingConnection) con2).getDelegate());
        con2.close();

        //通过输出，可见两个底层的connection是一样的，说明确实使用了连接池
        basicDataSource.close();
    }

    public static void main(String[] args) {
        init();
        try {
//            test1();
            test2();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
