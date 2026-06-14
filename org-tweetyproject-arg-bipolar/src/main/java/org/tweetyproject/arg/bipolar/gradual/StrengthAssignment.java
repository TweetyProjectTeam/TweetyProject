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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StrengthAssignment extends HashMap<Argument,Number> implements BeliefBase {

    @Override
    public Signature getMinimalSignature() {
        DungSignature sig = new DungSignature();
        for(Argument a: this.keySet())
            sig.add(a);
        return sig;
    }

    public List<Number> get(List<Argument> keys) {
        List<Number> result = new ArrayList<>();
        for (Argument key : keys) {
            result.add(this.get(key));
        }
        return result;
    }
}
