package Tests;

import Plot.TimeSeriesPlot;
import Plot.Plot.Setting;
import Plot.PlotData;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimeSeriesPlotTest {
    static final double acceptableDelta = 0.0001;

    @Test
    public void testPlot() {
        TimeSeriesPlot plot = new TimeSeriesPlot(0, 0, 100, 100, 5);
        PlotData result = plot.plot(1.0, 2.0);
        assertEquals(1, plot.getDatasets().size());
        assertEquals(1, result.size());
        assertEquals(1.0, result.getDataX(0), acceptableDelta);
        assertEquals(2.0, result.getDataY(0), acceptableDelta);
    }

    @Test
    public void testPlotWithExistingDataset() {
        TimeSeriesPlot plot = new TimeSeriesPlot(0, 0, 100, 100, 5);
        plot.plot(1.0, 2.0);
        PlotData result = plot.plot(3.0, 4.0);
        assertEquals(1, plot.getDatasets().size());
        assertEquals(2, result.size());
        assertEquals(3.0, result.getDataX(1), acceptableDelta);
        assertEquals(4.0, result.getDataY(1), acceptableDelta);
    }

    @Test
    public void testPlotWithInvalidIndex() {
        TimeSeriesPlot plot = new TimeSeriesPlot(0, 0, 100, 100, 5);
        PlotData result = plot.plot(1, 2.0, 3.0);
        assertEquals(1, plot.getDatasets().size());
        assertEquals(1, result.size());
        assertEquals(2.0, result.getDataX(0), acceptableDelta);
        assertEquals(3.0, result.getDataY(0), acceptableDelta);
    }

    @Test
    public void testPlotWithOutOfRangeYValue() {
        TimeSeriesPlot plot = new TimeSeriesPlot(0, 0, 100, 100, 5);
        plot.set(Setting.freeze_y_scale, true);
        PlotData result = plot.plot(1.0, 10.0);
        assertEquals(0, result.size());
    }

    // @Test
    // public void testShiftXBounds() {
    // TimeSeriesPlot plot = new TimeSeriesPlot(0, 0, 100, 100, 5);
    // PlotData data = new PlotData();
    // data.add(1.0, 2.0);
    // data.add(3.0, 4.0);
    // plot.shiftXBounds(data);
    // assertEquals(1.0, plot.getDataMinX());
    // assertEquals(3.0, plot.getDataMaxX());
    // assertEquals(1.0, data.getDataMinX());
    // assertEquals(3.0, data.getDataMaxX());
    // }
}