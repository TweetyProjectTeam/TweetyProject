package net.sf.tweety.plugin.parameter;

import java.util.ArrayList;

/**
 * This class expands the CommandParameter with a selection-array containing
 * strings the parameter supports
 * 
 * @author Bastian Wolf
 * 
 */

public class SelectionCommandParameter extends CommandParameter {

	/**
	 * all possible values for this parameter's argument
	 */
	private String[] selections;

	/**
	 * the value each instantiated needs, has to be in selections
	 */
	private String value;

	/**
	 * 
	 * @param id
	 * @param des
	 */
	public SelectionCommandParameter(String id, String des) {
		super(id, des);
	}

	/**
	 * 
	 * @param id
	 * @param des
	 * @param selections
	 */
	public SelectionCommandParameter(String id, String des, String[] selections) {
		super(id, des);
		setSelections(selections);
	}

	/**
	 * returns each possible selection argument
	 * 
	 * @return each possible selection argument
	 */
	public String[] getSelections() {
		return selections;
	}

	/**
	 * sets new selection parameter
	 * 
	 * @param selections
	 */
	public void setSelections(String[] selections) {
		this.selections = selections;
	}

	/**
	 * returns the given instantiation argument value for this parameter
	 * 
	 * @return the given instantiation argument value for this parameter
	 */
	public String getValue() {
		return value;
	}

	/**
	 * sets the instantiated parameter argument value, value has to be one of
	 * the options contained in selections
	 * 
	 * @param value
	 *            the value given as argument value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * checks whether a cli input parameter argument is valid for the called
	 * command parameter
	 */
	@Override
	public boolean isValid(String s) {
		for (int i = 0; i < selections.length; i++) {
			if (selections[i].equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	/**
	 * instantiates a new parameter iff the given value ist valid for this
	 * command parameter
	 */
	@Override
	public CommandParameter instantiate(String s){
		if (this.isValid(s)) {
			SelectionCommandParameter newParameter = (SelectionCommandParameter) this
					.clone();
			newParameter.setValue(s);
			return newParameter;
		}
		return null;
	}

	// TODO: Instantiation with an arraylist of strings instead of strings
	public ArrayList<CommandParameter> instantiate(ArrayList<String> s) {
		ArrayList<CommandParameter> alcp = new ArrayList<CommandParameter>();
		for (int i = 0; i < s.size(); i++) {
			if (this.isValid(s.get(i))) {
				SelectionCommandParameter newParameter = (SelectionCommandParameter) this
						.clone();
				newParameter.setValue(s.remove(0).toString());
				alcp.add(newParameter);
			}
		}
		return alcp;
	}

	@Override
	public Object clone() {
		return new SelectionCommandParameter(this.getIdentifier(),
				this.getDescription(), this.getSelections());

	}

}
