import processing.core.PApplet;

public class GraphAnimation extends PApplet {
    ScatterPlot plt;
    double timeSteps = 100;
    int time = 0;
    double[] x, y1, y2, y3, y4, dy;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new ScatterPlot(100, 100, 600, 600);

        x = MathUtils.linspace( -5, 5, 100);
        y1 = MathUtils.apply((v -> v*v/2+5), x);
        y2 = MathUtils.apply((v -> v), x);

        y3 = MathUtils.apply((v -> v*v/2+5), x);
        y4 = MathUtils.apply((v -> v), x);

        dy = MathUtils.apply((v -> v/timeSteps), y2);

        plt.set(ScatterPlot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(ScatterPlot.Setting.show_border, true);

        plt.plot(x, y1).fillColor("red").strokeColor("red").style("-");
        plt.plot(x, y2).fillColor("blue").strokeColor("blue").style("-");

        plt.plot(x, y3).fillColor("red").strokeColor("red").style(".");
        plt.plot(x, y4).fillColor("blue").strokeColor("blue").style(".");
    }

    public void draw() {
        background(255, 255, 255);
        time++;

        if (time < timeSteps) {
            for (int i = 0; i < dy.length; i++) {
                y3[i] = y3[i] - dy[i];
                y4[i] = y4[i] - dy[i];
            }

            plt.removePlot(2);
            plt.removePlot(2);
            plt.plot(x, y3).fillColor("red").strokeColor("red").style(".");
            plt.plot(x, y4).fillColor("blue").strokeColor("blue").style(".");
        }

        plt.draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("GraphAnimation");
    }
}
