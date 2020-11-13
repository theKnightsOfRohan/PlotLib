package Demos;

import Plot.MathUtils;
import Plot.Plot;
import Plot.ScatterPlot;
import processing.core.PApplet;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class GraphAnimation extends PApplet {
    ScatterPlot plt;
    double timeSteps = 300;
    int pauseAmount = 100;
    int time = -pauseAmount;

    DoubleUnaryOperator f_main = (v -> v*v/2 + 8);
    DoubleUnaryOperator f_sub = (v -> 4*Math.sin(v)+ 4);

    double[] x, y1, y2, y3, y4, dy;
    private boolean paused = false;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new ScatterPlot(100, 100, 600, 600);

        x = MathUtils.linspace( -5, 5, 100);

        // Stationary functions
        y1 = MathUtils.apply(f_main, x);
        y2 = MathUtils.apply(f_sub, x);

        // Moving functions
        y3 = MathUtils.apply(f_main, x);
        y4 = MathUtils.apply(f_sub, x);

        // Calculate delta y amounts
        dy = MathUtils.apply((v -> v/timeSteps), y2);

        plt.set(ScatterPlot.Setting.show_axes, true);
        plt.set(ScatterPlot.Setting.show_border, true);

        plt.plot(x, y1).fillColor("red").strokeColor("red").style("-");
        plt.plot(x, y2).fillColor("blue").strokeColor("blue").style("-");

        plt.plot(x, y3).fillColor("red").strokeColor("red").style(".");
        plt.plot(x, y4).fillColor("blue").strokeColor("blue").style(".");

        //plt.setYDataRangeMin(-3);
        plt.set(Plot.Setting.freeze_y_scale, true);
    }

    public void draw() {
        background(255, 255, 255);

        if (!paused) {
            time++;

            if (0 < time && time < timeSteps) {
                for (int i = 0; i < dy.length; i++) {
                    y3[i] = y3[i] - dy[i];
                    y4[i] = y4[i] - dy[i];
                }

                plt.removePlot(2);
                plt.removePlot(2);
                plt.plot(x, y3).fillColor("red").strokeColor("red").style(".");
                plt.plot(x, y4).fillColor("blue").strokeColor("blue").style(".");
            } else if (time >= timeSteps + 2 * pauseAmount) {
                time = -30;
                plt.removePlot(2);
                plt.removePlot(2);
                y3 = MathUtils.apply(f_main, x);
                y4 = MathUtils.apply(f_sub, x);
                plt.plot(x, y3).fillColor("red").strokeColor("red").style(".");
                plt.plot(x, y4).fillColor("blue").strokeColor("blue").style(".");
            }
        } else {
            fill(0);
            textSize(32);
            text("paused", 50, 50);
        }

        plt.draw(this);


        List<Integer> x = plt.getXScreenCoords(0);
        List<Integer> topY = plt.getYScreenCoords(2);
        List<Integer> origY = plt.getYScreenCoords(0);
        List<Integer> bottomY = plt.getYScreenCoords(3);
        double zeroHeight = plt.getScreenYFor(0);
        for (int i = 0; i < x.size(); i++) {
            stroke(color(255,0,0));
            line(x.get(i), (int)zeroHeight, x.get(i), bottomY.get(i));

            stroke(color(0, 255,0));
            line(x.get(i), topY.get(i), x.get(i), origY.get(i));
        }
    }

    public void mouseReleased() {
        paused = !paused;
    }

    public static void main(String[] args) {
        PApplet.main("Demos.GraphAnimation");
    }
}
