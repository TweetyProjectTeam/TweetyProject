package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * This class models an atom for the Tptp-Format
 * Each atom represents only one lower-case word.
 * @author Bastian Wolf
 */
public class TptpFofAtom {
	
	/**
	 * This atoms identifier string
	 */
	private String name;
    
	/**
	 * Empty Constructor
	 */
	public TptpFofAtom(){
		
	}
	/**
	 * Constructor with given identifier name
	 * @param name the given identifier name
	 */
	public TptpFofAtom(String name){
		if(name.toLowerCase().equals(name)){
			this.name = name;
		}
		else
			this.name = name.toLowerCase();
	}

	/*
	 * Getter
	 */
	public String getName() {
		return name;
	}

	/*
	 * Setter
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * This method validates, if the given identifier string meets the name conventions for atoms
	 */
	public boolean validate(){
		// TODO implement validation for correct TptpFofAtom-identifier (name)
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}
	
}
