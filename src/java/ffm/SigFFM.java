import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;

public class SigFFM {

    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup LOOKUP;

    static {
        System.loadLibrary("sig");
        LOOKUP = SymbolLookup.loaderLookup();
    }

    private static MethodHandle downcall(String name, FunctionDescriptor descriptor) {
        return LINKER.downcallHandle(
                LOOKUP.find(name).orElseThrow(() ->
                        new RuntimeException("Fonction " + name + " non trouvée")),
                descriptor
        );
    }

    public static double hyp(double a, double b) {
        try {
            MethodHandle mh = downcall("hyp",
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_DOUBLE,  // retour
                            ValueLayout.JAVA_DOUBLE,  // param 1
                            ValueLayout.JAVA_DOUBLE   // param 2
                    )
            );
            return (double) mh.invoke(a, b);
        } catch (Throwable e) {
            throw new RuntimeException("Erreur hyp", e);
        }
    }

    public static double avg(double[] data) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, data);

            MethodHandle mh = downcall("avg",
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT
                    )
            );

            return (double) mh.invoke(seg, data.length);
        } catch (Throwable e) {
            throw new RuntimeException("Erreur avg", e);
        }
    }

    public static double var(double[] data) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, data);

            MethodHandle mh = downcall("var",
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT
                    )
            );

            return (double) mh.invoke(seg, data.length);
        } catch (Throwable e) {
            throw new RuntimeException("Erreur var", e);
        }
    }

    public static double median(double[] data) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, data);

            MethodHandle mh = downcall("median",
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT
                    )
            );

            return (double) mh.invoke(seg, data.length);
        } catch (Throwable e) {
            throw new RuntimeException("Erreur median", e);
        }
    }

    public static double[] convolve(double[] signal, double[] kernel) {
        try (Arena arena = Arena.ofConfined()) {

            MemorySegment sigSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, signal);
            MemorySegment kerSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, kernel);
            MemorySegment outSeg = arena.allocate(ValueLayout.JAVA_DOUBLE, signal.length);

            MethodHandle mh = downcall("convolve",
                    FunctionDescriptor.ofVoid(
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS
                    )
            );

            mh.invoke(sigSeg, signal.length, kerSeg, kernel.length, outSeg);

            return outSeg.toArray(ValueLayout.JAVA_DOUBLE);

        } catch (Throwable e) {
            throw new RuntimeException("Erreur convolve", e);
        }
    }

    public static double[] movingAverage(double[] signal, int windowSize) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sigSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, signal);
            MemorySegment outSeg = arena.allocate(ValueLayout.JAVA_DOUBLE, signal.length);

            MethodHandle mh = downcall("moving_average",
                    FunctionDescriptor.ofVoid(
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS
                    )
            );

            mh.invoke(sigSeg, signal.length, windowSize, outSeg);

            return outSeg.toArray(ValueLayout.JAVA_DOUBLE);

        } catch (Throwable e) {
            throw new RuntimeException("Erreur movingAverage", e);
        }
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        try {
            MethodHandle mh = downcall("distance",
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE
                    )
            );
            return (double) mh.invoke(x1, y1, x2, y2);
        } catch (Throwable e) {
            throw new RuntimeException("Erreur distance", e);
        }
    }

    public static double angle(double x1, double y1, double x2, double y2) {
        try {
            MethodHandle mh = downcall("angle",
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE
                    )
            );
            return (double) mh.invoke(x1, y1, x2, y2);
        } catch (Throwable e) {
            throw new RuntimeException("Erreur angle", e);
        }
    }

    public static double dotProduct(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vecteurs de tailles différentes");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment v1Seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, v1);
            MemorySegment v2Seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, v2);

            MethodHandle mh = downcall("dot_product",
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT
                    )
            );

            return (double) mh.invoke(v1Seg, v2Seg, v1.length);
        } catch (Throwable e) {
            throw new RuntimeException("Erreur dotProduct", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Test FFM (Java 25) ===\n");

        System.out.println("1. Hypoténuse(3, 4) = " + hyp(3, 4));

        double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("\n2. Statistiques sur [1..10]:");
        System.out.println("   Moyenne  = " + avg(data));
        System.out.println("   Variance = " + var(data));
        System.out.println("   Médiane  = " + median(data));

        double[] signal = {1, 2, 3, 4, 5};
        double[] kernel = {1, 1};
        System.out.println("\n3. Convolution [1,2,3,4,5] * [1,1]:");
        double[] conv = convolve(signal, kernel);
        System.out.println("   Résultat = " + Arrays.toString(conv));

        double[] noisy = {1, 5, 2, 8, 3, 7, 4, 6, 5};
        System.out.println("\n4. Filtre moyenneur (fenêtre=3):");
        double[] smoothed = movingAverage(noisy, 3);
        System.out.print("   Signal bruité  = " + Arrays.toString(noisy));
        System.out.print("\n   Signal lissé   = [");
        for (int i = 0; i < smoothed.length; i++) {
            System.out.printf("%.2f%s", smoothed[i], (i < smoothed.length-1 ? ", " : ""));
        }
        System.out.println("]");

        System.out.println("\n5. Géométrie:");
        System.out.println("   Distance(0,0 -> 3,4) = " + distance(0, 0, 3, 4));
        System.out.println("   Angle(1,0 -> 0,1)    = " + angle(1, 0, 0, 1) + " rad");

        double[] v1 = {1, 2, 3};
        double[] v2 = {4, 5, 6};
        System.out.println("   Produit scalaire [1,2,3]·[4,5,6] = " + dotProduct(v1, v2));

        System.out.println("\n✓ Tous les tests FFM terminés avec succès !");
    }
}

