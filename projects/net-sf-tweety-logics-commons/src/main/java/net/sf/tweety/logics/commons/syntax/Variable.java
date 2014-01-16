package net.sf.tweety.logics.commons.syntax;

/**
 * A Variable is a placeholder for Constants in a generic formula of
 * an logic language. Like wife_of(X,Y) represents all wifes X and their 
 * specific husbands Y using the two Variables X,Y for example.
 * It is implemented as a specialized StringTerm which only allows name 
 * with have a upper-case character as first letter.
 * 
 * @author Tim Janus
 */
public class Variable extends StringTerm {
	
	/**
	 * Ctor: Creates a new Variable with the given name and the default
	 * Sort "Thing".
	 * @param name	The name of the Variable has to start with an 
	 * 				upper-case character.
	 */
	public Variable(String name){
		this(name,Sort.THING);
	}
	
	/**
	 * Ctor: Creates a new Variable with the given name and sort.
	 * @param name	The name of the Variable has to start with an 
	 * 				upper-case character.
	 * @param sort	The Sort (type) of the Variable.
	 */
	public Variable(String name, Sort sort){
		super(name, sort);
	}
	
	/**
	 * Copy-Ctor: Creates a deep copy of the given Variable
	 * @param other	The Variable that acts as source for the copy.
	 */
	public Variable(Variable other) {
		super(other);
	}

	@Override
	public void set(String value) {
		if(value == null || value.length() == 0)
			throw new IllegalArgumentException();
		
		char c = value.charAt(0);
		if( !(c > 64 && c <= 90) && c != '_')
			throw new IllegalArgumentException("Variable names start with a upper-case character." +
					"'" + value +"'");
		
		this.value = value;
	}
	
	@Override
	public Variable clone() {
		return new Variable(this);
	}
}
