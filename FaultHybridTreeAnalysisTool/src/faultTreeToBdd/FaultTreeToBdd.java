package faultTreeToBdd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	private static final int VAR_NUMBER = 1000;
	private static final int CACHE_SIZE = 1000;
	private static final int NODE_NUMBER = 1000;

	private Set<Integer> idMap = new HashSet<>();
	private BDDFactory bddFactory;
	private HashMap<Integer, BDD> bddMap = new HashMap<Integer, BDD>();

	public BDD faultTreeToBDD(GateNode faultTree) {
		bddFactory = BDDFactory.init(NODE_NUMBER, CACHE_SIZE);
		bddFactory.setVarNum(VAR_NUMBER);
		BDD bdd = buildBdd(faultTree);

		return bdd;

	}

	private BDD buildBdd(EventNode node) {
		BDD bdd = null;
		Integer nodeId = new Integer(node.getId());
		probabilities.put(nodeId, node.getProbability());
		if (node instanceof BasicNode) {
			if (!idMap.contains(nodeId)) {
				idMap.add(nodeId);
			}
		}
		if (bddMap.containsKey(nodeId)) {
			bdd = bddMap.get(nodeId);
		} else if (node instanceof BasicNode) {
			if (!bddMap.containsKey(nodeId)) {
				bdd = bddFactory.ithVar(node.getId());
				bddMap.put(nodeId, bdd);
			} else {
				bdd = bddMap.get(nodeId);
			}
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
		}
		return bdd;
	}

	public double getFailure(BDD bddTree) {
		List<?> list = (List<?>) bddTree.allsat();
		double failureProbability = 0;
		byte[] solutions;
		for (Object o : list) {
			double current = 1.0;
			solutions = (byte[]) o;
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
	
	public int getGeneratorMatrixSize() {
		return idMap.size();
	}
}
