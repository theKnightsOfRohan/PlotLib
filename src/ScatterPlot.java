import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ScatterPlot {
    public enum Setting {
        show_axes, freeze_y_scale, freeze_x_scale, show_border
    }

    protected int cornerX, cornerY, width, height;
    protected ArrayList<PlotData> datasets;
    protected HashMap<Setting, Boolean> settings;
    protected double dataMinX, dataMinY, dataMaxX, dataMaxY;
    protected boolean needScaling = true;

    public ScatterPlot(int cornerX, int cornerY, int w, int h) {
        this.cornerX = cornerX;
        this.cornerY = cornerY;
        this.width = w;
        this.height = h;
        this.datasets = new ArrayList<>();
        settings = new HashMap<Setting, Boolean>();
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

    public PlotData plot(double[] xData, double[] yData) {
        List<Double> xList = MathUtils.toList(xData);
        List<Double> yList = MathUtils.toList(yData);

        PlotData data = new PlotData(xList, yList);
        updateDataBoundsWith(data);
        this.datasets.add(data);

        return data;
    }

    public PlotData plot(int index, double x, double y) {
        PlotData data;

        if (index >= 0 && index < datasets.size()) {
            data = datasets.get(index);
        } else {
            data = new PlotData();
            this.datasets.add(data);
        }

        // If frozen scale, don't add data that's out of the range
        if (settings.containsKey(Setting.freeze_y_scale)) {
            if (! inDataYRange(y) ) {
                return data;
            }
        }

        data.add(x, y);
        updateDataBoundsWith(data);
        return data;
    }

    private boolean inDataYRange(double y) {
        return (this.dataMinY <= y && y <= this.dataMaxY);
    }

    public PlotData plot(double x, double y) {
        return plot(0, x, y);
    }

    private void updateDataBoundsWith(PlotData data) {
        if (!settings.containsKey(Setting.freeze_y_scale)) {
            updateYBoundsWith(data);
        }

        if (!settings.containsKey(Setting.freeze_x_scale)) {
            updateXBoundsWith(data);
        }
    }

    private void updateXBoundsWith(PlotData data) {
        if (data.getDataMinX() < dataMinX) dataMinX = data.getDataMinX();
        if (data.getDataMaxX() > dataMaxX) dataMaxX = data.getDataMaxX();
    }

    private void updateYBoundsWith(PlotData data) {
        if (data.getDataMinY() < dataMinY) dataMinY = data.getDataMinY();
        if (data.getDataMaxY() > dataMaxY) dataMaxY = data.getDataMaxY();
    }

    private void plotPoints(PApplet window) {
        if (needScaling) reScaleData(window);

        // TODO: remove data that's out of range if plot frozen?
        for (PlotData dataset : datasets) {
            plotDataSet(window, dataset);
        }
    }

    private void plotDataSet(PApplet window, PlotData dataset) {
        dataset.rescale(cornerX, cornerX + width, cornerY + height, cornerY);

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

    private void reScaleData(PApplet window) {
        // TODO: hmm??
    }

    private void printDebugInfo() {
        System.out.println("Datax: [" + dataMinX + ", " + dataMaxX + "]");
        System.out.println("Datay: [" + dataMinY + ", " + dataMaxY + "]");
        System.out.println("screen x: [" + cornerX + ", " + cornerX + width + "]");
        System.out.println("screen y: [" + cornerY + ", " + cornerY + height + "]");
    }

    private void drawAxes(PApplet window) {
        if (settings.containsKey(Setting.show_axes)) {
            int axisX = (int) getScreenXFor(0);
            int axisY = (int) getScreenYFor(0);
            window.stroke(0);
            window.line(cornerX, axisY, cornerX + width, axisY);
            window.line(axisX, cornerY, axisX, cornerY + height);
        }

        if (settings.containsKey(Setting.show_border)) {
            window.noFill();
            window.stroke(PlotData.BLACK);
            window.rect(cornerX, cornerY, width, height);
        }
    }

    public double getScreenXFor(double dataX) {
        return map(dataX, dataMinX, dataMaxX, cornerX, cornerX + width);  // TODO: use getters for this
    }

    public double getScreenYFor(double dataY) {
        return map(dataY, dataMinY, dataMaxY, cornerY + height, cornerY);  // TODO: use getters for this
    }

    public double getDataXFor(double screenX) {
        return map(screenX, cornerX, cornerX + width, dataMinX, dataMaxX);  // TODO: use getters for this
    }

    public double getDataYFor(double screenY) {
        return map(screenY, cornerY + height, cornerY, dataMinY, dataMaxY);  // TODO: use getters for this
    }

    public static double map(double target, double low1, double high1, double low2, double high2) {
        return ((target - low1) / (high1 - low1)) * (high2 - low2) + low2;
    }
}