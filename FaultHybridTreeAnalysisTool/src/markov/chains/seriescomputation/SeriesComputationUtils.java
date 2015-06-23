package markov.chains.seriescomputation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import markov.chains.parser.Connection;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class SeriesComputationUtils {
	private static final double TIME_INTERVAL = 0.5;

	public static RealMatrix buildGeneratorMatrix(List<Connection> chains, int i) {
		RealMatrix generatorMatrix = MatrixUtils.createRealMatrix(i, i);
		for (Connection conn : chains) {
			generatorMatrix.setEntry(conn.getIdTo() - 1, conn.getIdFrom() - 1,
					conn.getProbability());
		}
		return generatorMatrix;
	}

	public static RealMatrix calculateTimeSeries(
			Map<Integer, Double> probabilities, RealMatrix generatorMatrix,
			double time) {
		RealMatrix chainProbMatrix = null;
		SortedSet<Integer> keys = new TreeSet<Integer>(probabilities.keySet());
		List<Double> sortedProbabilities = new ArrayList<>();
		for (Integer key : keys) {
			Double value = probabilities.get(key);
			sortedProbabilities.add(value);
		}

		double[] sortedDoubleProbabilities = new double[sortedProbabilities
				.size()];

		for (int i = 0; i < sortedProbabilities.size(); i++) {
			sortedDoubleProbabilities[i] = sortedProbabilities.get(i);
		}
		if (time == 0.0) {
			return MatrixUtils.createRowRealMatrix(sortedDoubleProbabilities);
		}

		return chainProbMatrix;
	}
}
