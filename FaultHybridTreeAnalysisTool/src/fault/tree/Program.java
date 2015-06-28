package fault.tree;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import markov.chains.parser.Connection;
import markov.chains.parser.ConnectionXMLParser;
import markov.chains.seriescomputation.SeriesComputationUtils;
import net.sf.javabdd.BDD;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.xml.sax.SAXException;

import fault.tree.filestreamer.FileStreamer;
import fault.tree.model.xml.GateNode;
import fault.tree.model.xml.parser.FaultTreeXMLParser;
import fault.tree.visualizer.Visualizer;
import faultTreeToBdd.FaultTreeToBdd;

public class Program {
	public static void main(String[] args) {
		File faultTreeInput = FileUtils.toFile(Program.class.getResource("resources/gate14.xml"));
		File connectionsOfMarkovChainsInput = FileUtils.toFile(Program.class.getResource("resources/markovchains.xml"));
		FaultTreeToBdd ftToBDD = new FaultTreeToBdd();
		GateNode faultTree;
		try {
			faultTree = new FaultTreeXMLParser().readFaultTree(faultTreeInput.getAbsolutePath());
			System.out.println("Tree is built.");

			BDD bdd = ftToBDD.faultTreeToBDD(faultTree); 

			System.out.println("BDD is built");
			bdd.printDot();
			ConnectionXMLParser connectionParser = new ConnectionXMLParser();
			List<Connection> chains = connectionParser.parse(connectionsOfMarkovChainsInput.getAbsolutePath(),
					ftToBDD);
			System.out.println("Parsed markov chains");
			double probability = ftToBDD.getFailure(bdd);
			System.out.println("Probability = " + probability);
			System.out.println(ftToBDD.getProbabilitiesForBasicEvents());
			System.out.println(ftToBDD.getGeneratorMatrixSize());
			
			RealMatrix generatorMatrix = SeriesComputationUtils.buildGeneratorMatrix(chains,
					ftToBDD.getGeneratorMatrixSize());
			HashMap<Integer, Integer> markovChains = SeriesComputationUtils.getMarkovChains();
			RealMatrix seriesMatrix = SeriesComputationUtils.calculateTimeSeries(
					ftToBDD.getProbabilitiesForBasicEvents(), generatorMatrix, ConnectionXMLParser.TIME, ConnectionXMLParser.TIME_INTERVAL);
			System.out.println("Calculated the series for the initial time");

			RealVector probabilitiesOfTopEvent = SeriesComputationUtils.calculateProbabilitiesOfTopEvent(
					bdd, seriesMatrix, markovChains);
			System.out.println("Calculated the series for the top event");
			System.out.println(probabilitiesOfTopEvent);
			RealMatrix results = MatrixUtils.createRealMatrix(seriesMatrix.getRowDimension(), seriesMatrix.getColumnDimension() + 1);
			results.setSubMatrix(seriesMatrix.getData(), 0, 0);
			results.setColumnVector(seriesMatrix.getColumnDimension(), probabilitiesOfTopEvent);
			Visualizer.paint(results);

			FileStreamer.ouput(results);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}