package fault.tree.visualizer;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import org.apache.commons.math3.linear.RealMatrix;

public class Visualizer {

	private static final String FRAME_TITLE = "Probability values of MC states";

	public static void paint(RealMatrix seriesMatrix, int topGateId) {
		VisualizerFrame frame = new VisualizerFrame(FRAME_TITLE, seriesMatrix, topGateId);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}

}
