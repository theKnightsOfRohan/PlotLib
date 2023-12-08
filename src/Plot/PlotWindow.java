package Plot;

import processing.core.PApplet;
import processing.core.PVector;

public class PlotWindow extends PApplet {
    private int screenWidth, screenHeight;
    private ScatterPlot plt;
    private PVector startClick, endClick;

    private PlotWindow(ScatterPlot p, int w, int h) {
        this.plt = p;
        this.screenWidth = w;
        this.screenHeight = h;
    }

    public static PlotWindow getWindowFor(ScatterPlot p, int w, int h) {
        return new PlotWindow(new ScatterPlot(p), w, h);
    }

    public void settings() {
        size(this.screenWidth, this.screenHeight);
    }

    public void setup() {

    }

    public void draw() {
        background(255);
        plt.draw(this);

        if (mousePressed && keyPressed && key == CODED && keyCode == CONTROL) {
            plt.zoomIn(0.01, 0.05, mouseX, mouseY);
        }

        if (mousePressed && startClick != null) {
            fill(0,0,0,0);
            stroke(0);
            rect(startClick.x, startClick.y, (mouseX - startClick.x), (mouseY - startClick.y));
        }
    }

    public void mousePressed() {
        if (plt.containsMouse(this) && !keyPressed) {
            startClick = new PVector(mouseX, mouseY);
        }

        if (mouseButton == RIGHT) {
            startClick = null;
            plt.resetViewBoundaries();
        }
    }

    public void mouseReleased() {
        if (plt.containsMouse(this) && startClick != null) {
            endClick = new PVector(mouseX, mouseY);
            plt.zoomViewToScreenCoordinates(startClick.x, startClick.y, endClick.x, endClick.y);
        }
        startClick = null;
    }

    public void show() {
        PApplet.runSketch(new String[]{this.getClass().getName()}, this);
    }
}