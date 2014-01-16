package net.sf.tweety.plugin.parameter;

import java.util.ArrayList;

/**
 * This class models a string list command parameter for 
 *  Work-in-Progress, do not use!
 * 
 * @author bwolf
 *
 */


public class StringListCommandParameter extends CommandParameter {
	/**
	 * this parameters given values
	 */
	private String[] value;
	
	/**
	 * constructor with identifier and description
	 * @param id the identifier used to call this parameter
	 * @param des the description of this parameter
	 */
	public StringListCommandParameter(String id, String des) {
		super(id, des);
	}

	// getter
	public String[] getValue(){
		return value;
	}
	// setter
	public void setValue(String[] value){
		this.value = value;
	}
	
	/**
	 * checks each input string for validity
	 */
	@Override
	public boolean isValid(String s) {
		
		return true;
	}

	/**
	 * instantiates unary argument if valid
	 */
	@Override
	public CommandParameter instantiate(String s) {
		if(isValid(s)){
			String[] in = {s};
			StringListCommandParameter newParam = (StringListCommandParameter) this.clone();
			newParam.setValue(in);
			return newParam;
		}
		return null;
	}
	
	/**
	 * instantiates a list of arguments if valid 
	 * @param s
	 */
	public CommandParameter instantiate(String[] s){
		
		ArrayList<String> als = new ArrayList<String>();
		for(int i = 0; i < s.length; i++){
			if(isValid(s[i])){
				als.add(s[i]);
			}
		}
		StringListCommandParameter newParam = (StringListCommandParameter) this.clone();
		newParam.setValue((String[]) als.toArray());
		return newParam;
	}
	
	/**
	 * method to clone this object for instantiation
	 */
	@Override
	public Object clone() {
		return new StringListCommandParameter(this.getIdentifier(),
				this.getDescription());

	}
}
