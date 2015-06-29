package markov.chains.parser;

public class Connection {
	private int idFrom;
	private int idTo;
	private double probability;

	/**
	 * A public constructor for a markov chain connection
	 * 
	 * @param idFrom
	 *            an id of the event from which the connection happens
	 * @param idTo
	 *            an id of the event which the connection goes to
	 * @param probability
	 *            the probability as described in the xml
	 */
	public Connection(int idFrom, int idTo, double probability) {
		this.idFrom = idFrom;
		this.idTo = idTo;
		this.probability = probability;
	}

	/**
	 * Gets an id of the event from which the connection happens
	 * 
	 * @return an id of the event from which the connection happens
	 */
	public int getIdFrom() {
		return this.idFrom;
	}

	/**
	 * Gets an id of the event which the connection goes to
	 * 
	 * @return an id of the event which the connection goes to
	 */
	public int getIdTo() {
		return this.idTo;
	}

	/**
	 * Gets the probability as described in the xml containing connections
	 * 
	 * @return the probability as described in the xml
	 */
	public double getProbability() {
		return this.probability;
	}
}
