package fault.tree.visualizer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.apache.commons.math3.linear.RealMatrix;

public class VisualizerFrame extends JFrame {

	private static final int AXIS_OFFSET = 20;
	private static final long serialVersionUID = 1L;
	private final RealMatrix seriesMatrix;

	public VisualizerFrame(String frameTitle, RealMatrix seriesMatrix) {
		super(frameTitle);
		this.seriesMatrix = seriesMatrix;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawLine(AXIS_OFFSET, getHeight() - AXIS_OFFSET, getWidth() - AXIS_OFFSET, getHeight() - AXIS_OFFSET);
		g.drawLine(AXIS_OFFSET, getHeight() - AXIS_OFFSET, AXIS_OFFSET, AXIS_OFFSET);
		paintSeries(g);
	}

	private void paintSeries(Graphics g) {
		int columnDimension = seriesMatrix.getColumnDimension();
		int rowDimension = seriesMatrix.getRowDimension();
		ArrayList<Integer> uniqueColors = getUniqueColors(columnDimension);
		int timeStep = (getWidth() - AXIS_OFFSET) / rowDimension;
		for (int i = 0; i < columnDimension; i++) {
			// paint the basic with its own color
			Integer colorid = uniqueColors.get(i);
			g.setColor(new Color(colorid));
			int numberOfTimeStamps = seriesMatrix.getColumn(i).length;
			int[] xPoints = new int[numberOfTimeStamps];
			int[] yPoints = new int[numberOfTimeStamps];
			for (int j = 0; j < numberOfTimeStamps; j++) {
				xPoints[j] = j * timeStep + AXIS_OFFSET;
				yPoints[j] = (int) (getHeight() * (1 - seriesMatrix.getEntry(j, i)) - AXIS_OFFSET);
			}
			g.drawPolyline(xPoints, yPoints, numberOfTimeStamps);
			String label = "State " + (i + 1);
			g.drawString(label, getWidth() - AXIS_OFFSET - g.getFontMetrics().stringWidth(label),
					yPoints[numberOfTimeStamps - 1]);
		}
	}

	private ArrayList<Integer> getUniqueColors(int amount) {
		final int lowerLimit = 0x10;
		final int upperLimit = 0xE0;
		final int colorStep = (int) ((upperLimit - lowerLimit) / Math.pow(amount, 1f / 3));

		final ArrayList<Integer> colors = new ArrayList<Integer>(amount);

		for (int R = lowerLimit; R < upperLimit; R += colorStep) {
			for (int G = lowerLimit; G < upperLimit; G += colorStep) {
				for (int B = lowerLimit; B < upperLimit; B += colorStep) {
					if (colors.size() >= amount) {
						// The calculated step is not very precise, so this
						// safeguard is appropriate
						return colors;
					} else {
						int color = (R << 16) + (G << 8) + (B);
						colors.add(color);
					}
				}
			}
		}

		return colors;
	}
}
