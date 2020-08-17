package net.sf.tweety.logics.bpm.parser;

public class BufferedBpmnEdge {
	
	private String id; 
	private String sourceRef;
	private String targetRef;
	private String flowType;
	
	public BufferedBpmnEdge() {
	}

	public String getId() {
		return id;
	}
	
	public String getSourceRef() {
		return sourceRef;
	}
	
	public String getTargetRef() {
		return targetRef;
	}
	
	public String getFlowType() {
		return flowType;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}
	
	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}
	
}
