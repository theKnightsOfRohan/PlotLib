import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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

    public static double[] apply(DoubleUnaryOperator f, double[] x) {
        return Arrays.stream(x).map(f).toArray();
    }

    public static List<Double> toList(double[] arr) {
        return DoubleStream.of(arr).boxed().collect(Collectors.toList());
    }
}
