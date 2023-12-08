package Demos;

import Plot.MathUtils;
import Plot.PlotWindow;
import Plot.ScatterPlot;

public class MainVer2 {
    public static void main(String[] args) {
        ScatterPlot plt = new ScatterPlot(100, 100, 700, 700);

        double[] x = MathUtils.linspace(-2.0 * Math.PI, 2.0 * Math.PI, 1000);
        double[] y1 = MathUtils.apply(Math::sin, x);
        double[] y2 = MathUtils.apply(Math::cos, x);

        plt.set(ScatterPlot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(ScatterPlot.Setting.show_border, true);
        plt.setTextSize(20);

        plt.plot(x, y1).fillColor("red").strokeWeight(2).strokeColor("red").style("-");
        plt.plot(x, y2).fillColor("blue").strokeWeight(2).strokeColor("blue").style("-");

        PlotWindow window = PlotWindow.getWindowFor(plt, 400,400);
        window.show();

        PlotWindow window2 = PlotWindow.getWindowFor(plt, 800,800);
        window2.show();
    }
}
