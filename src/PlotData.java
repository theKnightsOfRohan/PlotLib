import java.util.ArrayList;
import java.util.List;

public class PlotData {
    public enum Style { POINT, LINE }
    public static final int BLACK = 0xFF000000;
    public static final int RED = 0xFFFF0000;
    public static final int BLUE = 0xFF0000FF;
    public static final int GREEN = 0xFF00FF00;

    private List<Double> x, y;
    private List<Integer> pixelX, pixelY;  // display coords
    private double minX, maxX, minY, maxY;  // for raw values in x, y
    private int strokeColor, fillColor;
    private Style style;
    private boolean dirty = false;

    public PlotData(List<Double> x, List<Double> y) {
        this.x = x;
        this.y = y;
        reCalculateBounds();
        pixelX = new ArrayList<>();
        pixelY = new ArrayList<>();

        strokeColor = BLACK;
        fillColor = BLACK;
        style = Style.POINT;
    }

    public PlotData() {
        this(new ArrayList<Double>(), new ArrayList<Double>());
    }

    private void reCalculateBounds() {
        for (int i = 0; i < size(); i++) {
            updateBounds(x.get(i), y.get(i));
        }
    }

    public void remove(int index) {
        // TODO: check if index is in bounds
        x.remove(index);
        y.remove(index);
    }

    public void setDataMinX(double dataMinX) {
        this.minX = dataMinX;
    }

    public void setDataMaxX(double dataMaxX) {
        this.maxX = dataMaxX;
    }

    public double getDataX(int i) {
        // TODO: range check
        return x.get(i);
    }

    public void add(double new_x, double new_y) {
        x.add(new_x);
        y.add(new_y);

        updateBounds(new_x, new_y);
        dirty = true;   // so parent can re-calculate bounds if desired.
    }

    private void updateBounds(double new_x, double new_y) {
        if (new_x < minX) {
            minX = new_x;
        }
        if (new_x > maxX) {
            maxX = new_x;
        }
        if (new_y < minY) {
            minY = new_y;
        }
        if (new_y > maxY) {
            maxY = new_y;
        }
    }

    public void setFillColor(int color) {
        this.fillColor = color;
    }

    public void setStrokeColor(int color) {
        this.strokeColor = color;
    }

    public int size() {
        return x.size();
    }

    public int getFillColor() {
        return this.fillColor;
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }

    public float getDisplayX(int i) {
        return this.pixelX.get(i);
    }

    public float getDisplayY(int i) {
        return this.pixelY.get(i);
    }

    /***
     * Re-scale dataset to bounds given by parameters.  Used by Plot to transform data for display once and then
     * never again until updated.
     * @param displayMinX
     * @param displayMaxX
     * @param displayMinY
     * @param displayMaxY
     */
    public void rescale(double displayMinX, double displayMaxX, double displayMinY, double displayMaxY,
                        double dataMinX, double dataMaxX, double dataMinY, double dataMaxY) {
        pixelX.clear();
        pixelY.clear();

        for (int i = 0; i < size(); i++) {
            pixelX.add((int) ScatterPlot.map(x.get(i), dataMinX, dataMaxX, displayMinX, displayMaxX));
            pixelY.add((int) ScatterPlot.map(y.get(i), dataMinY, dataMaxY, displayMinY, displayMaxY));
        }
    }

    public PlotData fillColor(String color) {
        this.fillColor = getValFor(color);
        return this;
    }

    public PlotData strokeColor(String color) {
        this.strokeColor = getValFor(color);
        return this;
    }

    public PlotData style(String style) {
        if (style.equals(".")) {
            this.style = Style.POINT;
        } else if (style.equals("-")) {
            this.style = Style.LINE;
        }
        return this;
    }

    private int getValFor(String color) {
        if (color.equals("red")) {
            return RED;
        } else if (color.equals("blue")) {
            return BLUE;
        } else if (color.equals("black")) {
            return BLACK;
        } else if (color.equals("green")) {
            return GREEN;
        }

        return BLACK;
    }

    public double getDataMinX() {
        return minX;
    }

    public double getDataMaxX() {
        return maxX;
    }

    public double getDataMinY() {
        return minY;
    }

    public double getDataMaxY() {
        return maxY;
    }


    public boolean isDirty() {
        return dirty;
    }

    public void setClean() {
        this.dirty = false;
    }

    public Style getStyle() {
        return this.style;
    }
}