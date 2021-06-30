package reflect;

import temp.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class reflectTest1 {
    public static void main(String[] args) {
        try {
//           test1();
            test2();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void test2() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class c = User.class;
        for (Method method : c.getMethods()) {
            System.out.println(method.getName());
            if (method.getName().equalsIgnoreCase("output")) {
                method.invoke(c.newInstance(),"123");
            }
        }
        Method output = c.getMethod("output",String.class);
        output.invoke(c.newInstance(), "123");

    }

    private static void test1() throws ClassNotFoundException {
        Class c = Class.forName("java.util.ArrayList");
        Constructor[] constructors = c.getConstructors();
        for (Constructor constructor : constructors) {
            System.out.println(constructor.getName());
            Class[] parameterTypes = constructor.getParameterTypes();
            for (Class parameterType : parameterTypes) {
                System.out.println(parameterType.getName());
            }
        }

        Field[] fields = c.getFields();
        for (Field field : fields) {
            System.out.println(field.getName());
        }
    }
}
