package Plot;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/***
 * Object containing data and view info for one set within a plot.
 */
public class PlotData {
    public static enum Style {
        POINT, LINE, DASH
    }

    public static final int BLACK = 0xFF000000;
    public static final int RED = 0xFFFF0000;
    public static final int BLUE = 0xFF0000FF;
    public static final int GREEN = 0xFF00FF00;

    // ------ STYLE -------
    private int strokeColor, fillColor;
    private float strokeWeight;
    private Style style;
    private int dashLength;

    // ------ DATA --------
    private List<Double> x, y;
    private List<Integer> pixelX, pixelY; // display coords (pre-calculated for speed
    // TODO: re-factor this to be in Plot?

    private double minX, maxX, minY, maxY; // for raw values in x, y
    private boolean dirty = false; // has data changed without updating pre-calculated values?

    /***
     * Create PlotData object from pre-made data lists x and y
     * 
     * @param x list of x coordinates
     * @param y list of y coordinates
     */
    public PlotData(List<Double> x, List<Double> y) {
        this.x = x;
        this.y = y;
        reCalculateBounds();
        pixelX = new ArrayList<>();
        pixelY = new ArrayList<>();

        strokeColor = BLACK;
        fillColor = BLACK;
        style = Style.POINT;
        strokeWeight = 1;
        dashLength = 5;
    }

    /***
     * Create PlotData object from pre-made data lists x and y
     * 
     * @param x array of x coordinates
     * @param y array of y coordinates
     */
    public PlotData(double[] x, double[] y) {
        this(MathUtils.toList(x), MathUtils.toList(y));
    }

    /***
     * Create new PlotData object with no data (can be added later with .plot(...)
     * methods ).
     */
    public PlotData() {
        this(new ArrayList<Double>(), new ArrayList<Double>());
    }

    /***
     * Remove (x, y) coordinates at index index
     * 
     * @param index the index to remove (x, y) coordinates from the plot
     */
    public void remove(int index) {
        if (!isInBounds(index))
            return;
        x.remove(index);
        y.remove(index);
    }

    private boolean isInBounds(int index) {
        return index >= 0 && index < x.size();
    }

    /***
     * Set the minimum x value to include in the plot (does not need to be a data
     * point)
     * 
     * @param dataMinX the value to set the minimum display to
     */
    public void setDataMinX(double dataMinX) {
        this.minX = dataMinX;
    }

    /***
     * Set the maximum x value to include in the plot (does not need to be a data
     * point)
     * 
     * @param dataMaxX the value to set the maximum display to
     */
    public void setDataMaxX(double dataMaxX) {
        this.maxX = dataMaxX;
    }

    /***
     * Get the x coordinate of raw data (not pixel value) at index i
     * 
     * @param i
     * @return
     */
    public double getDataX(int i) {
        if (!isInBounds(i)) {
            System.err.println("Index " + i + " is out of bounds");
            return 0;
        }
        return x.get(i);
    }

    public double getDataY(int i) {
        return y.get(i);
    }

    /***
     * Add a new set of data coordinates to the plot
     * 
     * @param new_x new x value
     * @param new_y new y value
     */
    public void add(double new_x, double new_y) {
        x.add(new_x);
        y.add(new_y);

        updateBounds(new_x, new_y);
        dirty = true; // so parent can re-calculate bounds if desired.
    }

    /***
     * Update the min and max values to reflect a new set of data points
     * 
     * @param new_x
     * @param new_y
     */
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
     * Re-scale dataset to bounds given by parameters. Used by Plot.Plot to
     * transform data for display once and then
     * never again until updated.
     * 
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
        this.fillColor = getColorValFor(color);
        return this;
    }

    public PlotData strokeColor(String color) {
        this.strokeColor = getColorValFor(color);
        return this;
    }

    public PlotData strokeWeight(int weight) {
        this.strokeWeight = weight;
        return this;
    }

    public PlotData style(String style) {
        switch (style) {
            case "." -> this.style = Style.POINT;
            case "-" -> this.style = Style.LINE;
            case "--" -> this.style = Style.DASH;
        }

        return this;
    }

    public PlotData dashLength(int length) {
        this.dashLength = length;
        return this;
    }

    private int getColorValFor(String color) {
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

    public float getStrokeWeight() {
        return this.strokeWeight;
    }

    public List<Integer> getScreenXCoords() {
        return this.pixelX;
    }

    public List<Integer> getScreenYCoords() {
        return this.pixelY;
    }

    /***
     * Re-calculate min and max data values by looping over all existing data
     */
    private void reCalculateBounds() {
        for (int i = 0; i < size(); i++) {
            updateBounds(x.get(i), y.get(i));
        }
    }

    public void drawSelf(PApplet window) {
        window.fill(this.getFillColor());
        window.stroke(this.getStrokeColor());
        window.strokeWeight(this.getStrokeWeight());

        switch (this.getStyle()) {
            case POINT -> {
                for (int i = 0; i < this.size(); i++) {
                    window.ellipse(this.getDisplayX(i), this.getDisplayY(i), 2, 2);
                }
            }
            case LINE -> {
                for (int i = 1; i < this.size(); i++) {
                    float x1 = this.getDisplayX(i - 1);
                    float y1 = this.getDisplayY(i - 1);
                    float x2 = this.getDisplayX(i);
                    float y2 = this.getDisplayY(i);
                    window.line(x1, y1, x2, y2);
                }
            }
            case DASH -> {
                for (int i = dashLength; i < this.size() - dashLength; i += dashLength) {
                    for (int j = i - dashLength / 3; j < i + dashLength / 3; j++) {
                        float x1 = this.getDisplayX(j);
                        float y1 = this.getDisplayY(j);
                        float x2 = this.getDisplayX(j + 1);
                        float y2 = this.getDisplayY(j + 1);
                        window.line(x1, y1, x2, y2);
                    }
                }
            }
        }

    }

}