package net.sf.tweety.logics.bpm.syntax;

import java.util.HashSet;
import java.util.Set;

public class Process extends BpmnElement{

	private Set<BpmnNode> nodes = new HashSet<>();
	private Set<Lane> lanes = new HashSet<>();
	private Set<Process> subprocesses = new HashSet<>();
	
	public Process() {
		super();
	}
	
	public void addNode (BpmnNode node) {
		this.nodes.add(node);
	}
	
	public void addLane (Lane lane) {
		this.lanes.add(lane);
	}
	
	public void addLanes (Set<Lane> lanes) {
		this.lanes.addAll(lanes);
	}
	
	public void addSubProcess(Process subprocess) {
		this.subprocesses.add(subprocess);
	}
	
}
