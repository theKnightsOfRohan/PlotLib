package Tests;

import Plot.PlotData;
import Plot.ScatterPlot;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ScatterPlotTest {
	private ScatterPlot scatterPlot;

	@Before
	public void setup() {
		scatterPlot = new ScatterPlot(0, 0, 100, 100);
	}

	@Test
	public void testPlot() {
		double[] xData = { 1.0, 2.0, 3.0 };
		double[] yData = { 4.0, 5.0, 6.0 };

		PlotData data = scatterPlot.plot(xData, yData);

		// Verify that the data is added to the scatter plot
		assertTrue(scatterPlot.getDatasets().contains(data));
	}

	@Test
	public void testPlotWithIndex() {
		double[] xData = { 1.0, 2.0, 3.0 };
		double[] yData = { 4.0, 5.0, 6.0 };

		PlotData data = scatterPlot.plot(0, xData[0], yData[0]);

		// Verify that the data is added to the scatter plot
		assertTrue(scatterPlot.getDatasets().contains(data));
	}

	@Test
	public void testPlotWithoutIndex() {
		double[] xData = { 1.0, 2.0, 3.0 };
		double[] yData = { 4.0, 5.0, 6.0 };

		PlotData data = scatterPlot.plot(xData[0], yData[0]);

		// Verify that the data is added to the scatter plot
		assertTrue(scatterPlot.getDatasets().contains(data));
	}

	@Test
	public void testPlotWithInvalidIndex() {
		double[] xData = { 1.0, 2.0, 3.0 };
		double[] yData = { 4.0, 5.0, 6.0 };

		int invalidIndex = 10;
		PlotData data = scatterPlot.plot(invalidIndex, xData[0], yData[0]);

		// Verify that a new data object is created and added to the scatter plot
		assertTrue(scatterPlot.getDatasets().contains(data));
	}
}