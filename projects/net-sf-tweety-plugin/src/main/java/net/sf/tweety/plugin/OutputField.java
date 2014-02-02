package net.sf.tweety.plugin;

/**
 * This class models single fields used for the PluginOutput
 * Each field contains its description and its value, e.g.:
 * 
 * Query:
 * a + b || !a + !b
 * 
 * where "Query:" is the description and "a + b || !a + !b" is the value.
 * 
 * @author Bastian Wolf
 */

public class OutputField {
	
	// this fields value description
	private String description;
	//the field value
	private String value;
	
	public OutputField() {
		value = "";
	}
	
	public OutputField(String val){
		value = val;
	}
	
	public OutputField(String description, String val){
		this.description = description;
		value = val;
	}
	
	public String merge(){
		String s = "";
		s += description + ":\n";
		s += value;
		return s;
	}
}
