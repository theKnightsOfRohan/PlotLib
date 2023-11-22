package Tests;

import org.junit.jupiter.api.Test;
import Plot.PlotData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlotDataTest {
    @Test
    public void testAdd() {
        PlotData plotData = new PlotData();
        plotData.add(1.0, 2.0);
        plotData.add(3.0, 4.0);
        assertEquals(2, plotData.size());
        assertEquals(1.0, plotData.getDataX(0), 0.001);
        assertEquals(2.0, plotData.getDataY(0), 0.001);
        assertEquals(3.0, plotData.getDataX(1), 0.001);
        assertEquals(4.0, plotData.getDataY(1), 0.001);
    }

    @Test
    public void testRemove() {
        PlotData plotData = new PlotData();
        plotData.add(1.0, 2.0);
        plotData.add(3.0, 4.0);
        plotData.remove(0);
        assertEquals(1, plotData.size());
        assertEquals(3.0, plotData.getDataX(0), 0.001);
        assertEquals(4.0, plotData.getDataY(0), 0.001);
    }

    @Test
    public void testSetDataMinX() {
        PlotData plotData = new PlotData();
        plotData.add(1.0, 2.0);
        plotData.add(3.0, 4.0);
        plotData.setDataMinX(0.0);
        assertEquals(0.0, plotData.getDataMinX(), 0.001);
    }

    @Test
    public void testSetDataMaxX() {
        PlotData plotData = new PlotData();
        plotData.add(1.0, 2.0);
        plotData.add(3.0, 4.0);
        plotData.setDataMaxX(5.0);
        assertEquals(5.0, plotData.getDataMaxX(), 0.001);
    }

    @Test
    public void testFillColor() {
        PlotData plotData = new PlotData();
        plotData.fillColor("red");
        assertEquals(0xFFFF0000, plotData.getFillColor());
    }

    @Test
    public void testStrokeColor() {
        PlotData plotData = new PlotData();
        plotData.strokeColor("blue");
        assertEquals(0xFF0000FF, plotData.getStrokeColor());
    }

    @Test
    public void testStrokeWeight() {
        PlotData plotData = new PlotData();
        plotData.strokeWeight(2);
        assertEquals(2, plotData.getStrokeWeight(), 0.001);
    }

    @Test
    public void testStyle() {
        PlotData plotData = new PlotData();
        plotData.style(".");
        assertEquals(PlotData.Style.POINT, plotData.getStyle());
        plotData.style("-");
        assertEquals(PlotData.Style.LINE, plotData.getStyle());
    }
}