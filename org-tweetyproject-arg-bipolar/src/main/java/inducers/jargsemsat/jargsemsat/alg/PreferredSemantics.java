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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.alg;


import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.*;

import java.util.Iterator;
import java.util.Vector;

public class PreferredSemantics extends CompleteSemantics {

    public static boolean extensions(Vector<Labelling> extensions, DungAF af, Encoding enc, String arg, boolean firstonly) {

        //Vector<Labelling> extensions = new Vector<Labelling>();

        if (af.getArguments().isEmpty()) {
            Labelling empty = new Labelling();
            extensions.add(empty);
            return true;
        }

        SATFormulae pi = basicComplete(af, enc);

        OrClause noempty_clause = new OrClause();

        for (Iterator<String> it_args = af.getArguments().iterator(); it_args
                .hasNext(); ) {
            noempty_clause.appendVariable(af.InVar(it_args.next()));
        }
        pi.appendOrClause(noempty_clause);

        SATFormulae cnf = pi.clone();

        do {
            Labelling prefcand = new Labelling();
            SATFormulae cnfdf = cnf.clone();

            while (true) {
                Labelling res = new Labelling();
                if (!satlab(cnfdf, res, af)) {
                    break;
                }

                boolean emptyundec = res.undecargs().isEmpty();

                prefcand = res.clone();

                Iterator<String> iter;
                if (!emptyundec) {
                    for (iter = res.inargs().iterator(); iter.hasNext(); ) {
                        cnfdf.appendOrClause(new OrClause(new int[]
                                {af.InVar(iter.next())}));
                    }
                }

                OrClause remaining = new OrClause();
                for (iter = res.outargs().iterator(); iter.hasNext(); ) {
                    String current = iter.next();
                    if (!emptyundec) {
                        cnfdf.appendOrClause(new OrClause(new int[]
                                {af.OutVar(current)}));
                    }
                    remaining.appendVariable(af.InVar(current));
                }

                OrClause remaining_df = new OrClause();
                for (iter = res.undecargs().iterator(); iter.hasNext(); ) {
                    String current = iter.next();
                    remaining.appendVariable(af.InVar(current));

                    if (!emptyundec) {
                        remaining_df.appendVariable(af.InVar(current));
                    }
                }

                if (!emptyundec) {
                    cnfdf.appendOrClause(remaining_df);
                }

                cnf.appendOrClause(remaining);

                if (emptyundec)
                    break;
            }

            if (prefcand.empty())
                break;

            if (arg != null) {
                if (!prefcand.inargs().contains(arg))
                    return false;
            } else {
                extensions.add(prefcand);
            }

            if (firstonly) {
                if (extensions.isEmpty() && arg == null) {
                    extensions.add(prefcand);
                    //ret = extensions;
                }
                return true;
            }

            if (prefcand.inargs().size() == af.getArguments().size())
                break;

        } while (true);

        if (extensions.isEmpty()) {
            extensions.add(new Labelling());
        }

//		if (ret != null)
//			ret = extensions;

        return true;
    }

    public static boolean credulousAcceptance(String arg, DungAF af, Encoding enc) {
        return CompleteSemantics.credulousAcceptance(arg, af, enc);
    }

    public static boolean skepticalAcceptance(String arg, DungAF af, Encoding enc) {
        if (credulousAcceptance(arg, af, enc) == false)
            return false;
        return extensions(null, af, enc, arg, false);
    }

    public static boolean someExtension(Labelling ret, DungAF af, Encoding enc) {
        Vector<Labelling> res = new Vector<Labelling>();
        boolean val = extensions(res, af, enc, null, true);
        ret.copyFrom(res.firstElement());
        return val;

    }
}
