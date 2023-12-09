package Plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScatterPlot extends Plot {

    public ScatterPlot(ScatterPlot plotToCopy) {
        super(0,0,0,0); // will get reset below
        this.cornerX = plotToCopy.cornerX;
        this.cornerY = plotToCopy.cornerY;
        this.width = plotToCopy.width;
        this.height = plotToCopy.height;
        this.settings = (HashMap<Setting, Boolean>)plotToCopy.settings.clone(); // =\
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

    private void setDataSets(ScatterPlot plotToCopy) {
        this.datasets = new ArrayList<PlotData>();
        for (PlotData dataset : plotToCopy.datasets) {
            this.datasets.add( copyDataSet(dataset) );
        }
    }

    private PlotData copyDataSet(PlotData dataset) {
        return new PlotData(dataset);
    }

    public ScatterPlot(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    public PlotData plot(double[] xData, double[] yData) {
        List<Double> xList = MathUtils.toList(xData);
        List<Double> yList = MathUtils.toList(yData);

        PlotData data = new PlotData(xList, yList);
        updateDataBoundsWith(data);
        this.datasets.add(data);

        return data;
    }

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
}