package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.semantics.ComparisonCriterion;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * @author Linda.Briesemeister
 */
public final class CriterionOptionHandler extends OptionHandler<ComparisonCriterion> {

    public CriterionOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super ComparisonCriterion> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters parameters) throws CmdLineException {
        setter.addValue(ComparisonCriterion.Factory.create(parameters.getParameter(0)));
        return 1;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "CRIT";
    }
}
