package proxy;

public class AudiCar implements Car, Machine {
    @Override
    public void run() {
        System.out.println("I am audi.");
    }

    @Override
    public void start() {
        System.out.println("start totototo....");
    }

    @Override
    public void stop() {
        System.out.println("end ....");
    }
}
