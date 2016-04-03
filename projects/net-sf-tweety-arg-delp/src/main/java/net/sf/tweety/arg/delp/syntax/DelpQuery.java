package net.sf.tweety.arg.delp.syntax;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;

/**
 * A possible way to wrap the classical FOL formula to allow for literals that are just
 * constants or variables in DeLP.
 *
 * @author Linda Briesemeister
 */
public class DelpQuery implements Formula {

    // use this to encode just a variable or constant
    private final static Predicate EMPTY_PREDICATE = new Predicate("EMPTY_PREDICATE", 1);

    private final FOLAtom atom;
    private final FolFormula formula;
    private final boolean isNegation; // make sure not to set this outside of constructors!
    private final Signature signature;


    public DelpQuery(FolFormula f, Signature s) {
        signature = s;
        isNegation = f instanceof Negation;
        if (isNegation) {
            atom = (FOLAtom) f.getFormula();
        } else {
            atom = (FOLAtom) f;
        }
        // TODO: initialize whether we need the empty predicate
        if (atom.getPredicate().getArity() == 0) {
            if (isNegation) {

            } else {

            }
            formula = f;
        } else {
            formula = f;
        }
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    public FolFormula getFormula() {
        return formula;
    }
}
