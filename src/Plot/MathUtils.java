package Plot;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/***
 * Static helper methods for doing math-related things
 */
public class MathUtils {

    /***
     * Return a double[] of num values from min to max.
     * 
     * @param min min value to start at
     * @param max max value to end at
     * @param num number of values in the array
     * @return a double[] of num values from min to max.
     */
    public static double[] linspace(double min, double max, int num) {
        double[] out = new double[num];

        double dx = (max - min) / num;
        for (int i = 0; i < out.length; i++) {
            out[i] = min;
            min += dx;
        }

        return out;
    }

    /***
     * Apply a function to each element in an input array. Returns output array of
     * return values.
     * For example:
     * double[] x = MathUtils.linspace( -6, 6, 500 );
     * double[] y = MathUtils.apply( Math::sin, x );
     *
     * double[] x2 = MathUtils.linspace( 1, 100, 100);
     * double[] squares = MathUtils.apply( (v -> v*v), x );
     * 
     * @param f function to apply to each element of x
     * @param x list of inputs
     * @return array of length x.length where element i is f(x[i])
     */
    public static double[] apply(DoubleUnaryOperator f, double[] x) {
        return Arrays.stream(x).map(f).toArray();
    }

    public static List<Double> copyDoubleList(List<Double> list) {
        return list.stream().collect(Collectors.toList());
    }

    public static List<Integer> copyIntList(List<Integer> list) {
        return list.stream().collect(Collectors.toList());
    }

    public static int[] copyArr(int[] arr) {
        return Arrays.copyOf(arr, arr.length);
    }

    public static double[] copyArr(double[] arr) {
        return Arrays.copyOf(arr, arr.length);
    }

    /***
     * Convert a double[] to List<Double>
     * 
     * @param arr input array
     * @return list<Double> containing same values as arr
     */
    public static List<Double> toList(double[] arr) {
        return DoubleStream.of(arr).boxed().collect(Collectors.toList());
    }

    public static List<Double> toList(int[] arr) {
        return Arrays.stream(arr).mapToDouble(i -> i).boxed().collect(Collectors.toList());
    }

    public static List<Double> toList(float[] arr) {
        return IntStream.range(0, arr.length).mapToDouble(i -> arr[i]).boxed().collect(Collectors.toList());
    }

    public static double ceilToNearest(double val, double M) {
        return Math.ceil(val / M) * M;
    }

    public static double roundToNearest(double val, double M) {
        return Math.round(val / M) * M;
    }
}
