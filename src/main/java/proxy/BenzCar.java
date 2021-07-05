package proxy;

public class BenzCar implements Car , Machine{
    @Override
    public void run() {
        System.out.println("I am benz.");
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
