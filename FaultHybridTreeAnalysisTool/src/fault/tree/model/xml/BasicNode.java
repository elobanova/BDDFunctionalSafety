package fault.tree.model.xml;

public class BasicNode extends EventNode {
	private String name;

	/**
	 * A method to obtain the name of the basic node as stated in xml
	 * 
	 * @return the name of the basic node
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * A method to set the name of the basic node (e.g. during xml parsing)
	 * 
	 * @param name
	 *            the name of the basic node to be set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
