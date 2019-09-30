package trunk.gles.view;

public class ABC {
    public float a = 2.912f;

    public double t(double x, double y) {
        double ePow = -(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(a, 2));
        double e = Math.pow(Math.E, ePow);
        return 1 * e / (2 * Math.PI * Math.pow(a, 2));
    }

    public double t(float x) {
        double ePow = -Math.pow(x, 2) / (2 * Math.pow(a, 2));
        double e = Math.pow(Math.E, ePow);
        return 1 * e / (Math.sqrt(2 * Math.PI) * a);
    }

    /*public static void main(String[] args) {
        ABC abc = new ABC();
        int n = 6;
        int size = n * 2 + 1;
        int z = 0;
        double[] arr = new double[size * size];
        for (int i = n; i >= -n; i--) {
            for (int j = -n; j <= n; j++) {
                double dy = i;
                double dx = j;

                double t = abc.t(0 + dx, 0 + dy);
                arr[z] = t;
                z++;
                System.out.print("[" + (0 + dx) + ", " + (0 + dy) *//*+ ", " + t*//* + "]  ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();
        System.out.println();

        for (int i = 0; i < z; i++) {
            System.out.print("[" + arr[i] + "]  ");
            if ( ((i + 1) % size) == 0) {
                System.out.println();
            }
        }
    }*/

    public static void main(String[] args) {
        ABC abc = new ABC();
        int n = 6;
        int size = n * 2 + 1;
        int z = 0;
        double[] arr = new double[size * size];
        for (int i = -n; i <= n; i++) {
            System.out.print("[" + (i) + ", "+ 0 +"]  ");
            double t = abc.t(i);
            arr[z] = t;
            z++;
        }

        System.out.println();
        System.out.println();
        System.out.println();

        for (int i = 0; i < z; i++) {
            System.out.print("[" + arr[i] + "]  ");
        }
    }
}
