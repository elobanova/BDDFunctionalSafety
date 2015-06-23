package faultTreeToBdd;

import java.util.HashMap;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import fault.tree.model.xml.BasicNode;
import fault.tree.model.xml.EventNode;
import fault.tree.model.xml.GateNode;
import fault.tree.model.xml.OperationEnum;

public class FaultTreeToBdd {
	private static final int VAR_NUMBER = 1000;
	private static final int CACHE_SIZE = 1000;
	private static final int NODE_NUMBER = 1000;
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
}
