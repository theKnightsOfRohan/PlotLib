import processing.core.PApplet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Plot {
    public enum Setting {
        show_axes, show_border
    }

    private int cornerX, cornerY, width, height;
    private ArrayList<PlotData> datasets;
    private HashMap<Setting, Boolean> settings;
    private double dataMinX, dataMinY, dataMaxX, dataMaxY;
    private boolean needScaling = true;

    public Plot(int cornerX, int cornerY, int w, int h) {
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

    public void draw(PApplet window) {
        drawAxes(window);
        plotPoints(window);
    }

    public PlotData plot(double[] xData, double[] yData) {
        List<Double> xList = MathUtils.toList(xData);
        List<Double> yList = MathUtils.toList(yData);

        PlotData data = new PlotData( xList, yList );
        updateDataBoundsWith(data);
        this.datasets.add(data);

        return data;
    }

    private void updateDataBoundsWith(PlotData data) {
        if (data.getDataMinX() < dataMinX) dataMinX = data.getDataMinX();
        if (data.getDataMaxX() > dataMaxX) dataMaxX = data.getDataMaxX();
        if (data.getDataMinY() < dataMinY) dataMinY = data.getDataMinY();
        if (data.getDataMaxY() > dataMaxY) dataMaxY = data.getDataMaxY();
    }

    private void plotPoints(PApplet window) {
        if (needScaling) reScaleData(window);

        for (PlotData dataset : datasets) {
            plotDataSet(window, dataset);
        }
    }

    private void plotDataSet(PApplet window, PlotData dataset) {
        dataset.rescale(cornerX, cornerX+width, cornerY+height, cornerY);
        // TODO: refactor so datasets draw themselves...?
        window.fill(dataset.getFillColor());
        window.stroke(dataset.getStrokeColor());

        for (int i = 0; i < dataset.size(); i++) {
            window.ellipse(dataset.getDisplayX(i), dataset.getDisplayY(i), 2, 2);
        }
    }

    private void reScaleData(PApplet window) {

    }

    private void printDebugInfo() {
        System.out.println("Datax: [" + dataMinX + ", " + dataMaxX + "]");
        System.out.println("Datay: [" + dataMinY + ", " + dataMaxY + "]");
        System.out.println("screen x: [" + cornerX + ", " + cornerX+width + "]");
        System.out.println("screen y: [" + cornerY + ", " + cornerY+height + "]");
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
        return map(dataX, dataMinX, dataMaxX, cornerX, cornerX+width);  // TODO: use getters for this
    }

    public double getScreenYFor(double dataY) {
        return map(dataY, dataMinY, dataMaxY, cornerY+height, cornerY);  // TODO: use getters for this
    }

    public double getDataXFor(double screenX) {
        return map(screenX, cornerX, cornerX+width, dataMinX, dataMaxX);  // TODO: use getters for this
    }

    public double getDataYFor(double screenY) {
        return map(screenY, cornerY+height, cornerY, dataMinY, dataMaxY);  // TODO: use getters for this
    }

    public static double map(double target, double low1, double high1, double low2, double high2) {
        return ((target - low1)/(high1-low1))*(high2-low2) + low2;
    }
}