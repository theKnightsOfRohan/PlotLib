package Demos;

import Plot.ScatterPlot;
import processing.core.PApplet;

public class InteractiveGraph extends PApplet {
    ScatterPlot plt; // TODO: add ability to be fixed size; new points bump off old points
    int time = 0;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new ScatterPlot(100, 100, 700, 700);

        plt.set(ScatterPlot.Setting.show_axes, true); // TODO: make a nice api for this
        plt.set(ScatterPlot.Setting.show_border, true);
        plt.setTextSize(30);
    }

    public void draw() {
        background(255);

        if (mousePressed) {
            plt.plot(0, time, mouseX).strokeColor("red").style("-");
            plt.plot(1, time, mouseY).strokeColor("green").style("-");
            time++;
        }

        plt.draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("Demos.InteractiveGraph");
    }
}
