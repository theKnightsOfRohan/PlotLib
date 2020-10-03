import processing.core.PApplet;

public class Main2 extends PApplet {
    Plot plt, plt2;         // TODO: add ability to be fixed size; new points bump off old points
    int time = 0;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        plt = new Plot(0, 0, 200, 200);
        plt2 = new Plot(0, 300, 200, 200);

        plt.set(Plot.Setting.show_axes, true);      // TODO: make a nice api for this
        plt.set(Plot.Setting.show_border, true);

        plt2.set(Plot.Setting.show_axes, true);
        plt2.set(Plot.Setting.show_border, true);
    }

    public void draw() {
        background(255);

        if (mousePressed) {
            plt.plot(time, mouseX).strokeColor("red");
            plt2.plot(time, mouseY).strokeColor("green");
            time++;
        }

        plt.draw(this);
        plt2.draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("Main2");
    }
}
