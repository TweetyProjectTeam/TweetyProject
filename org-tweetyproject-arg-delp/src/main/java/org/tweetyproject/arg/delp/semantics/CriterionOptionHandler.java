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
 package org.tweetyproject.arg.delp.semantics;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * Handles command-line options related to {@link ComparisonCriterion} objects.
 * <p>
 * This class extends {@link OptionHandler} to parse and handle command-line options
 * that specify comparison criteria. It is used to process the arguments provided
 * in the command line and convert them into {@link ComparisonCriterion} instances.
 * </p>
 *
 * @param <ComparisonCriterion> the type of the comparison criterion handled by this option handler
 *
 * @author Linda Briesemeister
 */
public final class CriterionOptionHandler extends OptionHandler<ComparisonCriterion> {

    /**
     * Constructs a new {@code CriterionOptionHandler} with the specified command line parser,
     * option definition, and setter.
     *
     * @param parser the {@link CmdLineParser} instance used to parse command-line arguments
     * @param option the {@link OptionDef} that defines the command-line option this handler manages
     * @param setter the {@link Setter} used to set the value of the parsed {@link ComparisonCriterion}
     */
    public CriterionOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super ComparisonCriterion> setter) {
        super(parser, option, setter);
    }

    /**
     * Parses the arguments from the command line and sets the corresponding {@link ComparisonCriterion} value.
     * <p>
     * This method extracts the criterion argument from the parameters and uses the
     * {@link ComparisonCriterion.Factory#create(String)} method to create a {@link ComparisonCriterion} instance.
     * </p>
     *
     * @param parameters the command-line parameters to parse
     * @return the number of arguments consumed by this option handler
     * @throws CmdLineException if there is an error parsing the arguments
     */
    @Override
    public int parseArguments(Parameters parameters) throws CmdLineException {
        setter.addValue(ComparisonCriterion.Factory.create(parameters.getParameter(0)));
        return 1;
    }

    /**
     * Returns the default meta-variable for this option handler.
     * <p>
     * The meta-variable is used in the help message to represent the option value.
     * </p>
     *
     * @return the default meta-variable for this option handler, which is "CRIT"
     */
    @Override
    public String getDefaultMetaVariable() {
        return "CRIT";
    }
}
