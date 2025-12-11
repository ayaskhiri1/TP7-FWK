
public class SigJNI {
    static {
        System.loadLibrary("sig");
    }

    public static native double hyp(double a, double b);

    public static native double avg(double[] data);

    public static native double var(double[] data);

    public static native double median(double[] data);

    public static native double[] convolve(double[] signal, double[] kernel);

    public static native double[] movingAverage(double[] signal, int windowSize);

    public static native double distance(double x1, double y1, double x2, double y2);

    public static native double angle(double x1, double y1, double x2, double y2);

    public static native double dotProduct(double[] v1, double[] v2);

    public static void main(String[] args) {
        System.out.println("=== Test JNI ===\n");

        // Test 1: Hypoténuse
        System.out.println("1. Hypoténuse(3, 4) = " + hyp(3, 4));

        // Test 2: Statistiques
        double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("\n2. Statistiques sur [1..10]:");
        System.out.println("   Moyenne  = " + avg(data));
        System.out.println("   Variance = " + var(data));
        System.out.println("   Médiane  = " + median(data));

        // Test 3: Convolution
        double[] signal = {1, 2, 3, 4, 5};
        double[] kernel = {1, 1};
        System.out.println("\n3. Convolution [1,2,3,4,5] * [1,1]:");
        double[] conv = convolve(signal, kernel);
        System.out.print("   Résultat = [");
        for (int i = 0; i < conv.length; i++) {
            System.out.print(conv[i] + (i < conv.length-1 ? ", " : ""));
        }
        System.out.println("]");

        // Test 4: Filtre moyenneur
        double[] noisy = {1, 5, 2, 8, 3, 7, 4, 6, 5};
        System.out.println("\n4. Filtre moyenneur (fenêtre=3):");
        double[] smoothed = movingAverage(noisy, 3);
        System.out.print("   Signal bruité  = [");
        for (int i = 0; i < noisy.length; i++) {
            System.out.print(noisy[i] + (i < noisy.length-1 ? ", " : ""));
        }
        System.out.println("]");
        System.out.print("   Signal lissé   = [");
        for (int i = 0; i < smoothed.length; i++) {
            System.out.printf("%.2f%s", smoothed[i], (i < smoothed.length-1 ? ", " : ""));
        }
        System.out.println("]");

        // Test 5: Géométrie
        System.out.println("\n5. Géométrie:");
        System.out.println("   Distance(0,0 -> 3,4) = " + distance(0, 0, 3, 4));
        System.out.println("   Angle(1,0 -> 0,1)    = " + angle(1, 0, 0, 1) + " rad");

        double[] v1 = {1, 2, 3};
        double[] v2 = {4, 5, 6};
        System.out.println("   Produit scalaire [1,2,3]·[4,5,6] = " + dotProduct(v1, v2));

        System.out.println("\n✓ Tous les tests JNI terminés avec succès !");
    }
}