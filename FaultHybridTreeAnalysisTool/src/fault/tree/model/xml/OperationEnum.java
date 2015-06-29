package fault.tree.model.xml;

public enum OperationEnum {
	OR("OR"), AND("AND");

	private final String name;

	private OperationEnum(String name) {
		this.name = name;
	}

	/**
	 * Returns the string representation of enumerator
	 * 
	 * @return the string representation of enumerator
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Checks if the passed enum is an AND operation.
	 * 
	 * @param op
	 *            the enum to be checked
	 * @return true, if the enum is an AND operation
	 */
	public static boolean isAND(OperationEnum op) {
		return AND.getName().equals(op.getName());
	}

	/**
	 * Checks if the passed enum is an OR operation.
	 * 
	 * @param op
	 *            the enum to be checked
	 * @return true, if the enum is an OR operation
	 */
	public static boolean isOR(OperationEnum op) {
		return OR.getName().equals(op.getName());
	}
}
