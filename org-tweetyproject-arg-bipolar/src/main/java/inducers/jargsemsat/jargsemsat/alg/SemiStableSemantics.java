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

import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.DungAF;
import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.Encoding;
import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.Labelling;

import java.util.Iterator;
import java.util.Vector;

public class SemiStableSemantics extends CompleteSemantics {

    public static boolean extensions(Vector<Labelling> extensions, DungAF af, Encoding enc, String arg, boolean firstonly) {
        Vector<Labelling> preferred = new Vector<Labelling>();

        PreferredSemantics.extensions(preferred, af, enc, null, false);

        for (Iterator<Labelling> extIter = preferred.iterator(); extIter.hasNext(); ) {
            Labelling ext = extIter.next();
            boolean toInsert = true;
            for (Iterator<Labelling> intIter = preferred.iterator(); intIter.hasNext(); ) {
                Labelling internal = intIter.next();
                if (!internal.equals(ext) && ext.undecargs().containsAll(internal.undecargs()))
                    toInsert = false;
            }

            if (toInsert) {
                extensions.addElement(ext);

                if (firstonly)
                    return true;
            }
        }

        if (arg != null) {
            for (Iterator<Labelling> it = extensions.iterator(); it.hasNext(); ) {
                if (!it.next().getExtension().contains(arg))
                    return false;
            }
            return true;
        }

        return true;
    }

    public static boolean credulousAcceptance(String arg, DungAF af, Encoding enc) {
        Vector<Labelling> extensions = new Vector<Labelling>();
        SemiStableSemantics.extensions(extensions, af, enc, null, false);

        for (Iterator<Labelling> l = extensions.iterator(); l.hasNext(); ) {
            if (l.next().inargs().contains(arg))
                return true;
        }

        return false;
    }

    public static boolean skepticalAcceptance(String arg, DungAF af, Encoding enc) {
        return extensions(null, af, enc, arg, false);
    }

    public static boolean someExtension(Labelling ret, DungAF af, Encoding enc) {
        Vector<Labelling> res = new Vector<Labelling>();
        boolean val = extensions(res, af, enc, null, true);
        ret.copyFrom(res.firstElement());
        return val;

    }

}
