package Plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Histogram extends Plot {
    public Histogram(Histogram plotToCopy) {
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
     * Creates a new histogram with the given dimensions.
     * 
     * @param x1 the x-coordinate of the top-left corner
     * @param y1 the y-coordinate of the top-left corner
     * @param x2 the x-coordinate of the bottom-right corner
     * @param y2 the y-coordinate of the bottom-right corner
     */
    public Histogram(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    /**
     * Sets the data sets for the scatter plot by copying the data sets from another
     * scatter plot.
     * 
     * @param plotToCopy the scatter plot from which to copy the data sets
     */
    private void setDataSets(Histogram plotToCopy) {
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
     * CANNOT USE THIS METHOD SIGNATURE FOR HISTOGRAMS
     * 
     * @param xData the x-coordinates of the data points
     * @param yData the y-coordinates of the data points
     * @return null
     */
    public PlotData plot(double[] xData, double[] yData) {
        throw new UnsupportedOperationException("ERROR: Cannot plot bivariate data on a histogram.");
    }

    public PlotData plot(int dataSetId, double x, double y) {
        throw new UnsupportedOperationException("ERROR: Cannot plot bivariate data on a histogram.");
    }
    /*
     * public PlotData plot(double[] xData) {
     * List<Double> xList = MathUtils.toList(xData);
     * 
     * PlotData data = new PlotData(xList);
     * updateDataBoundsWith(data);
     * this.datasets.add(data);
     * 
     * return data;
     * }
     */

}
