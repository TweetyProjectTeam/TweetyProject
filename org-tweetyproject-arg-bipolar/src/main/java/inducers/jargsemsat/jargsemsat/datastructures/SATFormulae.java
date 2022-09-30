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

package org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures;

import java.util.Iterator;
import java.util.Vector;

public class SATFormulae implements Iterable<OrClause> {
    private Vector<OrClause> problem = null;
    private int numVars = 0;

    public SATFormulae(int numArgs) {
        this.numVars = numArgs * 3;
        this.problem = new Vector<OrClause>();
    }

    public void appendOrClause(OrClause c) {
        if (!c.isEmpty())
            this.problem.add(c);
    }

    public int getNumVars() {
        return this.numVars;
    }

    public int getNumClause() {
        return this.problem.size();
    }

    private OrClause getClause(int index) {
        return this.problem.get(index);
    }

    @Override
    public Iterator<OrClause> iterator() {
        Iterator<OrClause> it = new Iterator<OrClause>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < getNumClause() && getClause(currentIndex) != null;
            }

            @Override
            public OrClause next() {
                return getClause(currentIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }


    public SATFormulae clone() {
        SATFormulae ret = new SATFormulae(this.numVars);

        for (Iterator<OrClause> it = this.iterator(); it.hasNext(); ) {
            ret.appendOrClause(it.next().clone());
        }

        return ret;
    }

    public String toString() {
        String newline = "\n";
        StringBuilder ret = new StringBuilder();
        ret.append("p cnf ");
        ret.append(this.numVars);
        ret.append(" ");
        ret.append(this.getNumClause());
        ret.append(newline);

        for (int i = 0; i < this.getNumClause(); i++) {
            if (!this.getClause(i).isEmpty()) {
                ret.append(this.getClause(i).toString());
                if (i != this.getNumClause() - 1)
                    ret.append(newline);
            }
        }
        return ret.toString();
    }
}
