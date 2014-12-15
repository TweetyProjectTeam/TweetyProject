package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * @author Bastian Wolf
 */
public class FofFormulaFormat {

    private String name;

    private TptpFofRole role;

    private TptpFofFormula formula;

    private String annotation;

    public FofFormulaFormat(String name, TptpFofRole role, TptpFofFormula formula) {
        this.name = name;
        this.role = role;
        this.formula = formula;
    }

    public FofFormulaFormat(String name, TptpFofRole role, TptpFofFormula formula, String annotation) {
        this.name = name;
        this.role = role;
        this.formula = formula;
        this.annotation = annotation;
    }

    public String getName() {
        return name;
    }

    public TptpFofRole getRole() {
        return role;
    }

    public TptpFofFormula getFormula() {
        return formula;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FofFormulaFormat that = (FofFormulaFormat) o;

        if (annotation != null ? !annotation.equals(that.annotation) : that.annotation != null) return false;
        if (formula != null ? !formula.equals(that.formula) : that.formula != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        result = 31 * result + (annotation != null ? annotation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String s = "fof(" + name + "," + role + "," + formula.toString();
        if (annotation !=null)
            s+= annotation;
        s+=")";
        return s;
    }

}
