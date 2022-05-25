package util;

public class Angle {
    public static double bisectAngle(double angle1, double angle2) {
        double outAng;
        if (angle1 <= angle2) {
            outAng = angle1 + (angle2 - angle1) / 2;
        } else {
            outAng = angle1 + (angle2 - angle1) / 2 + Math.PI;
        }
        while (outAng >= Math.PI *2) {
            outAng -= Math.PI *2;
        }
        return outAng;
    }

    // take a distance and 2 angles
    // angle1 is the
    public static double equidistantLength(double d, double angle1, double angle2) {
        double bAngle = bisectAngle(angle1, angle2);
        double splitAngle = bAngle - angle1;
        double tmp = d / Math.sin(splitAngle);
        return tmp;
    }

    public static double bound(double angle) {
        double temp = angle;
        while (temp < 0) {
            temp += 2*Math.PI;
        }
        while (DoubleE.geq(temp, 2*Math.PI)) {
            temp -= 2*Math.PI;
        }
        return temp;
    }
}
