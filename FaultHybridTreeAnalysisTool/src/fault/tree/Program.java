package fault.tree;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import fault.tree.model.xml.GateNode;
import fault.tree.model.xml.parser.FaultTreeXMLParser;

public class Program {
	public static void main(String[] args) {
		File faultTreeInput = FileUtils.toFile(Program.class.getResource("resources/data2.xml"));
		GateNode faultTree;
		try {
			faultTree = new FaultTreeXMLParser().readFaultTree(faultTreeInput.getAbsolutePath());
			System.out.println("Tree is built.");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}