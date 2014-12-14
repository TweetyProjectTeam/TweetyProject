package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * This class implements sorts used for constants
 * @author Bastian Wolf
 */
public class TptpFofSort {

	/**
	 * The name of this sort
	 */
    private String name;

    /**
     * Empty Constructor
     */
    public TptpFofSort(){

    }

    /**
     * 
     * @param name
     */
    public TptpFofSort(String name){
        this.name = name;
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TptpFofSort that = (TptpFofSort) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
