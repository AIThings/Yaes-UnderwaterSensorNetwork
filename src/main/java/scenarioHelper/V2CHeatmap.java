package scenarioHelper;

import java.awt.Color;
import java.util.List;

import yaes.ui.visualization.painters.IValueToColor;

public class V2CHeatmap implements IValueToColor {

    public enum ColorScheme {
        SimpleColorScheme,
        SingleHueProgression,
        BiPolarProgression,
        BlendedHueProgression,
        PartialSpectralProgression,
        FullSpectralProgression,
        ValueProgression,
        UcfTwoColorProgression
    };

    private ColorScheme colorScheme;
    private double valueLow;
    private double valueHigh;
    private Color color1 = Color.gray;
    private Color color2 = Color.red;

    List<Color> tempScheme;
    // Map Schemes = new HashMap<>();

    /**
     * Simple constructor, later it needs to be improved
     * 
     * @param colorScheme
     *            - the color scheme to be used
     * @param valueLow
     *            - everything below it is minimum
     * @param valueHigh
     *            - everything above it is maximum
     */
    public V2CHeatmap(ColorScheme colorScheme, double valueLow,
        double valueHigh) {
        this.colorScheme = colorScheme;
        this.valueLow = valueLow;
        this.valueHigh = valueHigh;
        // initialize(schemeName);
    }

    /*
     * private void initialize(String schemeName) { tempScheme.add((Color)
     * Schemes.get(schemeName)); for (int i = 0; i < SchemeRange; i++) {
     * tempScheme.add(tempScheme.get(i).darker()); } }
     */

    public List<Color> getColorRange() {
        return tempScheme;

    }

    @Override
    public float getTransparency() {
        return 0.5f;
    }

    @Override
    public Color getColor(double value) {
        switch (colorScheme) {
        case UcfTwoColorProgression:
            return getColorUcfTwoColorProgression(value);
        case BiPolarProgression:
            return getBiPolarProgression(value);
        case SimpleColorScheme:
            return getSimpleColorScheme(value);
        default:
            throw new Error(
                    "colorScheme " + colorScheme + " not implemented yet");
        }
    }

    /**
     * if the value is below valueLow - color1
     * 
     * if above valueHigh - color2
     * 
     * otherwise, blend
     * 
     * @param value
     * @return
     */
    private Color getColorUcfTwoColorProgression(double value) {
        if (value < valueLow) {
            return color1;
        }
        if (value > valueHigh) {
            return color2;
        }
        double blendingRatio = (value - valueLow) / (valueHigh - valueLow);
        int r = color1.getRed()
                + (int) (blendingRatio * (color2.getRed() - color1.getRed()));
        int g = color1.getGreen() + (int) (blendingRatio
                * (color2.getGreen() - color1.getGreen()));
        int b = color1.getBlue()
                + (int) (blendingRatio * (color2.getBlue() - color1.getBlue()));
        return new Color(r, g, b);
    }

    private Color getBiPolarProgression(double value) {
        color1 = Color.red;
        color2 = Color.blue;

        if (value < valueLow) {
            return color1;
        }
        if (value > valueHigh) {
            return color2;
        }
        double blendingRatio = (value - valueLow) / (valueHigh - valueLow);
        int r = color1.getRed()
                + (int) (blendingRatio * (color2.getRed() - color1.getRed()));
        int g = color1.getGreen() + (int) (blendingRatio
                * (color2.getGreen() - color1.getGreen()));
        int b = color1.getBlue()
                + (int) (blendingRatio * (color2.getBlue() - color1.getBlue()));

        return new Color(r, g, b);

    }

    private Color getSimpleColorScheme(double value) {
        color1 = Color.red;
        color2 = Color.yellow;
        if (value < valueLow)
            return color1;
        return color2;
    }

}
