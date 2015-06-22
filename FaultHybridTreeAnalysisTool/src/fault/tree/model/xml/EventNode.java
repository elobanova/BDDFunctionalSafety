package fault.tree.model.xml;

public class EventNode {
	private int id;
	private int level;
	private double probability;

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public double getProbability() {
		return this.probability;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
