package annotation;

public class Test {
    public static void main(String[] args) {
        try {
            test1();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static void test1() {
        AuthorAnotation[] annotations = Tools.class.getAnnotationsByType(AuthorAnotation.class);
        for (AuthorAnotation annotation : annotations) {
            System.out.println(annotation.firstName());
            System.out.println(annotation.lastName());
        }

    }
}
