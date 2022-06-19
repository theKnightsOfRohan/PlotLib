package Plot;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Plot {
    private static final int AXIS_STROKE_WEIGHT = 2;
    private static final float MIN_X_ZOOM_THRESHOLD = 50;
    private static final float MIN_Y_ZOOM_THRESHOLD = 50;

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
    protected double dataViewMinX, dataViewMaxX;
    protected double dataViewMinY, dataViewMaxY;
    protected boolean overrideDataView = false;
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

    public boolean containsMouse(PApplet window) {
        return isInBounds(window.mouseX, window.mouseY);
    }

    public double getDataViewMinX() {
        if (overrideDataView) return dataViewMinX;
        return dataMinX;
    }

    public double getDataViewMinY() {
        if (overrideDataView) return dataViewMinY;
        return dataMinY;
    }

    public double getDataViewMaxX() {
        if (overrideDataView) return dataViewMaxX;
        return dataMaxX;
    }

    public double getDataViewMaxY() {
        if (overrideDataView) return dataViewMaxY;
        return dataMaxY;
    }

    public void zoomIn(double zoomAmount, double reCenterAmount, double targetX, double targetY) {
        targetX = getDataXFor(targetX);
        targetY = getDataYFor(targetY);

        double newWidth = (getDataViewMaxX() - getDataViewMinX())*(1.0/(1 + zoomAmount));
        double newHeight = (getDataViewMaxY() - getDataViewMinY())*(1.0/(1 + zoomAmount));

        double newCenterX = getDataViewCenterX() + (targetX - getDataViewCenterX())*reCenterAmount;
        double newCenterY = getDataViewCenterY() + (targetY - getDataViewCenterY())*reCenterAmount;

        this.dataViewMinX = newCenterX - newWidth/2;
        this.dataViewMaxX = newCenterX + newWidth/2;
        this.dataViewMinY = newCenterY - newHeight/2;
        this.dataViewMaxY = newCenterY + newHeight/2;
        this.overrideDataView = true;
    }

    private double getDataViewCenterY() {
        return getDataViewMinY() + (getDataViewMaxY() - getDataViewMinY())/2;
    }

    private double getDataViewCenterX() {
        return getDataViewMinX() + (getDataViewMaxX() - getDataViewMinX())/2;
    }

    public void resetViewBoundaries() {
        this.overrideDataView = false;
        this.dataViewMaxX = this.dataMaxX;
        this.dataViewMinX = this.dataMinX;
        this.dataViewMaxY = this.dataMinY;
        this.dataViewMinY = this.dataMinY;
    }

    public void zoomViewTo(double minX, double minY, double maxX, double maxY) {
        this.overrideDataView = true;
        this.dataViewMinX = minX;
        this.dataViewMinY = minY;
        this.dataViewMaxX = maxX;
        this.dataViewMaxY = maxY;
    }

    private void debugPrintCurrentDataView() {
        System.out.println(getDataViewMinX() + ", " + getDataViewMinY() + " to " + getDataViewMaxX() + ", " + getDataViewMaxY());
    }

    /***
     * Zoom plot view to data ranges corresponding to current screen coordinates (x, y) to (x1, y1)
     * @param x x coordinate of upper left corner of region to zoom to
     * @param y y coordinate of upper left corner of region to zoom to
     * @param x1 x coordiante of lower right corner of region to zoom to
     * @param y1 y coordinate of lower right corner of region to zoom to
     */
    public void zoomViewToScreenCoordinates(float x, float y, float x1, float y1) {
        if (Math.abs(x - x1) < MIN_X_ZOOM_THRESHOLD ||
            Math.abs(y - y1) < MIN_Y_ZOOM_THRESHOLD) {
            System.err.println("Tried to zoom into too small a region");
            return;
        }
        double dx = getDataXFor(x);
        double dx2 = getDataXFor(x1);
        double dy = getDataYFor(y);
        double dy2 = getDataYFor(y1);
        double minx = Math.min(dx, dx2);
        double maxx = Math.max(dx, dx2);
        double miny = Math.min(dy, dy2);
        double maxy = Math.max(dy, dy2);

        zoomViewTo(minx, miny, maxx, maxy);
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
                getDataViewMinX(), getDataViewMaxX(), getDataViewMinY(), getDataViewMaxY());

        // TODO: refactor so datasets draw themselves...?
        window.fill(dataset.getFillColor());
        window.stroke(dataset.getStrokeColor());
        window.strokeWeight(dataset.getStrokeWeight());

        if (dataset.getStyle() == PlotData.Style.POINT) {
            for (int i = 0; i < dataset.size(); i++) {
                ellipse(window, dataset.getDisplayX(i), dataset.getDisplayY(i), 2, 2);
            }
        } else if (dataset.getStyle() == PlotData.Style.LINE) {
            for (int i = 1; i < dataset.size(); i++) {
                float x1 = dataset.getDisplayX(i-1);
                float y1 = dataset.getDisplayY(i-1);
                float x2 = dataset.getDisplayX(i);
                float y2 = dataset.getDisplayY(i);
                line(window, x1, y1, x2, y2);
            }
        }
    }

    private void line(PApplet window, float x1, float y1, float x2, float y2) {
        if (isInBounds(x1, y1) && isInBounds(x2, y2)) {
            window.line(x1, y1, x2, y2);
        }
    }

    private void ellipse(PApplet window, float displayX, float displayY, float w, float h) {
        if (isInBounds(displayX + w/2, displayY + h/2) && isInBounds(displayX - w/2, displayY - h/2)) {
            window.ellipse(displayX, displayY, w, h);
        }
    }

    private boolean isInBounds(float x, float y) {
        if (x < getLeftX() || x > getRightX()) return false;
        if (y < getTopY() || y > getBottomY()) return false;
        return true;
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
            window.stroke(0);       // TODO: only draw axes if in bounds for plot?!
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
        return Plot.map(dataX, getDataViewMinX(), getDataViewMaxX(), cornerX, cornerX + width);
    }

    public double getScreenYFor(double dataY) {
        return Plot.map(dataY, getDataViewMinY(), getDataViewMaxY(), cornerY + height, cornerY);
    }

    public double getDataXFor(double screenX) {
        return Plot.map(screenX, cornerX, cornerX + width, getDataViewMinX(), getDataViewMaxX());
    }

    public double getDataYFor(double screenY) {
        return Plot.map(screenY, cornerY + height, cornerY, getDataViewMinY(), getDataViewMaxY());
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
    // TDOO: add minor grid lines
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
            if (getDomain() == 0 || getRange() == 0) return;

            numXLines = (width / MIN_PIXEL_SPACING);
            numYLines = (height / MIN_PIXEL_SPACING);

            double[] xScaleInfo = calcScale(getDataViewMinX(), getDataViewMaxX(), numXLines);
            double[] yScaleInfo = calcScale(getDataViewMinY(), getDataViewMaxY(), numYLines);
            this.xScale = xScaleInfo[0];
            this.yScale = yScaleInfo[0];
            this.xScaleSigFigs = Math.max(0, -(int) xScaleInfo[1]); // 2 decimals is 10^(-2).  -2 --> 2
            this.yScaleSigFigs = Math.max(0, -(int) yScaleInfo[1]); // no decimals might be 10^(2).  2 --> -2, but max to 0

            // --------------- draw major x grid -----------------------------------------
            double startX = MathUtils.ceilToNearest(getDataViewMinX(), xScale);

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
/*            double xMinorScale = getMinorScale(xScale);
            while (x >= cornerX) {
                window.stroke(127);
                window.line((float)x, cornerY, (float)x, cornerY+height);

                i--;
                val = startX + i*xMinorScale;
                x = getScreenXFor(val);
            }*/

            double startY = MathUtils.ceilToNearest(getDataViewMinY(), yScale);
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
                window.text(value, getLeftX() + - yAxisTextXAdjust/2 + getYAxisTextXAdjust(), (float) y);

                i++;
                val = startY + i * yScale;
                y = getScreenYFor(val);
            }
        }

        private double getMinorScale(double xScale) {
            if ((""+xScale).endsWith("2")) return xScale/4;
            return xScale/5;
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
        return dataMaxY- dataMinY;
    }

    private double getDomain() {
        return dataMaxX - dataMinX;
    }

    protected static double[] calcScale(double minVal, double maxVal, int numIntervals){
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

        return new double[] {scale[scaleIndex]*Math.pow(10, count), count};
    }

}
