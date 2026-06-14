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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.gradual;

import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.graphs.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuantitativeBipolarArgumentationFramework extends BipolarArgumentationFramework {
    private Map<Argument,Number> strengths;

    public QuantitativeBipolarArgumentationFramework() {
        super();
        this.strengths = new StrengthAssignment();
    }

    public QuantitativeBipolarArgumentationFramework(BipolarArgumentationFramework graph) {
        super(graph);
        this.strengths = new StrengthAssignment();
        for (Argument arg : this) {
            strengths.put(arg, 1d);
        }
    }

    public Number getStrength(Argument argument) {
        return strengths.get(argument);
    }

    public List<Number> getStrength(List<Argument> arguments) {
        List<Number> result = new ArrayList<>();
        for (Argument argument : arguments) {
            result.add(getStrength(argument));
        }
        return result;
    }

    public Number setStrength(Argument argument, Number value) {
        return this.strengths.put(argument, value);
    }
}
