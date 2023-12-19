package Plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a scatter plot, displaying data points as
 * individual markers on a coordinate system.
 */
public class ScatterPlot extends Plot {

    public ScatterPlot(ScatterPlot plotToCopy) {
        super(0, 0, 0, 0); // will get reset below
        this.cornerX = plotToCopy.cornerX;
        this.cornerY = plotToCopy.cornerY;
        this.width = plotToCopy.width;
        this.height = plotToCopy.height;
        this.settings = (HashMap<Setting, Boolean>) plotToCopy.settings.clone(); // =\
        this.axes = plotToCopy.axes;
        setDataSets(plotToCopy);
        this.dataMinX = plotToCopy.dataMinX;
        this.dataMinY = plotToCopy.dataMinY;
        this.dataMaxX = plotToCopy.dataMaxX;
        this.dataMaxY = plotToCopy.dataMaxY;
        this.dataViewMaxX = plotToCopy.dataViewMaxX;
        this.dataViewMaxY = plotToCopy.dataViewMaxY;
        this.dataViewMinX = plotToCopy.dataViewMinX;
        this.dataViewMinY = plotToCopy.dataViewMinY;
        this.overrideDataView = plotToCopy.overrideDataView;
        this.needScaling = plotToCopy.needScaling;
    }

    /**
     * Sets the data sets for the scatter plot by copying the data sets from another
     * scatter plot.
     * 
     * @param plotToCopy the scatter plot from which to copy the data sets
     */
    private void setDataSets(ScatterPlot plotToCopy) {
        this.datasets = new ArrayList<PlotData>();
        for (PlotData dataset : plotToCopy.datasets) {
            this.datasets.add(copyDataSet(dataset));
        }
    }

    /**
     * Creates a copy of the given PlotData object.
     *
     * @param dataset The PlotData object to be copied.
     * @return A new PlotData object that is a copy of the given dataset.
     */
    private PlotData copyDataSet(PlotData dataset) {
        return new PlotData(dataset);
    }

    /**
     * Creates a new scatter plot with the given dimensions.
     * 
     * @param x1 the x-coordinate of the top-left corner
     * @param y1 the y-coordinate of the top-left corner
     * @param x2 the x-coordinate of the bottom-right corner
     * @param y2 the y-coordinate of the bottom-right corner
     */
    public ScatterPlot(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    /**
     * Plots the given data points on the scatter plot.
     * 
     * @param xData the x-coordinates of the data points
     * @param yData the y-coordinates of the data points
     * @return the PlotData object representing the plotted data
     */
    public PlotData plot(double[] xData, double[] yData) {
        List<Double> xList = MathUtils.toList(xData);
        List<Double> yList = MathUtils.toList(yData);

        PlotData data = new PlotData(xList, yList);
        updateDataBoundsWith(data);
        this.datasets.add(data);

        return data;
    }

    /**
     * Plots the given data points on the scatter plot.
     *
     * @param xData The x-coordinates of the data points.
     * @param yData The y-coordinates of the data points.
     * @return The PlotData object representing the plotted data.
     */
    public PlotData plot(List<Double> xData, List<Double> yData) {
        PlotData data = new PlotData(xData, yData);
        updateDataBoundsWith(data);
        this.datasets.add(data);

        return data;
    }

    @Override
    public PlotData plot(int dataSetId, double x, double y) {
        PlotData data;

        if (dataSetId >= 0 && dataSetId < datasets.size()) {
            data = datasets.get(dataSetId);
        } else {
            data = new PlotData();
            this.datasets.add(data);
        }

        // If frozen scale, don't add data that's out of the range
        if (settings.containsKey(Setting.freeze_y_scale)) {
            if (!inDataYRange(y)) {
                return data;
            }
        }

        data.add(x, y);
        updateDataBoundsWith(data);
        return data;
    }

    /**
     * Plots a horizontal or vertical line on the scatter plot.
     * 
     * @param i         the x- or y-coordinate of the line
     * @param direction the direction of the line ("horizontal" or "vertical")
     */
    public PlotData plotLine(double val, String direction) {
        PlotData data = new PlotData();
        if (direction.equals("horizontal")) {
            data.add(MathUtils.linspace(dataMinX, dataMaxX, 1000), MathUtils.linspace(val, val, 1000));
        } else if (direction.equals("vertical")) {
            data.add(MathUtils.linspace(val, val, 1000), MathUtils.linspace(dataMinY, dataMaxY, 1000));
        } else {
            System.err.println("Invalid direction. Must be 'horizontal' or 'vertical'.");
            return null;
        }

        this.datasets.add(data);

        return data;
    }
}
