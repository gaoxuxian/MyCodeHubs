
public class Test {
    static class Father {
        public static final String TAG = "Father";
        static {
            System.out.println(TAG);
            System.out.println("static Father");
        }
        public static final String s = "sFather";
        {
            System.out.println(s);
            System.out.println("unStatic Father");
        }

        public Father() {
            System.out.println("constract Father");
            method();
        }

        public void method() {
            System.out.println("method Father");
        }

        @Override
        public String toString() {
            return "toString Father";
        }
    }

    static class Son extends Father {
        public static Son instance = new Son();

        static {
            System.out.println("static Son");
        }

        {
            System.out.println("unStatic Son");
        }

        public Son() {
            System.out.println("constract Son");
            method();
        }

        public void method() {
            System.out.println("method Son");
        }

        @Override
        public String toString() {
            return "toString Son";
        }
    }

    public static void main(String[] args) {
        System.out.println("1.---------------------");
        System.out.println(Son.TAG);
        Son[] sons = new Son[10];
        System.out.println(sons);
        System.out.println("2.---------------------");
        System.out.println(Son.instance);
        System.out.println("3.---------------------");
        Son son = new Son();
        Father father = son;
        father.method();
        System.out.println(son);
    }
}

//结果
//1.---------------------
//Father
//[LTest$Son;@d716361
//2.---------------------
//static Father
//unStatic Father
//constract Father
//method Son
//unStatic Son
//constract Son
//method Son
//static Son
//toString Son
//3.---------------------
//unStatic Father
//constract Father
//method Son
//unStatic Son
//constract Son
//method Son
//method Son
//toString Son
