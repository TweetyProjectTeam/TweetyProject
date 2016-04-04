package net.sf.tweety.arg.delp;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;

/**
 * Wrapping a generic answer from a reasoner in order to allow UNDECIDED
 * in addition to the traditional YES and NO.
 *
 * We ensure backward compatibility by mapping those 3 values to Double
 * values as follows:
 * <ul>
 *     <li><pre>true  <=> YES       <=> Double(0)</pre></li>
 *     <li><pre>false <=  NO        <=> negative number</pre></li>
 *     <li><pre>false <=  UNDECIDED <=> positive number</pre></li>
 * </ul>
 * Note that only <code>true</code> can be reliably mapped to YES, whereas
 * <code>false</code> remains ambiguous.
 *
 *
 * @author Linda.Briesemeister
 */
class DelpAnswer extends Answer {

    interface AnswerType {
        boolean getBooleanAnswer();
        Double getDoubleAnswer();
    }

    enum Type implements AnswerType {
        YES ("The answer is: YES"),
        NO ("The answer is: NO"),
        UNDECIDED ("The answer is: UNDECIDED");

        private final String text;

        Type(String text) { this.text = text; }

        static Type typeForBoolean(boolean booleanAnswer) {
            if (booleanAnswer)
                return YES;
            else
                return NO; // ambiguous, so default is NO
        }

        static Type typeForDouble(Double doubleAnswer) {
            if (doubleAnswer == 0d)
                return YES;
            else if (doubleAnswer < 0d)
                return NO;
            else // double is positive
                return UNDECIDED;
        }


        @Override
        public String toString() { return text; }

        @Override
        public boolean getBooleanAnswer() {
            switch (this) {
                case YES: return true;
                default: return false;
            }
        }

        @Override
        public Double getDoubleAnswer() {
            switch (this) {
                case YES: return 0d;
                case NO: return -1d;
                case UNDECIDED: return 1d;
                default:
                    throw new IllegalStateException("Cannot generate Double answer from "+this);
            }
        }
    }

    private Type currentType;

    DelpAnswer(BeliefBase beliefBase, Formula query) { super(beliefBase, query); }

    /**
     * Obtain current type of this answer.
     * Note that this method could return NULL if the answer is still empty.
     *
     * @return Type of answer as in YES, NO, UNDECIDED
     */
    public Type getType() { return currentType; }

    @Override
    public void appendText(String text) {
        // NO OP! Text is only set from the assignment of Type!
    }

    @Override
    public void setAnswer(boolean answer) {
        currentType = Type.typeForBoolean(answer);
        super.appendText(currentType.toString());
    }

    @Override
    public void setAnswer(Double answer) {
        currentType = Type.typeForDouble(answer);
        super.appendText(currentType.toString());
    }

    @Override
    public boolean getAnswerBoolean() {
        if (currentType == null) return super.getAnswerBoolean();
        return currentType.getBooleanAnswer();
    }

    @Override
    public Double getAnswerDouble() {
        if (currentType == null) return super.getAnswerDouble();
        return currentType.getDoubleAnswer();
    }
}
