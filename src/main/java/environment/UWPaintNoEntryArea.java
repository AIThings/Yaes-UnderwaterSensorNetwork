package environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.Serializable;

import yaes.sensornetwork.visualization.paintEnvironment;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PaintSpec;
import yaes.ui.visualization.painters.PainterHelper;

public class UWPaintNoEntryArea extends paintEnvironment
    implements IPainter, Serializable {
    private PaintSpec activeSpec = null;
    private static final long serialVersionUID = -9107046949541717580L;

    public UWPaintNoEntryArea() {
        activeSpec = PaintSpec.createFill(Color.DARK_GRAY);
    }

    @Override
    public int getLayer() {
        return BACKGROUND_EVENT_LAYER;
    }

    @Override
    public void paint(Graphics2D g, Object o, VisualCanvas panel) {
        super.paint(g, o, panel);
        UWEnvironment ce = (UWEnvironment) o;
        if (ce.isActive())
            for (Shape s : ce.getShapes())
                PainterHelper.paintShape(s, activeSpec, g, panel);

    }

    @Override
    public void registerParameters(
        VisualizationProperties visualizationProperties) {
        // TODO Auto-generated method stub

    }

}
