package fault.tree;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import markov.chains.parser.Connection;
import markov.chains.parser.ConnectionXMLParser;
import markov.chains.seriescomputation.SeriesComputationUtils;
import net.sf.javabdd.BDD;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.xml.sax.SAXException;

import fault.tree.filestreamer.FileStreamer;
import fault.tree.model.xml.GateNode;
import fault.tree.model.xml.parser.FaultTreeXMLParser;
import fault.tree.visualizer.Visualizer;
import faultTreeToBdd.FaultTreeToBdd;

public class Program {
	public static void main(String[] args) {
		File faultTreeInput = FileUtils.toFile(Program.class.getResource("resources/data3.xml"));
		File connectionsOfMarkovChainsInput = FileUtils.toFile(Program.class.getResource("resources/markovchains.xml"));
		FaultTreeToBdd ftToBDD = new FaultTreeToBdd();
		GateNode faultTree;
		try {
			faultTree = new FaultTreeXMLParser().readFaultTree(faultTreeInput.getAbsolutePath());
			System.out.println("Tree is built.");
			BDD bdd = ftToBDD.faultTreeToBDD(faultTree);
			bdd.printDot();
			System.out.println("BDD is built");
			double probability = ftToBDD.getFailure(bdd);
			System.out.println("Probability = " + probability);
			ConnectionXMLParser connectionParser = new ConnectionXMLParser();
			List<Connection> chains = connectionParser.parse(connectionsOfMarkovChainsInput.getAbsolutePath());
			System.out.println("Parsed markov chains");
			System.out.println(ftToBDD.getGeneratorMatrixSize());
			RealMatrix generatorMatrix = SeriesComputationUtils.buildGeneratorMatrix(chains,
					ftToBDD.getGeneratorMatrixSize());
			RealMatrix seriesMatrix = SeriesComputationUtils.calculateTimeSeries(
					ftToBDD.getProbabilitiesForBasicEvents(), generatorMatrix, ConnectionXMLParser.TIME, ConnectionXMLParser.TIME_INTERVAL);
			System.out.println("Calculated the series for the initial time");
			Visualizer.paint(seriesMatrix);
			FileStreamer.ouput(seriesMatrix);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}