package Plot;

import java.util.ArrayList;

public class TimeSeriesPlot extends Plot {
    private int dataWidth;

    public TimeSeriesPlot(int x1, int y1, int x2, int y2, int dataPointWidth) {
        super(x1, y1, x2, y2);
        this.dataWidth = dataPointWidth;
    }

    /***
     * Add data (x, y) to last dataset.
     * 
     * @param x
     * @param y
     * @return
     */
    public PlotData plot(double x, double y) {
        if (datasets.size() == 0) {
            return plot(0, x, y);
        } else {
            return plot(datasets.size() - 1, x, y);
        }
    }

    /***
     * Add data point (x, y) to dataset index. If index is invalid a new dataset is
     * created.
     * Note: if freeze_y_scale setting is true and point is out of range it won't be
     * added.
     * 
     * @param dataSetId
     * @param x
     * @param y
     * @return
     */
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
            if (!inDataYRange(y)) { // TODO: print warning if verbose mode on
                return data;
            }
        }

        data.add(x, y);

        if (data.size() >= this.dataWidth) {
            data.remove(0);
            shiftXBounds(data);
        }

        if (!settings.containsKey(Setting.freeze_y_scale)) {
            updateYBoundsWith(data);
        }

        return data;
    }

    // TODO: decide if this needs to be public
    private void shiftXBounds(PlotData data) {
        this.dataMinX = data.getDataX(0);
        this.dataMaxX = data.getDataX(data.size() - 1);

        data.setDataMinX(dataMinX);
        data.setDataMaxX(dataMaxX);
    }

    public ArrayList<PlotData> getDatasets() {
        return datasets;
    }
}