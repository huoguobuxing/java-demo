package classloader;

import java.lang.*;
import java.lang.ClassLoader;
import java.lang.Exception;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.Integer;
import java.lang.String;
import java.lang.System;

public class Test {
    public static void main(String[] args) throws java.lang.ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
//            test1();
//            test2();
            test3();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    static void test3(){
        ClassLoader classLoader = Test.class.getClassLoader();
        System.out.println(classLoader);
        ClassLoader parent = classLoader.getParent();
        System.out.println(parent);
        ClassLoader parent1 = parent.getParent();
        System.out.println(parent1);
    }

    // 自己写的类是通过AppClassLoader加载的
    // Integer这种使用BootstrapClassLoader加载的
    static void test2() {
        ClassLoader classLoader = Test.class.getClassLoader();
        System.out.println(classLoader);
        ClassLoader classLoader1 = Integer.class.getClassLoader();
        System.out.println(classLoader1);
        System.out.println("done");
    }

    // 三种类加载器分别从下边三个位置加载class
    static void test1() {
        test("sun.boot.class.path");
        test("java.ext.dirs");
        test("java.class.path");
    }

    private static void test(String location) {
        System.out.println("-----------" + location + "--------------");
        String property = System.getProperty(location);
        String[] split = property.split(";");
        for (String s : split) {
            System.out.println(s);
        }
    }
}
