
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

// 主要测试jdbc相关功能
public class JDBCTest {

    static String url = "";
    static String user = "";
    static String password = "";

    public static void init() {
        try {
            InputStream resourceAsStream = JDBCTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
        } catch (Exception e) {

        }
    }

    public static void test1() throws ClassNotFoundException, IOException {
        Class<?> aClass = Class.forName(url);
    }

    public static void test2() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://139.198.21.183:3460/website", "prnqa", "Comeon2019_prn");
    }

    public static void main(String[] args) {
        try {
            init();
//            test1();
            test2();
        } catch (Exception e) {
        }
    }
}
