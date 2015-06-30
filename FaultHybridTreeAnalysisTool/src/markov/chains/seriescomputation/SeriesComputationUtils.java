package markov.chains.seriescomputation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import markov.chains.parser.Connection;
import net.sf.javabdd.BDD;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class SeriesComputationUtils {
	private static final int MAX_NUMBER_OF_ITERATIONS = 21;
	private static HashMap<Integer, Integer> markovChains = new HashMap<>();

	/**
	 * Computes the uniform rate parameter
	 * @param generatorMatrix the generator matrix of the states
	 * @return the uniform rate parameter
	 */
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

	/**
	 * Computes distribution for every point of time
	 * @param chainProbMatrix the matrix with probabilities
	 * @param gammaValue the uniform rate parameter
	 * @param time the complete mission time
	 * @param matrixToMultiply the probability transition matrix at the power of MAX_NUMBER_OF_ITERATIONS
	 * @param timeInterval the sampling interval
	 */
	private static void computeDistributionInTime(RealMatrix chainProbMatrix, double gammaValue, double time,
			RealMatrix matrixToMultiply, double timeInterval) {
		int pos = 1;
		for (double t = timeInterval; t <= time; t += timeInterval) {
			RealVector current = chainProbMatrix.getRowVector(pos);
			for (int n = 0; n < MAX_NUMBER_OF_ITERATIONS; n++) {
				double expValue = Math.exp(-gammaValue * t);
				current = current.add(matrixToMultiply.getRowVector(n).mapMultiply(
						Math.pow((gammaValue * t), n) / CombinatoricsUtils.factorial(n) * expValue));
			}
			chainProbMatrix.setRowVector(pos, current);
			pos++;
		}
	}
	
	/**
	 * Builts generator matrix
	 * @param chains list of markov chains
	 * @param size the size of generator matrix
	 * @return the generator matrix
	 */
	public static RealMatrix buildGeneratorMatrix(List<Connection> chains, int size) {
		RealMatrix generatorMatrix = MatrixUtils.createRealMatrix(size, size);
		for (Connection conn : chains) {
			generatorMatrix.setEntry(conn.getIdTo() - 1, conn.getIdFrom() - 1, conn.getProbability());
		}

		generatorMatrix = generatorMatrix.transpose();
		int counter = 0;
		for (int j = 0; j < generatorMatrix.getRowDimension(); j++) {
			double sumOfElementsInTheRow = 0.0;
			for (int k = 0; k < generatorMatrix.getColumnDimension(); k++) {
				sumOfElementsInTheRow += generatorMatrix.getEntry(j, k);
				if (generatorMatrix.getEntry(j, k) != 0) {
					if (markovChains.containsKey(k)) {
						markovChains.put(k, markovChains.get(k));
					} else if (markovChains.containsKey(size)) {
						markovChains.put(k, markovChains.get(j));
					} else {
						markovChains.put(j, counter);
						markovChains.put(k, counter);
						counter++;
					}
				}
			}
			if (!markovChains.containsKey(size)) {
				markovChains.put(size, counter);
				counter++;
			}
			generatorMatrix.setEntry(j, j, -sumOfElementsInTheRow);
		}
		return generatorMatrix;
	}

	public static HashMap<Integer, Integer> getMarkovChains() {
		return markovChains;
	}
	
	/**
	 * Calculates time series for continous markov chains by using uniformization
	 * @param probabilities the map with initial probabilities of states
	 * @param generatorMatrix the generator matrix of states  
	 * @param time the complete mission time
	 * @param timeInterval the sampling interval
	 * @return the time series matrix as RealMatrix, 
	 * where the row are the probabilities at time t and a column is a state
	 */
	public static RealMatrix calculateTimeSeries(Map<Integer, Double> probabilities, RealMatrix generatorMatrix,
			double time, double timeInterval) {
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

		int numberOfTrackedEntries = (int) (time / timeInterval);
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

		computeDistributionInTime(chainProbMatrix, gammaValue, time, matrixToMultiply, timeInterval);
		return chainProbMatrix;
	}
	
	/**
	 * Calculates probabilities of the top event over time. It iterates over every solution which is 
	 * obtained from allsat() method. Every solution is an array with values for states of markov chains.
	 * In one markov chain can be exactly one state with value 1, otherwise the probability of this path is 0.
	 * If a variable has value 1, it gets multiplied with the temp probability. 
	 * If a variable has value 0, (1 - the probability) gets multiplied.  
	 * @param bddTree BDD tree
	 * @param seriesMatrix time series matrix, a column is a state 
	 * @param markovChains the map of states and corresponding markov chains
	 * @return vector of probabilities for the top event as a RealVector
	 */
	public static RealVector calculateProbabilitiesOfTopEvent(BDD bddTree, RealMatrix seriesMatrix,
			HashMap<Integer, Integer> markovChains) {
		List<?> list = (List<?>) bddTree.allsat();
		int matrixSize = seriesMatrix.getRowDimension();
		double[] probabilities = new double[matrixSize];

		byte[] solutions;
		for (Object item : list) {
			double[] currentProb = new double[matrixSize];
			Arrays.fill(currentProb, 1.0);
			solutions = (byte[]) item;

			if (checkSolution(markovChains, solutions)) {
				for (int i = 0; i < solutions.length; i++) {
					if (solutions[i] == 1) {
						for (int j = 0; j < matrixSize; j++) {
							currentProb[j] *= seriesMatrix.getEntry(j, i);
						}
					} else if (solutions[i] == 0) {
						// check if there is another state in the same markov
						// chain
						if (Collections.frequency(markovChains.values(), markovChains.get(i)) == 1) {
							for (int j = 0; j < matrixSize; j++) {
								currentProb[j] *= (1 - seriesMatrix.getEntry(j, i));
							}
						}
					}
				}
				for (int i = 0; i < probabilities.length; i++) {
					probabilities[i] += currentProb[i];
				}
			}
		}
		RealVector topEventProbabilities = MatrixUtils.createRealVector(probabilities);
		return topEventProbabilities;
	}

	/**
	 * Checks that the solution is correct. In one markov chain exactly one state should have value 1
	 * @param markovChains the map of states and corresponding markov chains
	 * @param solution an array of variables assignment
	 * @return true if solution is correct, false otherwise
	 */
	private static boolean checkSolution(HashMap<Integer, Integer> markovChains, byte[] solution) {
		int valueA;
		int valueB;
		for (int state = 0; state < solution.length; state++) {
			valueA = solution[state];
			for (int n = state; n < solution.length; n++) {
				valueB = solution[n];
				int counter = 0;
				if (markovChains.get(state) == markovChains.get(n)) {
					if (state != n && valueA == 1 && valueB == 1) {
						return false;
					} else if (state != n && valueA == 0 && valueB == 0){
						counter++;
					}
				}
				if (Collections.frequency(markovChains.values(), markovChains.get(state))-1 == counter &&
						valueA == 0 && counter != 0){
					return false;
				}
			}
		}
		return true;
	}

}
