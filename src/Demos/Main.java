package Demos;

import Plot.MathUtils;
import Plot.ScatterPlot;
import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {
    ScatterPlot plt;
    private PVector startClick, endClick;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new ScatterPlot(100, 100, 700, 700);

        double[] x = MathUtils.linspace(-2.0 * Math.PI, 2.0 * Math.PI, 1000);
        double[] y1 = MathUtils.apply(Math::sin, x);
        double[] y2 = MathUtils.apply(Math::cos, x);

        plt.set(ScatterPlot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(ScatterPlot.Setting.show_border, true);
        plt.setTextSize(20);

        plt.plot(x, y1).fillColor("red").strokeWeight(5).strokeColor("red").style("-");
        plt.plot(x, y2).fillColor("blue").strokeWeight(5).strokeColor("blue").style("-");
    }

    public void draw() {
        background(200);
        plt.draw(this);
    }

    public void mousePressed() {
        if (plt.containsMouse(this)) {
            startClick = new PVector(mouseX, mouseY);
            System.out.println("Mouse pressed");
        }
    }

    public void mouseDragged() {
        if (startClick != null) {
            rect(startClick.x, startClick.y, (mouseX - startClick.x), (mouseY - startClick.y));
        }
    }

    public void mouseReleased() {
        if (plt.containsMouse(this) && startClick != null) {
            endClick = new PVector(mouseX, mouseY);
            System.out.printf("Mouse released, zooming");
            plt.zoomViewToScreenCoordinates(startClick.x, startClick.y, endClick.x, endClick.y);
        }
        startClick = null;
    }

    public static void main(String[] args) {
        PApplet.main("Demos.Main");
    }
}
