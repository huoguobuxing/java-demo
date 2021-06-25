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

    // 创建一个H2数据库，填充一些数据
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

            //释放资源
            stmt.close();
            stmt = null;
            conn.close();
            conn = null;

        } catch (Exception e) {
            System.out.println("初始化失败." + e.getMessage());
        }
        System.out.println("初始化完成");
    }

    public static void test1() throws ClassNotFoundException, IOException {
        Class<?> aClass = Class.forName(url);
    }

    public static void test2() throws SQLException, ClassNotFoundException {
        Connection conn = DriverManager.getConnection(url, user, password);
        Statement statement = conn.createStatement();
        System.out.println(statement);
    }

    public static void main(String[] args) {
        init();
        try {
//            test1();
//            test2();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
