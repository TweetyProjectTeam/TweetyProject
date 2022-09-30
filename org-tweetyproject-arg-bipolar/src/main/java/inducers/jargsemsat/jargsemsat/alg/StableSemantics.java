/**
 * This file is part of jArgSemSAT
 * <p>
 * Copyright (c) 2015 Federico Cerutti <federico.cerutti@acm.org>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.alg;

import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.*;

import java.util.Iterator;
import java.util.Vector;

public class StableSemantics extends CompleteSemantics {
    public static boolean extensions(Vector<Labelling> ret, DungAF af, Encoding enc, String arg, boolean firstonly) {

        SATFormulae pi = basicComplete(af, enc);

        for (Iterator<String> iter = af.getArguments().iterator(); iter.hasNext(); ) {
            pi.appendOrClause(new OrClause(new int[]{af.NotUndecVar(iter.next())}));
        }

        //ret = new Vector<Labelling>();

        Labelling res = new Labelling();

        while (satlab(pi, res, af)) {
            if (arg != null) {
                if (res.inargs().contains(arg) == false)
                    return false;
            } else {
                ret.add(res);
            }

            if (firstonly)
                return true;

            OrClause negation = new OrClause();

            for (Iterator<String> inarg = res.inargs().iterator(); inarg.hasNext(); ) {
                negation.appendVariable(af.NotInVar(inarg.next()));
            }

            for (Iterator<String> outarg = res.outargs().iterator(); outarg.hasNext(); ) {
                negation.appendVariable(af.NotOutVar(outarg.next()));
            }

            for (Iterator<String> undecarg = res.undecargs().iterator(); undecarg.hasNext(); ) {
                negation.appendVariable(af.NotUndecVar(undecarg.next()));
            }
            pi.appendOrClause(negation);
            res = new Labelling();
        }

        return true;
    }

    public static boolean credulousAcceptance(String arg, DungAF af, Encoding enc) {

        SATFormulae pi = basicComplete(af, enc);

        for (Iterator<String> iter = af.getArguments().iterator(); iter.hasNext(); ) {
            pi.appendOrClause(new OrClause(new int[]{af.NotUndecVar(iter.next())}));
        }

        pi.appendOrClause(new OrClause(new int[]{af.InVar(arg)}));

        return satlab(pi, null, af);
    }

    public static boolean skepticalAcceptance(String arg, DungAF af, Encoding enc) {

        SATFormulae cnf = basicComplete(af, enc);

        for (Iterator<String> iter = af.getArguments().iterator(); iter.hasNext(); ) {
            cnf.appendOrClause(new OrClause(new int[]{af.NotUndecVar(iter.next())}));
        }

        cnf.appendOrClause(new OrClause(new int[]{af.OutVar(arg)}));

        Labelling res = new Labelling();
        if (satlab(cnf, res, af))
            return false;

        while (true) {
            res = new Labelling();
            if (!satlab(cnf, res, af)) {
                break;
            }

            if (arg != null && !res.inargs().contains(arg))
                return false;

            if (res.undecargs().size() == af.getArguments().size())
                break;

            for (Iterator<String> iter = res.undecargs().iterator(); iter.hasNext(); ) {
                cnf.appendOrClause(new OrClause(new int[]{af.UndecVar(iter.next())}));
            }

            OrClause remaining = new OrClause();
            for (Iterator<String> iter = res.outargs().iterator(); iter.hasNext(); ) {
                remaining.appendVariable(af.UndecVar(iter.next()));
            }
            for (Iterator<String> iter = res.inargs().iterator(); iter.hasNext(); ) {
                remaining.appendVariable(af.UndecVar(iter.next()));
            }
            cnf.appendOrClause(remaining);
        }
        return true;
    }

    public static boolean someExtension(Labelling ret, DungAF af, Encoding enc) {

        Vector<Labelling> res = new Vector<Labelling>();
        boolean val = extensions(res, af, enc, null, true);
        if (res.isEmpty()) {
            ret = null;
        } else {
            ret.copyFrom(res.firstElement());
        }
        return val;
    }
}
