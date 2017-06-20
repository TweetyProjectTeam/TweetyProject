package net.sf.tweety.arg.dung.prover.constants;

/**
 * This enum lists all semantics.
 * @author Matthias Thimm
 */
public enum Semantics {
	CF ("conflict-free semantics", "CF"),
	ADM ("admissible semantics", "ADM"),
	CO ("complete semantics", "CO"),
	GR ("grounded semantics", "GR"),
	PR ("preferred semantics", "PR"),
	ST ("stable semantics", "ST"),
	STG ("stage semantics", "STG"),
	SST ("semi-stable semantics", "SST"),
	ID ("ideal semantics", "ID"),
	CF2 ("CF2 semantics", "CF2");
		
	/** The description of the semantics. */
	private String description;
	/** The abbreviation of the semantics. */
	private String abbreviation;
	
	/**
	 * Creates a new semantics.
	 * @param description some description
	 */
	private Semantics(String description, String abbreviation){
		this.description = description;
		this.abbreviation = abbreviation;
	}
	
	/**
	 * Returns the description of the semantics.
	 * @return the description of the semantics.
	 */
	public String description(){
		return this.description;
	}
	
	/**
	 * Returns the abbreviation of the semantics.
	 * @return the abbreviation of the semantics.
	 */
	public String abbreviation(){
		return this.abbreviation;
	}
}
