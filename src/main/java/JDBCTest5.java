import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import temp.User;
import temp.UserMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class JDBCTest5 {

    static String url = "";
    static String user = "";
    static String password = "";

    public static void main(String[] args) throws IOException {
        init();
        test1();
    }

    private static void test1() throws IOException {
        InputStream resourceAsStream = JDBCTest1.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,properties);
        try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User test = userMapper.selectUser("大日如来");
            System.out.println(test.getName());
        }
    }

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
}
