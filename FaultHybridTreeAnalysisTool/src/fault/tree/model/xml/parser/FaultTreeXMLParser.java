package fault.tree.model.xml.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fault.tree.model.xml.BasicNode;
import fault.tree.model.xml.EventNode;
import fault.tree.model.xml.GateNode;
import fault.tree.model.xml.OperationEnum;

public class FaultTreeXMLParser {
	// private static final String PROBABILITY_ATTRIBUTE_NAME = "probability";
	private static final String NAME_ATTRIBUTE_NAME = "name";
	private static final String OPERATION_ATTRIBUTE_NAME = "operation";
	private static final String LEVEL_ATTRIBUTE_NAME = "level";
	private static final String ID_ATTRIBUTE_NAME = "id";

	private static final String GATE_NODE_NAME = "gate";
	private static final String BASIC_NODE_NAME = "basic";

	public GateNode readFaultTree(String filePath) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream in = new FileInputStream(filePath);
		Document document = builder.parse(in);
		List<GateNode> events = new ArrayList<>();

		NodeList nodes = document.getElementsByTagName(GATE_NODE_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currentNode = nodes.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				GateNode faultTreeEvent = new GateNode();
				Element currentElement = (Element) currentNode;
				faultTreeEvent.setId(Integer.valueOf(currentElement.getAttribute(ID_ATTRIBUTE_NAME)));
				faultTreeEvent.setLevel(Integer.valueOf(currentElement.getAttribute(LEVEL_ATTRIBUTE_NAME)));
				faultTreeEvent
						.setOperation(OperationEnum.valueOf(currentElement.getAttribute(OPERATION_ATTRIBUTE_NAME)));

				// fetch children
				NodeList leaves = currentNode.getChildNodes();
				for (int j = 0; j < leaves.getLength(); j++) {
					Node gateChildNode = leaves.item(j);
					if (gateChildNode.getNodeType() == Node.ELEMENT_NODE) {
						EventNode gateSibling = checkIfChildIsGate(faultTreeEvent, gateChildNode);
						if (gateSibling == null) {
							gateSibling = checkIfChildIsBasic(faultTreeEvent, gateChildNode);
						}
						faultTreeEvent.addChildEvent(gateSibling);
					}
				}

				events.add(faultTreeEvent);
			}
		}

		removeDuplicates(events);
		return match(events);
	}

	private void removeDuplicates(List<GateNode> events) {
		List<GateNode> copy = new ArrayList<>(events);
		for (GateNode event : copy) {
			for (GateNode anotherEvent : copy) {
				if (event.getId() == anotherEvent.getId() && !event.equals(anotherEvent)) {
					events.remove(findGateById(event.getId(), copy));
				}
			}
		}
	}

	private EventNode checkIfChildIsBasic(GateNode faultTreeEvent, Node gateChildNode) {
		if (BASIC_NODE_NAME.equals(gateChildNode.getNodeName())) {
			BasicNode faultTreeBasicEvent = new BasicNode();
			Element basicElement = (Element) gateChildNode;
			faultTreeBasicEvent.setId(Integer.valueOf(basicElement.getAttribute(ID_ATTRIBUTE_NAME)));
			faultTreeBasicEvent.setLevel(Integer.valueOf(basicElement.getAttribute(LEVEL_ATTRIBUTE_NAME)));
			faultTreeBasicEvent.setName(basicElement.getAttribute(NAME_ATTRIBUTE_NAME));
			// faultTreeBasicEvent.setProbability(Double.valueOf(basicElement
			// .getAttribute(PROBABILITY_ATTRIBUTE_NAME)));
			return faultTreeBasicEvent;
		}

		return null;
	}

	private EventNode checkIfChildIsGate(GateNode faultTreeEvent, Node gateChildNode) {
		if (GATE_NODE_NAME.equals(gateChildNode.getNodeName())) {
			GateNode faultTreeGateEvent = new GateNode();
			Element gateElement = (Element) gateChildNode;
			faultTreeGateEvent.setId(Integer.valueOf(gateElement.getAttribute(ID_ATTRIBUTE_NAME)));
			faultTreeGateEvent.setLevel(Integer.valueOf(gateElement.getAttribute(LEVEL_ATTRIBUTE_NAME)));
			faultTreeGateEvent.setOperation(OperationEnum.valueOf(gateElement.getAttribute(OPERATION_ATTRIBUTE_NAME)));
			return faultTreeGateEvent;
		}
		return null;
	}

	/**
	 * Recursively builds the tree with the top gate as a root
	 * 
	 * @param events
	 * @return
	 */
	public GateNode match(List<GateNode> events) {
		List<GateNode> matchedEvents = new ArrayList<>(events);
		GateNode root = matchedEvents.get(0);
		replace(root, matchedEvents);
		return root;
	}

	private void replace(GateNode node, List<GateNode> matchedEvents) {
		List<EventNode> childEvents = node.getChildEvents();
		for (int i = 0; i < childEvents.size(); i++) {
			EventNode child = childEvents.get(i);
			if (child instanceof GateNode) {
				int id = child.getId();
				GateNode n = findGateById(id, matchedEvents);
				node.setChildEvent(i, n);
				replace(n, matchedEvents);
			}

			if (child instanceof BasicNode) {
				continue;
			}
		}
	}

	private GateNode findGateById(int id, List<GateNode> matchedEvents) {
		for (GateNode event : matchedEvents) {
			if (event.getId() == id) {
				return event;
			}
		}
		return null;
	}
}
