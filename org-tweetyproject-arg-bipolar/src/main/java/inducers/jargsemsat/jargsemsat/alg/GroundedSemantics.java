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

public class GroundedSemantics extends CompleteSemantics {

    public static boolean extensions(Vector<Labelling> ret, DungAF af,
                                     Encoding enc, String arg, boolean firstonly) {

        SATFormulae cnf = basicComplete(af, enc);

        Labelling grcand = new Labelling();

        while (true) {
            Labelling res = new Labelling();
            if (!satlab(cnf, res, af)) {
                break;
            }

            grcand = new Labelling();

            grcand = res.clone();

            if (res.undecargs().size() == af.getArguments().size())
                break;

            for (Iterator<String> iter = res.undecargs().iterator(); iter
                    .hasNext(); ) {
                cnf.appendOrClause(
                        new OrClause(new int[]{af.UndecVar(iter.next())}));
            }

            OrClause remaining = new OrClause();
            for (Iterator<String> iter = res.outargs().iterator(); iter
                    .hasNext(); ) {
                remaining.appendVariable(af.UndecVar(iter.next()));
            }
            for (Iterator<String> iter = res.inargs().iterator(); iter
                    .hasNext(); ) {
                remaining.appendVariable(af.UndecVar(iter.next()));
            }
            cnf.appendOrClause(remaining);
        }

        if (arg == null) {
            ret.add(grcand);
        } else {
            return grcand.inargs().contains(arg);
        }
        return true;
    }

    public static boolean credulousAcceptance(String arg, DungAF af,
                                              Encoding enc) {
        return extensions(new Vector<Labelling>(), af, enc, arg, false);
    }

    public static boolean skepticalAcceptance(String arg, DungAF af,
                                              Encoding enc) {
        return extensions(new Vector<Labelling>(), af, enc, arg, false);
    }

    public static boolean someExtension(Labelling ret, DungAF af,
                                        Encoding enc) {
        Vector<Labelling> res = new Vector<Labelling>();
        boolean val = extensions(res, af, enc, null, false);
        ret.copyFrom(res.firstElement());
        return val;
    }

}
