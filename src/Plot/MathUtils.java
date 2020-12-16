package Plot;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/***
 * Static helper methods for doing math-related things
 */
public class MathUtils {

    /***
     * Return a double[] of num values from min to max.
     * @param min min value to start at
     * @param max max value to end at
     * @param num number of values in the array
     * @return  a double[] of num values from min to max.
     */
    public static double[] linspace(double min, double max, int num) {
        double[] out = new double[num];

        double dx = (max-min)/num;
        for (int i = 0; i < out.length; i++) {
            out[i] = min;
            min += dx;
        }

        return out;
    }

    /***
     * Apply a function to each element in an input array.  Returns output array of return values.
     * For example:
     * double[] x = MathUtils.linspace( -6, 6, 500 );
     * double[] y = MathUtils.apply( Math::sin, x );
     *
     * double[] x2 = MathUtils.linspace( 1, 100, 100);
     * double[] squares = MathUtils.apply( (v -> v*v), x );
     * @param f function to apply to each element of x
     * @param x list of inputs
     * @return array of length x.length where element i is f(x[i])
     */
    public static double[] apply(DoubleUnaryOperator f, double[] x) {
        return Arrays.stream(x).map(f).toArray();
    }

    /***
     * Convert a double[] to List<Double>
     * @param arr input array
     * @return list<Double> containing same values as arr
     */
    public static List<Double> toList(double[] arr) {
        return DoubleStream.of(arr).boxed().collect(Collectors.toList());
    }

    // TODO: make overloaded version of toList for int[]
    // TODO: make overloaded version of toList for float[]  (because processing uses float)
}
