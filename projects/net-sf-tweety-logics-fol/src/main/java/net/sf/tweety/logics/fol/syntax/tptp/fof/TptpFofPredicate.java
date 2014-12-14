package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.List;

/**
 * @author Bastian Wolf
 */
public class TptpFofPredicate {

	/**
	 * Atom name
	 */
    private TptpFofAtom name;

	/**
	 * list of arguments
	 */
    private List<TptpFofSort> arguments;

    /**
	 * arity of arguments 
	 */
    private int arity;

    /*
     * Constructor
     */
    public TptpFofPredicate(){

    }

    public TptpFofPredicate(TptpFofAtom name) {
        this.name = name;
    }

    public TptpFofPredicate(TptpFofAtom name, List<TptpFofSort> arguments) {
        this.name = name;
        this.arguments = arguments;
        this.arity = arguments.size();
    }

    /*
     * Getter
     */
    public TptpFofAtom getName() {
        return name;
    }

    public List<TptpFofSort> getArguments() {
        return arguments;
    }

    public int getArity() {
        return arity;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TptpFofPredicate that = (TptpFofPredicate) o;

        if (arity != that.arity) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + arity;
        return result;
    }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	String s = this.name.toString();
    	s+= "(";
    	for(TptpFofSort sort : this.arguments){
    		s+=sort.toString()+ ",";
    	}
    	s = s.substring(0,s.length()-1) + ")";
        return s;

    }
}
