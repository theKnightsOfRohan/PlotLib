import processing.core.PApplet;

public class Main2 extends PApplet {
    Plot plt;         // TODO: add ability to be fixed size; new points bump off old points
    int time = 0;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new Plot(0, 0, 200, 200);

        plt.set(Plot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(Plot.Setting.show_border, true);
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
        PApplet.main("Main2");
    }
}
