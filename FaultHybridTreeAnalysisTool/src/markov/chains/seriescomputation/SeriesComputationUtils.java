package markov.chains.seriescomputation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import markov.chains.parser.Connection;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class SeriesComputationUtils {
	public static final double TIME_INTERVAL = 0.5;
	private static final int MAX_NUMBER_OF_ITERATIONS = 15;

	private static double computeGamma(RealMatrix generatorMatrix) {
		double gammaValue = 0.0;
		double maximalValue = 0.0;
		for (int i = 0; i < generatorMatrix.getColumnDimension(); i++) {
			maximalValue = Math.abs(generatorMatrix.getEntry(i, i));
			if (maximalValue > gammaValue) {
				gammaValue = maximalValue;
			}
		}
		return gammaValue;
	}

	private static void computeDistributionInTime(RealMatrix chainProbMatrix, double gammaValue, double time,
			RealMatrix matrixToMultiply) {
		int pos = 1;
		for (double t = TIME_INTERVAL; t <= time; t += TIME_INTERVAL) {
			RealVector current = chainProbMatrix.getRowVector(pos);
			for (int n = 0; n < MAX_NUMBER_OF_ITERATIONS; n++) {
				double expValue = Math.exp(-gammaValue * t);
				current = current.add(matrixToMultiply.getRowVector(n).mapMultiply(
						Math.pow((gammaValue * t), n) * expValue / CombinatoricsUtils.factorial(n)));
			}
			chainProbMatrix.setRowVector(pos, current);
			pos++;
		}
	}

	public static RealMatrix buildGeneratorMatrix(List<Connection> chains, int i) {
		RealMatrix generatorMatrix = MatrixUtils.createRealMatrix(i, i);
		for (Connection conn : chains) {
			generatorMatrix.setEntry(conn.getIdTo() - 1, conn.getIdFrom() - 1, conn.getProbability());
		}

		generatorMatrix = generatorMatrix.transpose();

		for (int j = 0; j < generatorMatrix.getRowDimension(); j++) {
			double sumOfElementsInTheRow = 0.0;
			for (int k = 0; k < generatorMatrix.getColumnDimension(); k++) {
				sumOfElementsInTheRow += generatorMatrix.getEntry(j, k);
			}
			generatorMatrix.setEntry(j, j, -sumOfElementsInTheRow);
		}
		return generatorMatrix;
	}

	public static RealMatrix calculateTimeSeries(Map<Integer, Double> probabilities, RealMatrix generatorMatrix,
			double time) {
		SortedSet<Integer> keys = new TreeSet<Integer>(probabilities.keySet());
		List<Double> sortedProbabilities = new ArrayList<>();
		for (Integer key : keys) {
			Double value = probabilities.get(key);
			sortedProbabilities.add(value);
		}

		int matrixSize = generatorMatrix.getColumnDimension();
		double[] sortedDoubleProbabilities = new double[matrixSize];

		for (int i = 0; i < matrixSize; i++) {
			sortedDoubleProbabilities[i] = sortedProbabilities.get(i);
		}
		if (time == 0.0) {
			return MatrixUtils.createRowRealMatrix(sortedDoubleProbabilities);
		}

		int numberOfTrackedEntries = (int) (time / TIME_INTERVAL);
		RealMatrix matrixToMultiply = MatrixUtils.createRealMatrix(MAX_NUMBER_OF_ITERATIONS, matrixSize);
		RealMatrix chainProbMatrix = MatrixUtils.createRealMatrix(numberOfTrackedEntries + 1, matrixSize);
		RealVector startingDistribution = MatrixUtils.createRealVector(sortedDoubleProbabilities);
		RealMatrix iMatrix = MatrixUtils.createRealIdentityMatrix(matrixSize);

		double gammaValue = computeGamma(generatorMatrix);
		RealMatrix transMatrix = iMatrix.add(generatorMatrix.scalarMultiply(1 / gammaValue));
		chainProbMatrix.setRowVector(0, startingDistribution);

		for (int i = 0; i < MAX_NUMBER_OF_ITERATIONS; i++) {
			matrixToMultiply.setRowVector(i, transMatrix.power(i).preMultiply(startingDistribution));
		}

		computeDistributionInTime(chainProbMatrix, gammaValue, time, matrixToMultiply);
		return chainProbMatrix;
	}
}
