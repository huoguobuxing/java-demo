package proxy;

// 静态代理，代理对象是写死的，只能是Car这种类型
public class StaticCarProxy implements Car {
    private Car car;

    public StaticCarProxy(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        car.run();
    }
}
