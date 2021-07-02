package sun.applet;

import java.io.*;

//  没有使用双亲委派机制，对于 sun.applet.Main 这个类，系统中会存在两个 Class 实例，理论上应该是一个才对
// 但有时也是需要两个或者多个的，如tomcat里每个网站可能都有driver的不同版本，这使需要多个Drvier实例
public class UnDelegationClassLoader extends ClassLoader{


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        InputStream is = null;
        try {
            String classFilePath = this.a + name.replace(".", "/") + ".class";
            is = new FileInputStream(classFilePath);
            byte[] buf = new byte[is.available()];
            is.read(buf);
            return defineClass(name, buf, 0, buf.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }
        }
    }
    private String a;
    private UnDelegationClassLoader(String classpath) {
        super(null);
        this.a = classpath;
    }
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clz = findLoadedClass(name);
        if (clz != null) {
            return clz;
        }
        // jdk 目前对"java."开头的包增加了权限保护，这些包我们仍然交给 jdk 加载
        if (name.startsWith("java.")) {
            return ClassLoader.getSystemClassLoader().loadClass(name);
        }
        return findClass(name);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, FileNotFoundException {
        sun.applet.Main obj1 = new sun.applet.Main();
        UnDelegationClassLoader unDelegationClassLoader = new UnDelegationClassLoader("D:/Work/Dev/me/javademo/target/classes/");
//        System.out.println(unDelegationClassLoader.findClassPath());
        String name = "sun.applet.Main";
        Class<?> clz = unDelegationClassLoader.loadClass(name);
        Object obj2 = clz.newInstance();
        System.out.println("obj1 class: " + obj1.getClass());
        System.out.println("obj2 class: " + obj2.getClass());
        System.out.println("obj1 classloader: " + obj1.getClass().getClassLoader());
        System.out.println("obj2 classloader: " + obj2.getClass().getClassLoader());
        System.out.println("obj1 == obj2" + obj1 == obj2);
    }
}
