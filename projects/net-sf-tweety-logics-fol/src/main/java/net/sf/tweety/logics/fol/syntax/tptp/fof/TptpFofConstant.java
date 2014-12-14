package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * A constant consists of an unique identifier name and a sort.
 * @author Bastian Wolf
 */
public class TptpFofConstant {

	/**
	 * The identifier name
	 */
    private String name;

    /**
     * Internal representation with prefix "c_"
     */
    private String tptp_name;
    
    /**
     * The sort of this constant
     */
    private TptpFofSort sort;
    
    /**
     * Constructor
     * @param name the identifier name
     * @param sort the sort of this constant
     */
    public TptpFofConstant(String name, TptpFofSort sort) {
        this.name = name;
        
    	this.tptp_name = "c_" + name;
        
    	this.sort = sort;
    }

    /*
     * Getter
     */
    
    public String getName() {
        return name;
    }
    
    public TptpFofSort getSort(){
    	return sort;
    }
    
    /**
     * Returns this constants tptp name
     */
    public String getTptpName(){
    	return tptp_name;
    }
    
    
}
