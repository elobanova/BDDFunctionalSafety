package markov.chains.parser;

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

public class ConnectionXMLParser {
	private static final String PROBABILITY_ATTRIBUTE_NAME = "probability";
	private static final String ID_TO_ATTRIBUTE_NAME = "to";
	private static final String ID_FROM_ATTRIBUTE_NAME = "from";
	private static final String MISSION_TIME_ATTRIBUTE_NAME = "missionTime";
	private static final String INTERVAL_ATTRIBUTE_NAME = "interval";

	private static final String CONNECTION_NODE_NAME = "connection";
	public static double TIME = 40.0;
	public static double TIME_INTERVAL = 0.5;

	public List<Connection> parse(String filePath) throws ParserConfigurationException, SAXException, IOException {
		List<Connection> connections = new ArrayList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream in = new FileInputStream(filePath);
		Document document = builder.parse(in);
		Element documentElement = document.getDocumentElement();
		TIME_INTERVAL = Double.valueOf(documentElement.getAttribute(INTERVAL_ATTRIBUTE_NAME));
		TIME = Double.valueOf(documentElement.getAttribute(MISSION_TIME_ATTRIBUTE_NAME));

		NodeList nodes = document.getElementsByTagName(CONNECTION_NODE_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currentNode = nodes.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				Element currentElement = (Element) currentNode;
				int fromId = Integer.valueOf(currentElement.getAttribute(ID_FROM_ATTRIBUTE_NAME));
				int toId = Integer.valueOf(currentElement.getAttribute(ID_TO_ATTRIBUTE_NAME));
				double probability = Double.valueOf(currentElement.getAttribute(PROBABILITY_ATTRIBUTE_NAME));
				Connection conn = new Connection(fromId, toId, probability);
				connections.add(conn);
			}
		}
		return connections;
	}
}
