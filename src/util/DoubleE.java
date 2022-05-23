package util;

public class DoubleE {
    public final static double float_error = Math.pow(10, -4);

    public static boolean equals(double f1, double f2) {
        return Math.abs(f1 - f2) < float_error;
    }

    // f1 >= f2
    public static boolean geq(double f1, double f2) {
        return equals(f1, f2) || f1 > f2;
    }

    // f1 <= f2
    public static boolean leq(double f1, double f2) {
        return equals(f1, f2) || f1 < f2;
    }

    public static boolean inRange(double x, double start, double end) {
        return geq(x,start) && leq(x, end);
    }

    public static boolean outRange(double x, double start, double end) {
        return leq(x,start) || geq(x, end);
    }


}
