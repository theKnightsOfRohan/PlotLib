package Plot;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;

public class UnivariateData {
    public static enum Style {
        BAR,
    }

    public static final int BLACK = 0xFF000000;
    public static final int RED = 0xFFFF0000;
    public static final int BLUE = 0xFF0000FF;
    public static final int GREEN = 0xFF00FF00;

    // ------ STYLE -------
    private int strokeColor, fillColor;
    private float strokeWeight;
    private Style style;

    // ------ DATA --------
    private List<Double> rawData, sortedData;
    private List<Integer> pixelX, pixelY; // display coords (pre-calculated for speed
    private int pixelWidth;
    private int[] bins;

    private double minData, maxData, maxFreq; // For histogram
    private int binAmount;
    private boolean dirty = false; // has data changed without updating pre-calculated values?

    public UnivariateData(UnivariateData toCopy) {
        this.strokeWeight = toCopy.strokeWeight;
        this.strokeColor = toCopy.strokeColor;
        this.fillColor = toCopy.fillColor;
        this.style = toCopy.style; // TODO: will this cause bugs?
        this.rawData = new ArrayList<Double>();
        this.minData = toCopy.minData;
        this.maxData = toCopy.maxData;
        this.binAmount = toCopy.binAmount;
        this.maxFreq = toCopy.maxFreq;

        for (Double val : toCopy.rawData) {
            rawData.add(val);
        }

        // Something something OOP something something truth
        this.sortedData = MathUtils.copyDoubleList(toCopy.sortedData);
        this.sortedData.sort(null);

        this.bins = MathUtils.copyArr(toCopy.bins);
        this.pixelWidth = toCopy.pixelWidth;
        this.pixelX = MathUtils.copyIntList(toCopy.pixelX);
        this.pixelY = MathUtils.copyIntList(toCopy.pixelY);
    }

    /***
     * Create UnivariateData object from pre-made data.
     *
     * @param data the data to plot
     */
    public UnivariateData(List<Double> data) {
        this.rawData = data;
        this.sortedData = MathUtils.copyDoubleList(data);
        sortedData.sort(null);

        reCalculateBounds();
        reCalculateBins();
        pixelWidth = 0;
        pixelX = new ArrayList<>();
        pixelY = new ArrayList<>();

        strokeColor = BLACK;
        fillColor = BLACK;
        style = Style.BAR;
        strokeWeight = 1;
    }

    public void reCalculateBins() {
        bins = new int[binAmount];
        for (double val : rawData) {
            addToBins(val);
        }
    }

    /***
     * Create UnivariateData object from pre-made data
     *
     * @param data the data to plot
     */
    public UnivariateData(double[] data) {
        this(MathUtils.toList(data));
    }

    /***
     * Create new UnivariateData object with no data
     *
     * (can be added later with .plot(...) methods ).
     */
    public UnivariateData() {
        this(new ArrayList<Double>());
    }

    /***
     * Remove data point at index index
     *
     * @param index the index to remove from the raw data
     */
    public void remove(int index) {
        if (!isInBounds(index))
            return;

        sortedData.remove(rawData.remove(index));
    }

    private boolean isInBounds(int index) {
        return index >= 0 && index < rawData.size();
    }

    /***
     * Set the minimum data value to include in the plot (does not need to be a data
     * point)
     *
     * @param newMinData the value to set the minimum display to
     */
    public void setMinData(double newMinData) {
        this.minData = newMinData;
    }

    /***
     * Set the maximum data value to include in the plot (does not need to be a data
     * point)
     *
     * @param newMaxData the value to set the maximum display to
     */
    public void setMaxData(double newMaxData) {
        this.maxData = newMaxData;
    }

    /***
     * Get the x coordinate of raw data (not sorted data) at index
     *
     * @param index
     * @return
     */
    public double getMinData(int index) {
        if (!isInBounds(index)) {
            System.err.println("Index " + index + " is out of bounds");
            return 0;
        }
        return rawData.get(index);
    }

    /***
     * Get the frequency of the bin that the value is in
     *
     * @param val the value to get the bin's frequency of
     * @return the frequency of the value
     */
    public int getDataFrequency(double val) {
        double binIncrement = (maxData - minData) / binAmount;
        int bin = (int) ((val - minData) / binIncrement);

        return bins[bin];
    }

    /***
     * Add a new data point to the plot
     *
     * @param new_val the new value to add
     */
    public void add(double new_val) {
        rawData.add(new_val);
        addToSorted(new_val);
        addToBins(new_val);
        updateBounds(new_val);
        dirty = true; // so parent can re-calculate bounds if desired.
    }

    public void addToBins(double new_val) {
        double binIncrement = (maxData - minData) / binAmount;
        int bin = (int) ((new_val - minData) / binIncrement);

        bins[bin]++;
        if (bins[bin] > maxFreq) {
            maxFreq = bins[bin];
        }
    }

    // TODO: Implement Binary search for faster insertion
    // Or maybe just use a TreeSet?
    public void addToSorted(double new_val) {
        int index = 0;
        while (index < sortedData.size() && sortedData.get(index) < new_val) {
            index++;
        }

        sortedData.add(index, new_val);
    }

    /***
     * Add multiple new data points to the plot.
     *
     * @param new_data the new values to add
     */
    public void add(double[] new_data) {
        for (double val : new_data) {
            add(val);
        }
    }

    private void reCalculateBounds() {
        minData = sortedData.get(0);
        maxData = sortedData.get(sortedData.size() - 1);
    }

    /***
     * Update the min and max values to reflect a new set of data points
     *
     * @param new_data new data value
     */
    private void updateBounds(double new_data) {
        if (new_data < minData) {
            minData = new_data;
        } else if (new_data > maxData) {
            maxData = new_data;
        }
    }

    /***
     * @return the size of the raw data set
     */
    public int size() {
        return rawData.size();
    }

    public int getFillColor() {
        return this.fillColor;
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }

    public float getDisplayBarWidth() {
        return this.pixelWidth;
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
        pixelY.clear();

        pixelX = (int) ScatterPlot.map(calcBarWidth(), dataMinX, dataMaxX, displayMinX, displayMaxX);

        for (int i = 0; i < bins.length; i++) {
            pixelY.add((int) ScatterPlot.map(bins[i], dataMinY, dataMaxY, displayMinY, displayMaxY));
        }
    }

    /**
     * Sets the fill color of the plot data.
     * Currently supports: "red", "blue", "black", "green"
     * Attempting an unsupported color will default to black.
     * 
     * @param color the color to set
     * @return the updated UnivariateData object
     */
    public UnivariateData fillColor(String color) {
        this.fillColor = getColorValFor(color);
        return this;
    }

    /**
     * Sets the stroke color of the plot data.
     * Currently supports: "red", "blue", "black", "green"
     * Attempting an unsupported color will default to black.
     * 
     * @param color the color to set
     * @return the updated UnivariateData object
     */
    public UnivariateData strokeColor(String color) {
        this.strokeColor = getColorValFor(color);
        return this;
    }

    public UnivariateData strokeWeight(int weight) {
        this.strokeWeight = weight;
        return this;
    }

    /**
     * Sets the style of the plot data.
     * "|" = BAR
     * 
     * @param style the style to set
     * @return the UnivariateData object with the updated style
     */
    public UnivariateData style(String style) {
        if (style.equals("|")) {
            this.style = Style.BAR;
        } else {
            System.err.println("Style " + style + " not recognized.");
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
        } else {
            System.err.println("Color " + color + " not recognized.\nDefaulting to black.");
        }

        return BLACK;
    }

    public double getMinData() {
        return minData;
    }

    public double getMaxData() {
        return maxData;
    }

    public int getBinAmount() {
        return binAmount;
    }

    public double getMaxFreq() {
        return maxFreq;
    }

    public int[] getBins() {
        return bins;
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

        if (this.getStyle() == Style.BAR) {
            for (int i = 0; i < this.getBinAmount(); i++) {
                window.rect(this.getBarX(i), this.getBarY(i), this.getDisplayBarWidth(), this.getDisplayBarHeight());
            }
        }
    }

    public double calcBinWidth() {
        return (maxData - minData) / binAmount;
    }
}
