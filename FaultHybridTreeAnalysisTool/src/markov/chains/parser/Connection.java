package markov.chains.parser;

public class Connection {
	private int idFrom;
	private int idTo;
	private double probability;

	public Connection(int idFrom, int idTo, double probability) {
		this.idFrom = idFrom;
		this.idTo = idTo;
		this.probability = probability;
	}

	public int getIdFrom() {
		return this.idFrom;
	}

	public int getIdTo() {
		return this.idTo;
	}

	public double getProbability() {
		return this.probability;
	}
}
