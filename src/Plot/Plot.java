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

    // ---- DATA ----
    protected ArrayList<PlotData> datasets;
    protected double dataMinX;
    protected double dataMinY;
    protected double dataMaxX;
    protected double dataMaxY;
    protected boolean needScaling = true;

    /***
     * Create a plot from upper-left corner (x1, y1) to lower right corner (x2, y2)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
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
        plotPoints(window);
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

    protected void plotPoints(PApplet window) {
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
        // TODO: hmm??
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
}
