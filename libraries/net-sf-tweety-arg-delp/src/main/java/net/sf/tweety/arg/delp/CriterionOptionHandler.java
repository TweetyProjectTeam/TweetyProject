/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
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
