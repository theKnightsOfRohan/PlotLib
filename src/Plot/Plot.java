package Plot;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Plot {
    private static final int AXIS_STROKE_WEIGHT = 2;

    // ---- STYLE ----
    protected int cornerX;
    protected int cornerY;
    protected int width;
    protected int height;
    protected HashMap<Setting, Boolean> settings;
    protected Axes axes;

    // ---- DATA ----
    protected ArrayList<PlotData> datasets;
    protected double dataMinX;
    protected double dataMinY;
    protected double dataMaxX;
    protected double dataMaxY;
    protected boolean needScaling = true;

    /***
     * Create a plot from upper-left corner (x1, y1) to lower right corner (x2, y2)
     * 
     * @param x1 x coord of upper left corner
     * @param y1 y coord of upper left corner
     * @param x2 x coord of lower right corner
     * @param y2 y coord of lower right corner
     */
    public Plot(int x1, int y1, int x2, int y2) {
        if (x2 <= x1) {
            System.err.println("x1 must be smaller than x2");
        }
        if (y2 <= y1) {
            System.err.println("y1 must be smaller than y2");
        }

        this.cornerX = x1;
        this.cornerY = y1;
        this.width = x2 - x1;
        this.height = y2 - y1;
        this.datasets = new ArrayList<>();
        settings = new HashMap<Setting, Boolean>();
        this.axes = new Axes();
    }

    public static double map(double target, double low1, double high1, double low2, double high2) {
        return ((target - low1) / (high1 - low1)) * (high2 - low2) + low2;
    }

    public void set(Setting setting, boolean value) {
        settings.put(setting, value);
    }

    public void setYDataRange(int min, int max) {
        this.dataMinY = min;
        this.dataMaxY = max;
    }

    public void draw(PApplet window) {
        for (PlotData dataset : datasets) {
            if (dataset.isDirty()) {
                updateDataBoundsWith(dataset);
                dataset.setClean();
            }
        }

        drawAxes(window);
        drawDataPoints(window);
    }

    private void reScaleFromData() {
        if (datasets.size() == 0)
            return;
        dataMinX = 0;
        dataMinY = 0;
        dataMaxX = 0;
        dataMaxY = 0;

        for (PlotData dataset : datasets) {
            updateDataBoundsWith(dataset);
            dataset.setClean();
        }

        needScaling = false;
    }

    public abstract PlotData plot(int index, double x, double y);

    protected boolean inDataYRange(double y) {
        return (this.dataMinY <= y && y <= this.dataMaxY);
    }

    public PlotData plot(double x, double y) {
        return plot(0, x, y);
    }

    protected void updateDataBoundsWith(PlotData data) {
        if (!settings.containsKey(Setting.freeze_y_scale)) {
            updateYBoundsWith(data);
        }

        if (!settings.containsKey(Setting.freeze_x_scale)) {
            updateXBoundsWith(data);
        }
    }

    protected void updateXBoundsWith(PlotData data) {
        if (data.getDataMinX() < dataMinX)
            dataMinX = data.getDataMinX();
        if (data.getDataMaxX() > dataMaxX)
            dataMaxX = data.getDataMaxX();
    }

    protected void updateYBoundsWith(PlotData data) {
        if (data.getDataMinY() < dataMinY)
            dataMinY = data.getDataMinY();
        if (data.getDataMaxY() > dataMaxY)
            dataMaxY = data.getDataMaxY();
    }

    protected void drawDataPoints(PApplet window) {
        if (needScaling)
            reScaleData(window);

        // TODO: remove data that's out of range if plot frozen?
        for (PlotData dataset : datasets) {
            plotDataSet(window, dataset);
        }
    }

    protected void plotDataSet(PApplet window, PlotData dataset) {
        dataset.rescale(cornerX, cornerX + width, cornerY + height, cornerY,
                this.dataMinX, this.dataMaxX, this.dataMinY, this.dataMaxY);

        dataset.drawSelf(window);
    }

    protected void reScaleData(PApplet window) {
        System.err.println("Warning: call to reScaleData is currently unimplemented");
        this.needScaling = false;
    }

    protected void printDebugInfo() {
        System.out.println("Datax: [" + dataMinX + ", " + dataMaxX + "]");
        System.out.println("Datay: [" + dataMinY + ", " + dataMaxY + "]");
        System.out.println("screen x: [" + cornerX + ", " + cornerX + width + "]");
        System.out.println("screen y: [" + cornerY + ", " + cornerY + height + "]");
    }

    protected void drawAxes(PApplet window) {
        if (settings.containsKey(Setting.show_axes)) {
            int axisX = (int) getScreenXFor(0);
            int axisY = (int) getScreenYFor(0);
            window.strokeWeight(AXIS_STROKE_WEIGHT);
            window.stroke(0);
            window.line(cornerX, axisY, cornerX + width, axisY);
            window.line(axisX, cornerY, axisX, cornerY + height);
            window.strokeWeight(1);
        }

        if (settings.containsKey(Setting.show_border)) {
            window.noFill();
            window.stroke(PlotData.BLACK);
            window.rect(cornerX, cornerY, width, height);
        }

        axes.draw(window);
    }

    public double getScreenXFor(double dataX) {
        return Plot.map(dataX, dataMinX, dataMaxX, cornerX, cornerX + width); // TODO: use getters for this
    }

    public double getScreenYFor(double dataY) {
        return Plot.map(dataY, dataMinY, dataMaxY, cornerY + height, cornerY); // TODO: use getters for this
    }

    public double getDataXFor(double screenX) {
        return Plot.map(screenX, cornerX, cornerX + width, dataMinX, dataMaxX); // TODO: use getters for this
    }

    public double getDataYFor(double screenY) {
        return Plot.map(screenY, cornerY + height, cornerY, dataMinY, dataMaxY); // TODO: use getters for this
    }

    public void removePlot(int plotIndex) {
        if (plotIndex < 0 || plotIndex >= this.datasets.size()) {
            System.err.println("Error: plotIndex out of bounds");
            return;
        }
        this.datasets.remove(plotIndex);
    }

    public void setYDataRangeMin(double dataYMin) {
        this.dataMinY = dataYMin;
    }

    public List<Integer> getXScreenCoords(int dataIndex) {
        if (dataIndex < 0 || dataIndex >= this.datasets.size()) {
            System.err.println("Error: dataIndex out of bounds");
            return null;
        }
        PlotData dataset = this.datasets.get(dataIndex);

        return dataset.getScreenXCoords();
    }

    public List<Integer> getYScreenCoords(int dataIndex) {
        if (dataIndex < 0 || dataIndex >= this.datasets.size()) {
            System.err.println("Error: dataIndex out of bounds");
            return null;
        }
        PlotData dataset = this.datasets.get(dataIndex);

        return dataset.getScreenYCoords();
    }

    public void setTextSize(int xAxisTextSize, int yAxisTextSize) {
        axes.xAxisTextSize = xAxisTextSize;
        axes.yAxisTextSize = yAxisTextSize;
    }

    public void setTextSize(int textSize) {
        axes.xAxisTextSize = textSize;
        axes.yAxisTextSize = textSize;
    }

    public void setXAxisTextYAdjustement(float amt) {
        this.axes.xAxisTextYAdjust = amt;
    }

    public void setYAxisTextXAdjustment(float amt) {
        this.axes.yAxisTextXAdjust = amt;
    }

    public enum Setting {
        show_axes, freeze_y_scale, freeze_x_scale, show_border
    }

    // TODO: refactor so cleaner
    // TODO: add minor grid lines
    // TODO: organize all features so easy to turn on and off
    protected class Axes {
        private static final int MIN_PIXEL_SPACING = 50;
        public float yAxisTextXAdjust, xAxisTextYAdjust;

        protected int numXLines, numYLines;
        protected double xScale, yScale;
        protected int xScaleSigFigs, yScaleSigFigs;

        protected int xAxisTextSize = 10;
        protected int yAxisTextSize = 10;

        protected void draw(PApplet window) {
            if (getDomain() == 0 || getRange() == 0)
                return;

            numXLines = (width / MIN_PIXEL_SPACING);
            numYLines = (height / MIN_PIXEL_SPACING);

            double[] xScaleInfo = calcScale(dataMinX, dataMaxX, numXLines);
            double[] yScaleInfo = calcScale(dataMinY, dataMaxY, numYLines);
            this.xScale = xScaleInfo[0];
            this.yScale = yScaleInfo[0];
            this.xScaleSigFigs = Math.max(0, -(int) xScaleInfo[1]); // 2 decimals is 10^(-2). -2 --> 2
            this.yScaleSigFigs = Math.max(0, -(int) yScaleInfo[1]); // no decimals might be 10^(2). 2 --> -2, but max to
                                                                    // 0

            // --------------- draw major x grid -----------------------------------------
            double startX = MathUtils.ceilToNearest(dataMinX, xScale);

            double val = startX;
            double x = getScreenXFor(val);
            int i = 0;
            while (x <= cornerX + width) {
                window.line((float) x, cornerY, (float) x, cornerY + height);
                window.textSize(xAxisTextSize);
                window.textAlign(window.CENTER, window.CENTER);
                window.fill(0);
                window.stroke(0);
                String value = String.format("%." + this.xScaleSigFigs + "f", val);
                window.textAlign(PConstants.CENTER);
                window.text(value, (float) x, getBottomY() + xAxisTextSize + getXAxisTextYAdjust());

                i++;
                val = startX + i * xScale;
                x = getScreenXFor(val);
            }

            // -------------- draw minor grid -----------------------------------
            /*
             * double xMinorScale = getMinorScale(xScale);
             * while (x >= cornerX) {
             * window.stroke(127);
             * window.line((float)x, cornerY, (float)x, cornerY+height);
             * 
             * i--;
             * val = startX + i*xMinorScale;
             * x = getScreenXFor(val);
             * }
             */

            double startY = MathUtils.ceilToNearest(dataMinY, yScale);
            val = startY + yScale;
            double y = getScreenYFor(val);
            i = 0;
            while (y >= cornerY) {
                window.line(cornerX, (float) y, cornerX + width, (float) y);
                window.textSize(yAxisTextSize);
                window.textAlign(window.CENTER, window.CENTER);
                window.fill(0);
                window.stroke(0);

                String value = String.format("%." + this.yScaleSigFigs + "f", val);
                window.textAlign(PConstants.RIGHT);
                window.text(value, getLeftX() + -yAxisTextXAdjust / 2 + getYAxisTextXAdjust(), (float) y);

                i++;
                val = startY + i * yScale;
                y = getScreenYFor(val);
            }
        }

        private double getMinorScale(double xScale) {
            if (("" + xScale).endsWith("2"))
                return xScale / 4;
            return xScale / 5;
        }
    }

    private float getXAxisTextYAdjust() {
        return this.axes.xAxisTextYAdjust;
    }

    public float getYAxisTextXAdjust() {
        return this.axes.yAxisTextXAdjust;
    }

    public float getBottomY() {
        return cornerY + height;
    }

    public float getTopY() {
        return cornerY;
    }

    public float getLeftX() {
        return cornerX;
    }

    public float getRightX() {
        return cornerX + width;
    }

    private double getRange() {
        return dataMaxY - dataMinY;
    }

    private double getDomain() {
        return dataMaxX - dataMinX;
    }

    protected static double[] calcScale(double minVal, double maxVal, int numIntervals) {
        double[] scale = { 1, 2, 5 };

        double in = (maxVal - minVal) / numIntervals;
        int count = 0;
        while (in > scale[scale.length - 1]) {
            count++;
            in /= 10;
        }

        while (in <= scale[scale.length - 1] / 10) {
            count--;
            in *= 10;
        }

        int scaleIndex = 0;
        while (in > scale[scaleIndex])
            scaleIndex++;

        return new double[] { scale[scaleIndex] * Math.pow(10, count), count };
    }

    public List<PlotData> getDatasets() {
        return this.datasets;
    }
}
