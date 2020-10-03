import processing.core.PApplet;

import java.util.Arrays;

public class Main extends PApplet {
    Plot plt;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new Plot(100, 100, 600, 600);

        double[] x = MathUtils.linspace( -2.0*Math.PI, 2.0*Math.PI, 1000);
        double[] y1 = Arrays.stream(x).map(Math::sin).toArray();
        double[] y2 = Arrays.stream(x).map(Math::cos).toArray();

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
