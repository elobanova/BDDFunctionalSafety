package fault.tree.filestreamer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import markov.chains.parser.ConnectionXMLParser;

import org.apache.commons.math3.linear.RealMatrix;

public class FileStreamer {

	public static void saveProgramAsCSV(String filename, List<String> fileLines) {
		File file = new File(filename);
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			try {
				output.append("sep=;");
				output.newLine();
				for (String line : fileLines) {
					output.append(line);
					output.newLine();
				}
			} finally {
				output.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void ouput(RealMatrix seriesMatrix) {
		List<String> fileLines = generateLinesOfOutputFile(seriesMatrix);
		saveProgramAsCSV("series.csv", fileLines);
	}

	private static List<String> generateLinesOfOutputFile(RealMatrix seriesMatrix) {
		List<String> fileLines = new ArrayList<>();
		int rowDimension = seriesMatrix.getRowDimension();
		fileLines.add(getHeader(seriesMatrix.getColumnDimension()));
		for (int i = 0; i < rowDimension; i++) {
			int numberOfVariables = seriesMatrix.getRow(i).length;
			StringBuffer lineBuffer = new StringBuffer();
			lineBuffer.append(ConnectionXMLParser.TIME_INTERVAL * i + ";");
			for (int j = 0; j < numberOfVariables; j++) {
				lineBuffer.append(seriesMatrix.getEntry(i, j) + ";");
			}
			fileLines.add(lineBuffer.toString());
		}
		return fileLines;
	}

	private static String getHeader(int columnDimension) {
		StringBuffer header = new StringBuffer();
		header.append("timestamp;");
		for (int i = 0; i < columnDimension; i++) {
			header.append("Variable " + (i + 1) + ";");
		}
		return header.toString();
	}

}
