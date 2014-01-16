package net.sf.tweety.logics.commons.syntax;

/**
 * This class represents terms which are objects identified by a
 * string. Subclasses are Variable and Constant.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 */
public abstract class StringTerm extends TermAdapter<String> {

	/** the value of the term */
	protected String value;

	/** 
	 * Ctor: Creates a string term with the given String as value, uses the
	 * Sort "Thing"
	 * @param value	The value for the string term.
	 */
	public StringTerm(String value) {
		super(value);
	}
	
	/**
	 * Ctor: Create a string term with the given value and sort.
	 * @param value	The value of for the string term.
	 * @param sort	The sort representing the type of the StringTerm.
	 */
	public StringTerm(String value, Sort sort) {
		super(value, sort);
	}
	
	/** 
	 * Copy-Ctor: Creates a deep copy of the StringTerm
	 * @param other	The StringTerm that acts as source for the copy
	 */
	public StringTerm(StringTerm other) {
		super(other.value, other.getSort());
	}
	
	@Override
	public abstract void set(String value);

	@Override
	public String get() {
		return value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
}
