package fault.tree.model.xml;

import java.util.ArrayList;
import java.util.List;

public class GateNode extends EventNode {

	private OperationEnum operation;
	private List<EventNode> childEvents;

	public GateNode() {
		this.childEvents = new ArrayList<>();
	}

	public OperationEnum getOperation() {
		return this.operation;
	}

	public void setOperation(OperationEnum operation) {
		this.operation = operation;
	}

	public List<EventNode> getChildEvents() {
		return this.childEvents;
	}

	public void setChildEvents(List<EventNode> childEvents) {
		this.childEvents = childEvents;
	}

	public void addChildEvent(EventNode event) {
		this.childEvents.add(event);
	}

	public EventNode getChildEvent(int index) {
		return this.childEvents.get(index);
	}

	public void setChildEvent(int index, EventNode event) {
		this.childEvents.set(index, event);
	}

	public void removeChildEvent(int index) {
		this.childEvents.remove(index);
	}
}