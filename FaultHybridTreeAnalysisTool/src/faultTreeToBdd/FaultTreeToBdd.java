package faultTreeToBdd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import fault.tree.model.xml.BasicNode;
import fault.tree.model.xml.EventNode;
import fault.tree.model.xml.GateNode;
import fault.tree.model.xml.OperationEnum;

public class FaultTreeToBdd {
	private static final int FALSE_SAT_VALUE = 0;
	private static final int TRUE_SAT_VALUE = 1;
	private Map<Integer, Double> probabilities = new HashMap<>();
	private static final int CACHE_SIZE = 1000;
	private static final int NODE_NUMBER = 1000;

	private Set<Integer> idMap = new HashSet<>();
	private BDDFactory bddFactory;
	private HashMap<Integer, BDD> bddMap = new HashMap<Integer, BDD>();

	/**
	 * Builds the BDD diagram from the fault tree, which root is passed as a
	 * parameter.
	 * 
	 * @param faultTree
	 *            the root of the fault tree from which BDD will be constructed
	 * @param basicNodesFromMarkovChains
	 *            a list of basic events
	 * @return the generated BDD
	 */
	public BDD faultTreeToBDD(GateNode faultTree, List<BasicNode> basicNodesFromMarkovChains) {
		bddFactory = BDDFactory.init(NODE_NUMBER, CACHE_SIZE);
		bddFactory.setVarNum(basicNodesFromMarkovChains.size());
		for (BasicNode basicNode : basicNodesFromMarkovChains) {
			if (!this.idMap.contains(basicNode.getId() - 1)) {
				addToIdMap(basicNode.getId() - 1);
			}
			addProbabilityOfBasicNode(basicNode.getId() - 1, basicNode.getProbability());
		}
		BDD bdd = buildBdd(faultTree);

		return bdd;
	}

	private BDD buildBdd(EventNode node) {
		BDD bdd = null;
		Integer nodeId = new Integer(node.getId() - 1);
		if (bddMap.containsKey(nodeId)) {
			bdd = bddMap.get(nodeId);
		} else if (node instanceof BasicNode) {
			bdd = bddFactory.ithVar(node.getId() - 1);
			bddMap.put(nodeId, bdd);
		} else if (node instanceof GateNode) {
			GateNode gate = (GateNode) node;
			for (EventNode enode : gate.getChildEvents()) {
				if (bdd == null) {
					bdd = buildBdd(enode);
				} else {
					if (OperationEnum.isAND(gate.getOperation())) {
						bdd = bdd.and(buildBdd(enode));
					} else {
						bdd = bdd.or(buildBdd(enode));
					}
				}
			}
			bddMap.put(nodeId, bdd);
		}
		return bdd;
	}

	/**
	 * Computes the failure probability of the top event in the BDD
	 * 
	 * @param bddTree
	 *            a BDD tree
	 * @return the failure probability of the top event
	 */
	public double getFailure(BDD bddTree) {
		List<?> list = (List<?>) bddTree.allsat();
		double failureProbability = 0;
		byte[] solutions;
		for (Object item : list) {
			double current = 1.0;
			solutions = (byte[]) item;
			for (int i = 0; i < solutions.length; i++) {
				if (solutions[i] == TRUE_SAT_VALUE) {
					current *= probabilities.get(i);
				} else if (solutions[i] == FALSE_SAT_VALUE) {
					current *= (1 - probabilities.get(i));
				}
			}
			failureProbability += current;
		}
		return failureProbability;
	}

	/**
	 * Returns the generator matrix size
	 * 
	 * @return the generator matrix size
	 */
	public int getGeneratorMatrixSize() {
		return idMap.size();
	}

	private void addToIdMap(int stateId) {
		Integer id = new Integer(stateId);
		this.idMap.add(id);
	}

	private void addProbabilityOfBasicNode(int nodeId, double probability) {
		Integer id = new Integer(nodeId);
		if (this.idMap.contains(id)) {
			this.probabilities.put(id, new Double(probability));
		}
	}

	/**
	 * Returns the described probabilities of the events in the markov chains
	 * 
	 * @return a map of event id to its probability
	 */
	public Map<Integer, Double> getProbabilitiesForBasicEvents() {
		Map<Integer, Double> result = new HashMap<>();
		for (Entry<Integer, Double> entry : probabilities.entrySet()) {
			if (this.idMap.contains(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
}
