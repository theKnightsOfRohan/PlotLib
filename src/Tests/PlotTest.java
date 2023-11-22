package Tests;

import Plot.Plot;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlotTest {
	@Test
	public void testMap() {
		double result = Plot.map(5, 0, 10, 0, 100);
		assertEquals(50, result, 0.001);

		result = Plot.map(2, 0, 5, 10, 20);
		assertEquals(14, result, 0.001);

		result = Plot.map(8, 0, 10, -100, 100);
		assertEquals(160, result, 0.001);
	}
}