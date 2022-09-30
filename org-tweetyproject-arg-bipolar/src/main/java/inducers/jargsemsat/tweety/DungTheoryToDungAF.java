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
package org.tweetyproject.arg.peaf.inducers.jargsemsat.tweety;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.DungAF;
import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.Labelling;
import org.tweetyproject.graphs.Edge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


class DungTheoryToDungAF {

    static DungAF fromDungTheory(DungTheory kb) {
        Collection<String> arguments = new Vector<String>();
        Collection<String[]> attacks = new Vector<String[]>();

        Iterator<Argument> it = kb.getNodes().iterator();
        while (it.hasNext()) {
            arguments.add(it.next().getName());
        }

        Iterator<? extends Edge<? extends Argument>> itt = kb.getEdges().iterator();
        while (itt.hasNext()) {
            Edge<? extends Argument> current = itt.next();
            attacks.add(new String[]{current.getNodeA().getName(), current.getNodeB().getName()});
        }

        return new DungAF(arguments, attacks);
    }

    static java.util.Set<Extension> DungAFToExtensions(DungTheory kb, Vector<Labelling> exts) {
        java.util.Set<Extension> ret = new HashSet<Extension>();

        Iterator<Labelling> it = exts.iterator();
        while (it.hasNext()) {
            Extension e = new Extension();
            Iterator<String> arg = it.next().inargs().iterator();

            while (arg.hasNext()) {
                e.add(new Argument(arg.next()));
            }
            ret.add(e);
        }
        return ret;
    }

}
