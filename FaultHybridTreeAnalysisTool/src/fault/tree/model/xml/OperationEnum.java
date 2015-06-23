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

	public static boolean isAND(OperationEnum op) {
		return AND.getName().equals(op.getName());
	}
	
	public static boolean isOR(OperationEnum op) {
		return OR.getName().equals(op.getName());
	}
}
