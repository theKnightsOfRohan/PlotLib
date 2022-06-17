package Plot;

import processing.core.PApplet;

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
        this.width = x2-x1;
        this.height = y2-y1;
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
        if (datasets.size() == 0) return;
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
        if (data.getDataMinX() < dataMinX) dataMinX = data.getDataMinX();
        if (data.getDataMaxX() > dataMaxX) dataMaxX = data.getDataMaxX();
    }

    protected void updateYBoundsWith(PlotData data) {
        if (data.getDataMinY() < dataMinY) dataMinY = data.getDataMinY();
        if (data.getDataMaxY() > dataMaxY) dataMaxY = data.getDataMaxY();
    }

    protected void drawDataPoints(PApplet window) {
        if (needScaling) reScaleData(window);

        // TODO: remove data that's out of range if plot frozen?
        for (PlotData dataset : datasets) {
            plotDataSet(window, dataset);
        }
    }

    protected void plotDataSet(PApplet window, PlotData dataset) {
        dataset.rescale(cornerX, cornerX + width, cornerY + height, cornerY,
                this.dataMinX, this.dataMaxX, this.dataMinY, this.dataMaxY);

        // TODO: refactor so datasets draw themselves...?
        window.fill(dataset.getFillColor());
        window.stroke(dataset.getStrokeColor());
        window.strokeWeight(dataset.getStrokeWeight());

        if (dataset.getStyle() == PlotData.Style.POINT) {
            for (int i = 0; i < dataset.size(); i++) {
                window.ellipse(dataset.getDisplayX(i), dataset.getDisplayY(i), 2, 2);
            }
        } else if (dataset.getStyle() == PlotData.Style.LINE) {
            for (int i = 1; i < dataset.size(); i++) {
                float x1 = dataset.getDisplayX(i-1);
                float y1 = dataset.getDisplayY(i-1);
                float x2 = dataset.getDisplayX(i);
                float y2 = dataset.getDisplayY(i);
                window.line(x1, y1, x2, y2);
            }
        }
    }

    protected void reScaleData(PApplet window) {
        System.err.println("Warning: call to reScaleData is currently unimplemented");
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
        return Plot.map(dataX, dataMinX, dataMaxX, cornerX, cornerX + width);  // TODO: use getters for this
    }

    public double getScreenYFor(double dataY) {
        return Plot.map(dataY, dataMinY, dataMaxY, cornerY + height, cornerY);  // TODO: use getters for this
    }

    public double getDataXFor(double screenX) {
        return Plot.map(screenX, cornerX, cornerX + width, dataMinX, dataMaxX);  // TODO: use getters for this
    }

    public double getDataYFor(double screenY) {
        return Plot.map(screenY, cornerY + height, cornerY, dataMinY, dataMaxY);  // TODO: use getters for this
    }

    public void removePlot(int plotIndex) {
        // TODO: bounds check
        this.datasets.remove(plotIndex);
    }

    public void setYDataRangeMin(double dataYMin) {
        this.dataMinY = dataYMin;
    }

    public List<Integer> getXScreenCoords(int dataIndex) {
        // TODO: bounds check
        PlotData dataset = this.datasets.get(dataIndex);

        return dataset.getScreenXCoords();
    }

    public List<Integer> getYScreenCoords(int dataIndex) {
        // TODO: bounds check
        PlotData dataset = this.datasets.get(dataIndex);

        return dataset.getScreenYCoords();
    }

    public enum Setting {
        show_axes, freeze_y_scale, freeze_x_scale, show_border
    }


    // TODO: refactor so cleaner
    // TODO: fix number rounding display problem
    // TDOO: add minor grid lines
    // TODO: organize all features so easy to turn on and off
    protected class Axes {
        private static final int MIN_PIXEL_SPACING = 50;

        protected int numXLines, numYLines;
        protected double xScale, yScale;

        protected void draw(PApplet window) {
            if (getDomain() == 0 || getRange() == 0) return;

            numXLines = (width/MIN_PIXEL_SPACING);
            numYLines = (height/MIN_PIXEL_SPACING);

            xScale = calcScale(dataMinX, dataMaxX, numXLines);
            yScale = calcScale(dataMinY, dataMaxY, numYLines);

            // --------------- draw major x grid -----------------------------------------
            double startX = MathUtils.ceilToNearest(dataMinX, xScale);
            double val = startX;
            double x = getScreenXFor(val);
            int i = 0;
            while (x <= cornerX + width) {
                window.line((float)x, cornerY, (float)x, cornerY+height);
                window.textSize(10);
                window.textAlign(window.CENTER, window.CENTER);
                window.fill(0);
                window.stroke(0);
                window.text(""+val, (float)x, cornerY + height - 12);

                i++;
                val = startX + i*xScale;
                x = getScreenXFor(val);
            }

            // -------------- draw minor grid -----------------------------------
/*            double xMinorScale = getMinorScale(xScale);
            while (x >= cornerX) {
                window.stroke(127);
                window.line((float)x, cornerY, (float)x, cornerY+height);

                i--;
                val = startX + i*xMinorScale;
                x = getScreenXFor(val);
            }*/

            double startY = MathUtils.ceilToNearest(dataMinY, yScale);
            val = startY + yScale;
            double y = getScreenYFor(val);
            i = 0;
            while (y >= cornerY) {
                window.line(cornerX, (float)y, cornerX+width, (float)y);
                window.textSize(10);
                window.textAlign(window.CENTER, window.CENTER);
                window.fill(0);
                window.stroke(0);
                window.text(""+val, cornerX - 12, (float)y);

                i++;
                val = startY + i*yScale;
                y = getScreenYFor(val);
            }
        }

        private double getMinorScale(double xScale) {
            if ((""+xScale).endsWith("2")) return xScale/4;
            return xScale/5;
        }
    }

    private double getRange() {
        return dataMaxY- dataMinY;
    }

    private double getDomain() {
        return dataMaxX - dataMinX;
    }

    protected static double calcScale(double minVal, double maxVal, int numIntervals){
        double[] scale = { 1, 2, 5 };

        double in = (maxVal-minVal)/numIntervals;
        int count = 0;
        while ( in > scale[ scale.length - 1] ) {
            count++;
            in /= 10;
        }

        while ( in <= scale[ scale.length - 1]/10 ) {
            count--;
            in *= 10;
        }

        int scaleIndex = 0;
        while ( in > scale[scaleIndex] ) scaleIndex++;

        return scale[scaleIndex]*Math.pow(10, count);
    }

}
