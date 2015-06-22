package fault.tree.model.xml;

public enum OperationEnum {
	OR("OR"), AND("AND");

	private final String name;

	private OperationEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
