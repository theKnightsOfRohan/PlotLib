public class MathUtils {
    public static double[] linspace(double min, double max, int num) {
        double[] out = new double[num];

        double dx = (max-min)/num;
        for (int i = 0; i < out.length; i++) {
            out[i] = min;
            min += dx;
        }

        return out;
    }
}
