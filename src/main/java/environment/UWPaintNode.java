package environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.visualization.paintSensorNode;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.painters.PainterHelper;

public class UWPaintNode extends paintSensorNode implements Serializable {
	private static final long serialVersionUID = 8108059669986420496L;

	public UWPaintNode(SensorNetworkWorld sensorNetworkWorld) {
		super(sensorNetworkWorld);
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		// TODO Auto-generated method stub
		Graphics2D g2 = (Graphics2D) g;
		super.paint(g2, o, panel);
	}

	@Override
	protected void paintNode(Graphics2D g, Object o, VisualCanvas panel) {
		SensorNode node = (SensorNode) o;
		if (node.isEnabled()) {
			Graphics2D g2 = (Graphics2D) g;
			PainterHelper.paintRectangleAtLocation(node.getLocation(), 7, Color.black, Color.white, g2, panel);
		} else {
			PainterHelper.paintRectangleAtLocation(node.getLocation(), 7, Color.red, Color.black, g, panel);
		}
	}

}
