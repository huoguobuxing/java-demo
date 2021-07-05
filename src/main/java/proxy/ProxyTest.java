package proxy;


import java.lang.reflect.Proxy;

public class ProxyTest {
    public static void main(String[] args) {
        try {
//            test1();
//            test2();
            test3();
//            test4();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static void test4() {
        CustomCglibProxy customCglibProxy = new CustomCglibProxy();
        Car proxyObject = (Car) customCglibProxy.createProxyObject(AudiCar.class);
        proxyObject.run();
    }

    static void test3() {
        Object audiCar = new AudiCar();
        Car car = (Car) Proxy.newProxyInstance(audiCar.getClass().getClassLoader(), audiCar.getClass().getInterfaces(), new CustomInvocationHandlerProxy(audiCar));
        car.run();

        Machine machine = (Machine)car;
        machine.start();
        machine.stop();
    }

    // 静态代理
    static void test2() {
        Car carProxy = new StaticCarProxy(new BenzCar());
        carProxy.run();
    }

    // 正常测试，多态
    static void test1() {
        Car benz = new BenzCar();
        benz.run();
        Car audi = new AudiCar();
        audi.run();
    }
}
