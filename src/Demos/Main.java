package Demos;

import Plot.MathUtils;
import Plot.ScatterPlot;
import processing.core.PApplet;

public class Main extends PApplet {
    ScatterPlot plt;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new ScatterPlot(100, 100, 600, 600);

        double[] x = MathUtils.linspace( -2.0*Math.PI, 2.0*Math.PI, 1000);
        double[] y1 = MathUtils.apply(Math::sin, x);
        double[] y2 = MathUtils.apply(Math::cos, x);

        plt.set(ScatterPlot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(ScatterPlot.Setting.show_border, true);

        plt.plot(x, y1).fillColor("red").strokeColor("red").style("-");
        plt.plot(x, y2).fillColor("blue").strokeColor("blue").style("-");
    }

    public void draw() {
        plt.draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("Demos.Main");
    }
}
