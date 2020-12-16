package Plot;

public class TimeSeriesPlot extends Plot {
    private int dataWidth;

    public TimeSeriesPlot(int x1, int y1, int x2, int y2, int dataPointWidth) {
        super(x1, y1, x2, y2);
        this.dataWidth = dataPointWidth;
    }

    @Override
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

        if (data.size() >= this.dataWidth) {
            data.remove(0);
            shiftXBounds(data);
        }

        if (!settings.containsKey(Setting.freeze_y_scale)) {
            updateYBoundsWith(data);
        }

        return data;
    }

    private void shiftXBounds(PlotData data) {
        this.dataMinX = data.getDataX(0);
        this.dataMaxX = data.getDataX(data.size()-1);

        data.setDataMinX(dataMinX);
        data.setDataMaxX(dataMaxX);
    }
}