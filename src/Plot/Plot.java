package Plot;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Plot {
    private static final float MIN_X_ZOOM_THRESHOLD = 50;
    private static final float MIN_Y_ZOOM_THRESHOLD = 50;

    // ---- STYLE ----
    private static final int AXIS_STROKE_WEIGHT = 2;
    protected int cornerX;
    protected int cornerY;
    protected int width;
    protected int height;

    public enum Setting {
        show_axes, freeze_y_scale, freeze_x_scale, show_border
    }

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

    /**
     * Maps a value from one range to another range.
     *
     * @param target the value to be mapped
     * @param low1   the lower bound of the input range
     * @param high1  the upper bound of the input range
     * @param low2   the lower bound of the output range
     * @param high2  the upper bound of the output range
     * @return the mapped value
     */
    public static double map(double target, double low1, double high1, double low2, double high2) {
        return ((target - low1) / (high1 - low1)) * (high2 - low2) + low2;
    }

    /**
     * Sets the specified setting to the given value.
     *
     * @param setting the setting to be set
     * @param value   the value to set the setting to
     */
    public void set(Setting setting, boolean value) {
        settings.put(setting, value);
    }

    public void setYDataRange(int min, int max) {
        this.dataMinY = min;
        this.dataMaxY = max;
    }

    /**
     * Draws the plot on the given PApplet window.
     * 
     * @param window The PApplet window on which to draw the plot.
     */
    public void draw(PApplet window) {
        for (PlotData dataset : datasets) {
            if (dataset.isDirty()) {
                updateDataBoundsWith(dataset);
                dataset.setClean();
            }
        }

        drawAxes(window);
        drawDataPoints(window);

        window.fill(0); // kludgy hack to insure axes display in black always?
        window.stroke(0);
    }

    public boolean containsMouse(PApplet window) {
        return isInBounds(window.mouseX, window.mouseY);
    }

    public double getDataViewMinX() {
        if (overrideDataView)
            return dataViewMinX;
        return dataMinX;
    }

    public double getDataViewMinY() {
        if (overrideDataView) {
            return dataViewMinY;
        }
        return dataMinY;
    }

    public double getDataViewMaxX() {
        if (overrideDataView)
            return dataViewMaxX;
        return dataMaxX;
    }

    public double getDataViewMaxY() {
        if (overrideDataView)
            return dataViewMaxY;
        return dataMaxY;
    }

    public void zoomInOn(double zoomAmount, double reCenterAmount, double targetX, double targetY) {
        targetX = getDataXFor(targetX);
        targetY = getDataYFor(targetY);

        double newWidth = (getDataViewMaxX() - getDataViewMinX()) * (1.0 / (1 + zoomAmount));
        double newHeight = (getDataViewMaxY() - getDataViewMinY()) * (1.0 / (1 + zoomAmount));

        double newCenterX = getDataViewCenterX() + (targetX - getDataViewCenterX()) * reCenterAmount;
        double newCenterY = getDataViewCenterY() + (targetY - getDataViewCenterY()) * reCenterAmount;

        this.dataViewMinX = newCenterX - newWidth / 2;
        this.dataViewMaxX = newCenterX + newWidth / 2;
        this.dataViewMinY = newCenterY - newHeight / 2;
        this.dataViewMaxY = newCenterY + newHeight / 2;
        this.overrideDataView = true;
    }

    private double getDataViewCenterY() {
        return getDataViewMinY() + (getDataViewMaxY() - getDataViewMinY()) / 2;
    }

    private double getDataViewCenterX() {
        return getDataViewMinX() + (getDataViewMaxX() - getDataViewMinX()) / 2;
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
        System.out.println(
                getDataViewMinX() + ", " + getDataViewMinY() + " to " + getDataViewMaxX() + ", " + getDataViewMaxY());
    }

    /***
     * Zoom plot view to data ranges corresponding to current screen coordinates (x,
     * y) to (x1, y1)
     * 
     * @param x  x coordinate of upper left corner of region to zoom to
     * @param y  y coordinate of upper left corner of region to zoom to
     * @param x1 x coordinate of lower right corner of region to zoom to
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

    /**
     * Rescales the plot based on the data in the datasets.
     * If there are no datasets, the method returns without making any changes.
     * The method updates the minimum and maximum values for the x and y axes based
     * on the data in the datasets.
     * After updating the data bounds, it marks each dataset as clean.
     * Finally, it sets the needScaling flag to false.
     */
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

    public abstract PlotData plot(int dataSetId, double x, double y);

    protected boolean inDataYRange(double y) {
        return (this.dataMinY <= y && y <= this.dataMaxY);
    }

    public PlotData plot(double x, double y) {
        return plot(0, x, y);
    }

    /**
     * Updates the data bounds with the given PlotData.
     * If the freeze_y_scale setting is not present in the settings map, it updates
     * the y bounds with the data.
     * If the freeze_x_scale setting is not present in the settings map, it updates
     * the x bounds with the data.
     *
     * @param data The PlotData to update the bounds with.
     */
    protected void updateDataBoundsWith(PlotData data) {
        if (!settings.containsKey(Setting.freeze_y_scale)) {
            updateYBoundsWith(data);
        }

        if (!settings.containsKey(Setting.freeze_x_scale)) {
            updateXBoundsWith(data);
        }
    }

    /**
     * Updates the bounds of the x-axis with the given PlotData.
     * If the minimum x-value of the data is less than the current minimum x-value,
     * the minimum x-value is updated. If the maximum x-value of the data is greater
     * than the current maximum x-value, the maximum x-value is updated.
     *
     * @param data the PlotData containing the x-values to update the bounds with
     */
    protected void updateXBoundsWith(PlotData data) {
        if (data.getDataMinX() < dataMinX)
            dataMinX = data.getDataMinX();
        if (data.getDataMaxX() > dataMaxX)
            dataMaxX = data.getDataMaxX();
    }

    /**
     * Updates the y-axis bounds of the plot with the given data.
     * If the minimum y-value of the data is less than the current minimum y-value,
     * the minimum y-value is updated. If the maximum y-value of the data is greater
     * than the current maximum y-value, the maximum y-value is updated.
     *
     * @param data the PlotData object containing the data to update the y-axis
     *             bounds
     */
    protected void updateYBoundsWith(PlotData data) {
        if (data.getDataMinY() < dataMinY)
            dataMinY = data.getDataMinY();
        if (data.getDataMaxY() > dataMaxY)
            dataMaxY = data.getDataMaxY();
    }

    /**
     * Draws the data points on the plot.
     * If the plot is frozen, it removes data points that are out of range.
     * 
     * @param window The PApplet window on which the plot is drawn.
     */
    protected void drawDataPoints(PApplet window) {
        if (needScaling)
            reScaleData(window);

        // TODO: remove data that's out of range if plot frozen?
        for (PlotData dataset : datasets) {
            plotDataSet(window, dataset);
        }
    }

    /**
     * Plots the given dataset on the specified window.
     *
     * @param window  the PApplet window on which to plot the dataset
     * @param dataset the PlotData object representing the dataset to be plotted
     */
    protected void plotDataSet(PApplet window, PlotData dataset) {
        dataset.rescale(cornerX, cornerX + width, cornerY + height, cornerY,
                getDataViewMinX(), getDataViewMaxX(), getDataViewMinY(), getDataViewMaxY());

        dataset.drawSelf(window, this);
    }

    /**
     * Checks if the given coordinates (x, y) are within the bounds of the plot.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return true if the coordinates are within the bounds, false otherwise.
     */
    public boolean isInBounds(float x, float y) {
        if (x < getLeftX() || x > getRightX())
            return false;
        if (y < getTopY() || y > getBottomY())
            return false;
        return true;
    }

    // ===== constants for clipping algorithm ========
    private static final int INSIDE = 0; // 0000
    private static final int LEFT = 1; // 0001
    private static final int RIGHT = 2; // 0010
    private static final int BOTTOM = 4; // 0100
    private static final int TOP = 8; // 1000
    // ===== constants for clipping algorithm ========

    /**
     * Clips a line segment defined by two points (x1, y1) and (x2, y2) to fit
     * within the current viewing window.
     * Uses the Cohen-Sutherland line clipping algorithm. More info here:
     * https://www.geeksforgeeks.org/line-clipping-set-1-cohen-sutherland-algorithm/
     * 
     * @param x1 The x-coordinate of the first point of the line segment.
     * @param y1 The y-coordinate of the first point of the line segment.
     * @param x2 The x-coordinate of the second point of the line segment.
     * @param y2 The y-coordinate of the second point of the line segment.
     * @return An array of integers representing the clipped line segment
     *         coordinates [x1, y1, x2, y2], or null if the line is entirely outside
     *         the viewing window.
     */
    public int[] clipLine(int x1, int y1, int x2, int y2) {
        int windowHeight = (int) (Math.abs(this.getBottomY() - this.getTopY()));
        int windowWidth = (int) (Math.abs(this.getRightX() - this.getLeftX()));

        int code1 = computeCode(x1, y1, windowWidth, windowHeight);
        int code2 = computeCode(x2, y2, windowWidth, windowHeight);
        boolean accept = false;

        while (true) {
            if ((code1 == 0) && (code2 == 0)) {
                accept = true;
                break;
            } else if ((code1 & code2) != 0) {
                break;
            } else {
                int codeOut;
                int x, y;

                if (code1 != 0) {
                    codeOut = code1;
                } else {
                    codeOut = code2;
                }

                if ((codeOut & TOP) != 0) {
                    x = (int) (x1 + (x2 - x1) * (this.getTopY() - y1) / (y2 - y1));
                    y = (int) this.getTopY();
                } else if ((codeOut & BOTTOM) != 0) {
                    x = (int) (x1 + (x2 - x1) * (this.getTopY() + windowHeight - y1) / (y2 - y1));
                    y = (int) (this.getTopY() + windowHeight);
                } else if ((codeOut & RIGHT) != 0) {
                    y = (int) (y1 + (y2 - y1) * (this.getLeftX() + windowWidth - x1) / (x2 - x1));
                    x = (int) (this.getLeftX() + windowWidth);
                } else if ((codeOut & LEFT) != 0) {
                    y = (int) (y1 + (y2 - y1) * (this.getLeftX() - x1) / (x2 - x1));
                    x = (int) this.getLeftX();
                } else {
                    x = 0;
                    y = 0;
                }

                if (codeOut == code1) {
                    x1 = x;
                    y1 = y;
                    code1 = computeCode(x1, y1, windowWidth, windowHeight);
                } else {
                    x2 = x;
                    y2 = y;
                    code2 = computeCode(x2, y2, windowWidth, windowHeight);
                }
            }
        }

        if (accept) {
            return new int[] { x1, y1, x2, y2 };
        } else {
            // System.err.println("Tried to clip line entirely outside viewing window");
            return null;
        }
    }

    /**
     * Computes the code for a given point (x, y) relative to the plot window.
     * The code is a combination of INSIDE, LEFT, RIGHT, TOP, and BOTTOM flags,
     * indicating the position of the point with respect to the plot window
     * boundaries.
     *
     * @param x            the x-coordinate of the point
     * @param y            the y-coordinate of the point
     * @param windowWidth  the width of the plot window
     * @param windowHeight the height of the plot window
     * @return the computed code
     */
    private int computeCode(int x, int y, int windowWidth, int windowHeight) {
        int code = INSIDE;

        if (x < this.getLeftX()) {
            code |= LEFT;
        } else if (x > this.getLeftX() + windowWidth) {
            code |= RIGHT;
        }

        if (y < this.getTopY()) {
            code |= TOP;
        } else if (y > this.getTopY() + windowHeight) {
            code |= BOTTOM;
        }

        return code;
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

    /**
     * Draws the axes on the plot.
     * 
     * @param window The PApplet window to draw on.
     */
    protected void drawAxes(PApplet window) {
        if (settings.containsKey(Setting.show_axes)) {
            int axisX = (int) getScreenXFor(0);
            int axisY = (int) getScreenYFor(0);
            window.strokeWeight(AXIS_STROKE_WEIGHT);
            window.stroke(0); // TODO: only draw axes if in bounds for plot?!
            window.line(cornerX, axisY, cornerX + width, axisY);
            window.line(axisX, cornerY, axisX, cornerY + height);
            window.strokeWeight(1);
        }

        if (settings.containsKey(Setting.show_border)) {
            window.noFill();
            window.stroke(PlotData.BLACK);
            window.rect(cornerX, cornerY, width, height);
        }

        axes.draw(this, window);
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

    /**
     * Removes a plot from the dataset based on the given dataSetId.
     *
     * @param dataSetId the ID of the dataset to be removed
     */
    public void removePlot(int dataSetId) {
        if (dataSetId < 0 || dataSetId >= this.datasets.size()) {
            System.err.println("Error: dataSet out of bounds");
            return;
        }
        this.datasets.remove(dataSetId);
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

    // TODO: refactor so cleaner
    // TODO: add minor grid lines
    // TODO: organize all features so easy to turn on and off
    /**
     * The Axes class represents the axes of a plot. It provides methods for drawing
     * the major and minor grid lines, as well as labeling the tick marks on the x
     * and y axes.
     */
    protected class Axes {
        private static final int MIN_PIXEL_SPACING = 50;
        public float yAxisTextXAdjust = -5, xAxisTextYAdjust = 5;

        protected int numXLines, numYLines;
        protected double xScale, yScale;
        protected int xScaleSigFigs, yScaleSigFigs;

        protected int xAxisTextSize = 10;
        protected int yAxisTextSize = 10;

        /**
         * Draws the plot on the given PApplet window.
         * 
         * @param plot   The Plot object containing the data and settings for the plot.
         * @param window The PApplet window on which the plot will be drawn.
         */
        protected void draw(Plot plot, PApplet window) {
            if (getDomain() == 0 || getRange() == 0)
                return;

            numXLines = (width / MIN_PIXEL_SPACING);
            numYLines = (height / MIN_PIXEL_SPACING);

            double[] xScaleInfo = calcScale(plot.getDataViewMinX(), plot.getDataViewMaxX(), numXLines);
            double[] yScaleInfo = calcScale(plot.getDataViewMinY(), plot.getDataViewMaxY(), numYLines);
            this.xScale = xScaleInfo[0];
            this.yScale = yScaleInfo[0];
            this.xScaleSigFigs = Math.max(0, -(int) xScaleInfo[1]); // 2 decimals is 10^(-2). -2 --> 2
            this.yScaleSigFigs = Math.max(0, -(int) yScaleInfo[1]); // no decimals might be 10^(2). 2 --> -2, but max to
                                                                    // 0

            // --------------- draw major x grid -----------------------------------------
            double startX = MathUtils.ceilToNearest(plot.getDataViewMinX(), xScale);

            double val = startX;
            double x = plot.getScreenXFor(val);
            int i = 0;
            while (x <= cornerX + width) {
                window.line((float) x, cornerY, (float) x, cornerY + height);
                window.textSize(xAxisTextSize);
                window.textAlign(window.CENTER, window.CENTER);
                window.fill(0);
                window.stroke(0);
                String value = String.format("%." + this.xScaleSigFigs + "f", val);
                window.textAlign(PConstants.LEFT);
                window.text(value, (float) x - getCenterShiftAmount(window, value),
                        plot.getBottomY() + xAxisTextSize + plot.getXAxisTextYAdjust());

                i++;
                val = startX + i * xScale;
                x = plot.getScreenXFor(val);
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

            double startY = MathUtils.ceilToNearest(getDataViewMinY(), yScale);
            val = startY + yScale;
            double y = plot.getScreenYFor(val);
            i = 0;
            while (y >= cornerY) {
                window.line(cornerX, (float) y, cornerX + width, (float) y);
                window.textSize(yAxisTextSize);
                window.textAlign(window.CENTER, window.CENTER);
                window.fill(0);
                window.stroke(0);

                String value = String.format("%." + this.yScaleSigFigs + "f", val);
                window.textAlign(PConstants.RIGHT, PConstants.CENTER);
                window.text(value, plot.getLeftX() + plot.getYAxisTextXAdjust(), (float) y - yAxisTextSize * 0.1f);

                i++;
                val = startY + i * yScale;
                y = plot.getScreenYFor(val);
            }
        }

        /**
         * Calculates the amount of shift needed to center the text value horizontally.
         * 
         * @param window The PApplet window where the text will be displayed.
         * @param value  The text value for which the center shift amount is calculated.
         * @return The amount of shift needed to center the text value horizontally.
         */
        private float getCenterShiftAmount(PApplet window, String value) {
            if (value.startsWith("-"))
                value = value + "-"; // add extra dummy character so leading "-" isn't counted
            // in shift amount
            return window.textWidth(value) / 2;
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

    /**
     * Calculates the scale factor and count for a given range of values and number
     * of intervals.
     *
     * @param minVal       the minimum value of the range
     * @param maxVal       the maximum value of the range
     * @param numIntervals the number of intervals
     * @return an array containing the scale factor and count
     */
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
