import processing.core.PApplet;

public class Main3 extends PApplet {
    Plot plt;         // TODO: add ability to be fixed size; new points bump off old points
    int time = 0;
    double yVal = 400;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new Plot(0, 0, 200, 200);

        plt.set(Plot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(Plot.Setting.show_border, true);
        plt.set(Plot.Setting.freeze_y_scale, true);
    }

    public void draw() {
        background(255);

        plt.plot(1, time, yVal).strokeColor("green").style("-");
        time++;

        yVal += -3 + Math.random()*6;

        plt.draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("Main3");
    }
}
