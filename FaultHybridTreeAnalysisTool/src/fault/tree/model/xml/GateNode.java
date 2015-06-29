package fault.tree.model.xml;

import java.util.ArrayList;
import java.util.List;

public class GateNode extends EventNode {

	private OperationEnum operation;
	private List<EventNode> childEvents;

	/**
	 * A constructor for the gate node of the fault tree.
	 */
	public GateNode() {
		this.childEvents = new ArrayList<>();
	}

	/**
	 * A method to get the operation associated with the gate and stated in the
	 * fault tree.
	 * 
	 * @return the operation enumerator (e.g. AND)
	 */
	public OperationEnum getOperation() {
		return this.operation;
	}

	/**
	 * A method to set the operation associated with the gate, e.g. during the
	 * parsing of the fault tree.
	 * 
	 * @param operation
	 *            the operation enumerator (e.g. AND)
	 */
	public void setOperation(OperationEnum operation) {
		this.operation = operation;
	}

	/**
	 * A method to get the children (leaves) of the gate as described in the
	 * fault tree.
	 * 
	 * @return the list of gate's leaves (which can also be either gates or
	 *         basic events)
	 */
	public List<EventNode> getChildEvents() {
		return this.childEvents;
	}

	/**
	 * A method to set the children (leaves) of the gate, e.g. during the
	 * parsing of the fault tree xml.
	 * 
	 * @param childEvents
	 *            the list of gate's leaves (which can also be either gates or
	 *            basic events)
	 */
	public void setChildEvents(List<EventNode> childEvents) {
		this.childEvents = childEvents;
	}

	/**
	 * Adds the leaf (which can also be either gate or a basic event) to the
	 * gate.
	 * 
	 * @param event
	 *            the leaf node to be added
	 */
	public void addChildEvent(EventNode event) {
		this.childEvents.add(event);
	}

	/**
	 * Gets the leaf (which can also be either gate or a basic event) of the
	 * gate by the leaf's id.
	 * 
	 * @param index
	 *            the id of the leaf
	 */
	public EventNode getChildEvent(int index) {
		return this.childEvents.get(index);
	}

	/**
	 * Sets the leaf (which can also be either gate or a basic event) of the
	 * gate to the place represented by the leaf's id.
	 * 
	 * @param index
	 *            the position where to set the new leaf
	 * @param event
	 *            the new leaf
	 */
	public void setChildEvent(int index, EventNode event) {
		this.childEvents.set(index, event);
	}

	/**
	 * Removes the leaf (which can also be either gate or a basic event) of the
	 * gate by the leaf's id.
	 * 
	 * @param index
	 *            the id of the leaf to be removed
	 */
	public void removeChildEvent(int index) {
		this.childEvents.remove(index);
	}
}