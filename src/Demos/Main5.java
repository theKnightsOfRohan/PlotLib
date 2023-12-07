package Demos;

import Plot.Plot;
import Plot.TimeSeriesPlot;
import processing.core.PApplet;
import Plot.ScatterPlot;

public class Main5 extends PApplet {
	ScatterPlot plt;
	double[] x;
	double[] y;

	public void settings() {
		size(800, 800);
	}

	public void setup() {
		plt = new ScatterPlot(50, 50, 750, 750);
		x = new double[2000];
		y = new double[2000];

		for (int i = 0; i < x.length; i++) {
			x[i] = i;
			y[i] = Math.sin(i / 180.0);
		}

		plt.plot(x, y).fillColor("red").strokeWeight(5).strokeColor("red").style("--").dashLength(50);
	}

	public void draw() {
		plt.draw(this);
	}

	public static void main(String[] args) {
		PApplet.main("Demos.Main5");
	}
}
