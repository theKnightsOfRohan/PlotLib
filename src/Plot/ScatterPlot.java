package Plot;

import java.util.List;

public class ScatterPlot extends Plot {

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
            if (!inDataYRange(y)) {
                return data;
            }
        }

        data.add(x, y);
        updateDataBoundsWith(data);
        return data;
    }
}