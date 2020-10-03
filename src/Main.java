import processing.core.PApplet;

import java.util.Arrays;

public class Main extends PApplet {
    Plot plt;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new Plot(0, 0, 200, 200);

        double[] x = MathUtils.linspace( -2.0*Math.PI, 2.0*Math.PI, 1000);
        double[] y1 = MathUtils.apply(Math::sin, x);
        double[] y2 = MathUtils.apply(Math::cos, x);

        plt.set(Plot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(Plot.Setting.show_border, true);

        plt.plot(x, y1).fillColor("red").strokeColor("red");
        plt.plot(x, y2).fillColor("blue").strokeColor("blue");
    }

    public void draw() {
        plt.draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("Main");
    }
}
