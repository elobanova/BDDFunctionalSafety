package fault.tree.model.xml;

public class EventNode {
	private int id;
	private int level;
	private double probability;

	/**
	 * A method to set the level of the event (either gate or a markov chain
	 * member) as it appears in the tree.
	 * 
	 * @param level
	 *            the integer value of the level in the tree
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * A method to get the level of the event (either gate or a markov chain
	 * member) as it appears in the tree.
	 * 
	 * @return the integer value of the level in the tree
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * A method to set the probability of the event (either gate or a markov
	 * chain member).
	 * 
	 * @param probability
	 *            the double value of the probability of event
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	/**
	 * A method to get the probability of the event (either gate or a markov
	 * chain member).
	 * 
	 * @return the double value of the probability of event
	 */
	public double getProbability() {
		return this.probability;
	}

	/**
	 * A method to get the identifier of the event (either gate or a markov
	 * chain member) stated in the fault tree.
	 * 
	 * @return the identifier of event
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * A method to set the identifier of the event (either gate or a markov
	 * chain member), e.g. during the xml parsing.
	 * 
	 * @param id
	 *            the identifier of event
	 */
	public void setId(int id) {
		this.id = id;
	}
}
