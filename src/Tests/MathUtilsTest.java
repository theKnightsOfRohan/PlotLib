package Tests;

import Plot.MathUtils;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathUtilsTest {
	static final double acceptableDelta = 0.0001;

	@Test
	public void testLinspace() {
		double min = -5.0;
		double max = 5.0;
		int num = 10;
		double[] expected = { -5.0, -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0 };
		double[] result = MathUtils.linspace(min, max, num);
		assertArrayEquals(expected, result, acceptableDelta);
	}

	@Test
	public void testApply() {
		double[] x = { 1.0, 2.0, 3.0, 4.0, 5.0 };
		DoubleUnaryOperator f = Math::sqrt;
		double[] expected = { 1.0, 1.4142135623730951, 1.7320508075688772, 2.0, 2.23606797749979 };
		double[] result = MathUtils.apply(f, x);
		assertArrayEquals(expected, result, acceptableDelta);
	}

	@Test
	public void testToList() {
		double[] arr = { 1.0, 2.0, 3.0, 4.0, 5.0 };
		List<Double> expected = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
		List<Double> result = MathUtils.toList(arr);
		assertEquals(expected, result);
	}

	@Test
	public void testToListWithIntArray() {
		int[] arr = { 1, 2, 3, 4, 5 };
		List<Double> expected = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
		List<Double> result = MathUtils.toList(arr);
		assertEquals(expected, result);
	}

	@Test
	public void testToListWithFloatArray() {
		float[] arr = { 1.0f, 2.0f, 3.0f, 4.0f, 5.0f };
		List<Double> expected = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
		List<Double> result = MathUtils.toList(arr);
		assertEquals(expected, result);
	}

	@Test
	public void testCeilToNearest() {
		double val = 7.5;
		double M = 2.0;
		double expected = 8.0;
		double result = MathUtils.ceilToNearest(val, M);
		assertEquals(expected, result, acceptableDelta);
	}

	@Test
	public void testRoundToNearest() {
		double val = 7.5;
		double M = 2.0;
		double expected = 8.0;
		double result = MathUtils.roundToNearest(val, M);
		assertEquals(expected, result, acceptableDelta);
	}

}