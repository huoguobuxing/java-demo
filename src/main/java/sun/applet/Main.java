package sun.applet;

public class Main {
    static {
        System.out.println("customized sun.applet.Main constructed");
    }
    public static void main(String[] args) {
        System.out.println("recognized as sun.applet.Main in jdk," +
                " and there isn't any main method");
    }
}
