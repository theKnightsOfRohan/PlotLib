package Plot;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import static Plot.PlotData.Style.*;

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

    public PlotData(PlotData toCopy) {
        this.strokeWeight = toCopy.strokeWeight;
        this.strokeColor = toCopy.strokeColor;
        this.fillColor = toCopy.fillColor;
        this.style = toCopy.style; // TODO: will this cause bugs?
        this.x = new ArrayList<Double>();
        for (Double val : toCopy.x) {
            x.add(val);
        }
        this.y = new ArrayList<Double>();
        for (Double val : toCopy.y) {
            y.add(val);
        }

        this.pixelX = new ArrayList<Integer>();
        for (Integer val : toCopy.pixelX) {
            pixelX.add(val);
        }
        this.pixelY = new ArrayList<Integer>();
        for (Integer val : toCopy.pixelY) {
            pixelY.add(val);
        }
    }

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
        style = POINT;
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

    /**
     * Sets the fill color of the plot data.
     * Currently supports: "red", "blue", "black", "green"
     * Attempting an unsupported color will default to black.
     * 
     * @param color the color to set
     * @return the updated PlotData object
     */
    public PlotData fillColor(String color) {
        this.fillColor = getColorValFor(color);
        return this;
    }

    /**
     * Sets the stroke color of the plot data.
     * Currently supports: "red", "blue", "black", "green"
     * Attempting an unsupported color will default to black.
     * 
     * @param color the color to set
     * @return the updated PlotData object
     */
    public PlotData strokeColor(String color) {
        this.strokeColor = getColorValFor(color);
        return this;
    }

    public PlotData strokeWeight(int weight) {
        this.strokeWeight = weight;
        return this;
    }

    /**
     * Sets the style of the plot data.
     * "." = points, "-" = line, "--" = dashed line
     * 
     * (Note: dash style requires a .dashLength() to be set.
     * 
     * @param style the style to set
     * @return the PlotData object with the updated style
     */
    public PlotData style(String style) {
        if (".".equals(style)) {
            this.style = POINT;
        } else if ("-".equals(style)) {
            this.style = LINE;
        } else if ("--".equals(style)) {
            this.style = Style.DASH;
        } else {
            System.err.println("Style " + style + " not recognized.");
        }

        return this;
    }

    /**
     * Sets the length of the dash for the plot data. This value determines the
     * number of points in your dataset to connect at a time.
     * 
     * @param length the length of the dash
     * @return the updated PlotData object
     */
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
        } else {
            System.err.println("Color " + color + " not recognized.\nDefaulting to black.");
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

    /**
     * Draws the plot data on the given PApplet window using the specified plot
     * configuration. Draws the line based on the style of the plot data.
     *
     * @param window The PApplet window on which to draw the plot data.
     * @param p      The Plot to use for drawing.
     */
    public void drawSelf(PApplet window, Plot p) {
        window.fill(this.getFillColor());
        window.stroke(this.getStrokeColor());
        window.strokeWeight(this.getStrokeWeight());

        if (this.getStyle() == POINT) {
            for (int i = 0; i < this.size(); i++) {
                if (p.isInBounds(this.getDisplayX(i), this.getDisplayY(i))) {
                    window.ellipse(this.getDisplayX(i), this.getDisplayY(i), 2, 2);
                }
            }
        } else if (this.getStyle() == LINE) {
            for (int i = 1; i < this.size(); i++) {
                float x1 = this.getDisplayX(i - 1);
                float y1 = this.getDisplayY(i - 1);
                float x2 = this.getDisplayX(i);
                float y2 = this.getDisplayY(i);

                int[] clipped = p.clipLine((int) x1, (int) y1, (int) x2, (int) y2);

                if (clipped != null) {
                    window.line(clipped[0], clipped[1], clipped[2], clipped[3]);
                }
            }
        } else if (this.getStyle() == DASH) {
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
