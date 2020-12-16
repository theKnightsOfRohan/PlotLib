package Plot;

import java.util.ArrayList;
import java.util.List;

/***
 * Object containing data and view info for one set within a plot.
 */
public class PlotData {
    public static enum Style { POINT, LINE }
    public static final int BLACK = 0xFF000000;
    public static final int RED = 0xFFFF0000;
    public static final int BLUE = 0xFF0000FF;
    public static final int GREEN = 0xFF00FF00;

    // ------ STYLE -------
    private int strokeColor, fillColor;
    private Style style;

    // ------ DATA --------
    private List<Double> x, y;
    private List<Integer> pixelX, pixelY;   // display coords (pre-calculated for speed
                                            // TODO: re-factor this to be in Plot?

    private double minX, maxX, minY, maxY;  // for raw values in x, y
    private boolean dirty = false;          // has data changed without updating pre-calculated values?

    /***
     * Create PlotData object from pre-made data lists x and y
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
    }

    /***
     * Create PlotData object from pre-made data lists x and y
     * @param x array of x coordinates
     * @param y array of y coordinates
     */
    public PlotData(double[] x, double[] y) {
        this(MathUtils.toList(x), MathUtils.toList(y));
    }

    /***
     * Create new PlotData object with no data (can be added later with .plot(...) methods ).
     */
    public PlotData() {
        this(new ArrayList<Double>(), new ArrayList<Double>());
    }

    /***
     * Remove (x, y) coordinates at index index
     * @param index the index to remove (x, y) coordinates from the plot
     */
    public void remove(int index) {
        if (!isInBounds(index)) return;
        x.remove(index);
        y.remove(index);
    }

    private boolean isInBounds(int index) {
        return index >= 0 && index < x.size();
    }

    /***
     * Set the minimum x value to include in the plot (does not need to be a data point)
     * @param dataMinX the value to set the minimum display to
     */
    public void setDataMinX(double dataMinX) {
        this.minX = dataMinX;
    }

    /***
     * Set the maximum x value to include in the plot (does not need to be a data point)
     * @param dataMaxX the value to set the maximum display to
     */
    public void setDataMaxX(double dataMaxX) {
        this.maxX = dataMaxX;
    }

    /***
     * Get the x coordinate of raw data (not pixel value) at index i
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

    /***
     * Add a new set of data coordinates to the plot
     * @param new_x new x value
     * @param new_y new y value
     */
    public void add(double new_x, double new_y) {
        x.add(new_x);
        y.add(new_y);

        updateBounds(new_x, new_y);
        dirty = true;   // so parent can re-calculate bounds if desired.
    }

    /***
     * Update the min and max values to reflect a new set of data points
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
     * Re-scale dataset to bounds given by parameters.  Used by Plot.Plot to transform data for display once and then
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
        this.fillColor = getColorValFor(color);
        return this;
    }

    public PlotData strokeColor(String color) {
        this.strokeColor = getColorValFor(color);
        return this;
    }

    // TODO: add dashed-line style
    public PlotData style(String style) {
        if (style.equals(".")) {
            this.style = Style.POINT;
        } else if (style.equals("-")) {
            this.style = Style.LINE;
        }
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
}