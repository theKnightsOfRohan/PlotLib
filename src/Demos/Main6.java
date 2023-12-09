package Demos;

import Plot.MathUtils;
import Plot.PlotWindow;
import Plot.ScatterPlot;
import processing.core.PApplet;
import processing.core.PVector;

public class Main6  {
    ScatterPlot plt;
    private PVector startClick, endClick;

    public static void main(String[] args) {
        ScatterPlot plt = new ScatterPlot(100, 100, 1100, 700);

        double[] x = MathUtils.linspace(-2.0 * Math.PI, 2.0 * Math.PI, 1000);
        double[] y1 = MathUtils.apply(Math::sin, x);
        double[] y2 = MathUtils.apply(Math::cos, x);

        for (int i = 0; i < x.length; i++) {
            plt.plot(1, x[i], y1[i]).strokeColor("red").strokeWeight(5).style("-");
        }

        for (int i = 0; i < x.length; i += 10) {
            plt.plot(0, x[i], y2[i]).strokeColor("blue").strokeWeight(2).style(".");
        }

        PlotWindow window = PlotWindow.getWindowFor(plt, 1200,800);
        window.show();
    }

}
