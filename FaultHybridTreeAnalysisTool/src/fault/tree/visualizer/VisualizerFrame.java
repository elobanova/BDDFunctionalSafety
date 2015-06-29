package fault.tree.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import markov.chains.parser.ConnectionXMLParser;

import org.apache.commons.math3.linear.RealMatrix;

public class VisualizerFrame extends JFrame {

	private static final int DEFAULT_LINE_WIDTH = 2;
	private static final int AXIS_OFFSET = 50;
	private static final long serialVersionUID = 1L;
	private static final int NUMBER_OF_HORIZONTAL_WIRES = 8;
	private static final int TEXT_Y_OFFSET = 5;
	private final RealMatrix seriesMatrix;
	private final int topGateId;

	/**
	 * Constructor of the JFrame implementation to draw the time series of the
	 * markov chains members and the top event.
	 * 
	 * @param frameTitle
	 *            a title of the top bar for the JPanel
	 * @param seriesMatrix
	 *            a matrix containing the probabilities of the markov chains
	 *            members and the top event over time
	 * @param topGateId
	 *            an id of the top event
	 */
	public VisualizerFrame(String frameTitle, RealMatrix seriesMatrix, int topGateId) {
		super(frameTitle);
		this.seriesMatrix = seriesMatrix;
		this.topGateId = topGateId;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawLine(AXIS_OFFSET, getHeight() - AXIS_OFFSET, getWidth() - AXIS_OFFSET, getHeight() - AXIS_OFFSET);
		g.drawLine(AXIS_OFFSET, getHeight() - AXIS_OFFSET, AXIS_OFFSET, AXIS_OFFSET);
		paintWire(g);
		paintSeries(g);
	}

	private void paintWire(Graphics g) {
		int rowDimension = seriesMatrix.getRowDimension();
		for (int i = 1; i < rowDimension; i++) {
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(i * ((getWidth() - 2 * AXIS_OFFSET) / rowDimension) + AXIS_OFFSET, AXIS_OFFSET, i
					* ((getWidth() - 2 * AXIS_OFFSET) / rowDimension) + AXIS_OFFSET, getHeight() - AXIS_OFFSET);
			if (i % 5 == 0) {
				g.setColor(Color.DARK_GRAY);
				g.drawString(String.valueOf(ConnectionXMLParser.TIME_INTERVAL * i), i
						* ((getWidth() - 2 * AXIS_OFFSET) / rowDimension) + AXIS_OFFSET, getHeight()
						- g.getFontMetrics().getHeight());
			}
		}

		for (int i = 1; i <= NUMBER_OF_HORIZONTAL_WIRES; i++) {
			g.setColor(Color.LIGHT_GRAY);
			int horizontalWireY = getHeight() - AXIS_OFFSET - i * (getHeight() - 2 * AXIS_OFFSET)
					/ NUMBER_OF_HORIZONTAL_WIRES;
			g.drawLine(AXIS_OFFSET, horizontalWireY, getWidth() - 2 * AXIS_OFFSET, horizontalWireY);

			g.setColor(Color.DARK_GRAY);
			String axisYText = String.valueOf((double) i / NUMBER_OF_HORIZONTAL_WIRES);
			int axisYTextWidth = g.getFontMetrics().stringWidth(axisYText);
			g.drawString(axisYText, AXIS_OFFSET - axisYTextWidth - TEXT_Y_OFFSET, horizontalWireY);
		}
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
				yPoints[j] = (int) (AXIS_OFFSET + (getHeight() - 2 * AXIS_OFFSET) * (1 - seriesMatrix.getEntry(j, i)));
			}
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(DEFAULT_LINE_WIDTH));
			g.drawPolyline(xPoints, yPoints, numberOfTimeStamps);
			String label = i == columnDimension - 1 ? "Gate " + topGateId : "State " + (i + 1);
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
