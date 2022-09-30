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

public class OrClause {
    private Vector<Integer> clause = null;

    public OrClause() {
        this.clause = new Vector<Integer>();
    }

    public OrClause(int[] vars) {
        this.clause = new Vector<Integer>();
        for (int i = 0; i < vars.length; i++)
            this.clause.add(vars[i]);
    }

    public void appendVariable(int var) {
        this.clause.add(var);
    }

    public OrClause clone() {
        OrClause ret = new OrClause();
        Iterator<Integer> it = this.clause.iterator();
        while (it.hasNext()) {
            ret.appendVariable(it.next());
        }
        return ret;
    }

    public int[] toArray() {
        int[] ret = new int[this.clause.size()];
        int count = 0;
        for (int i : this.clause) ret[count++] = i;
        return ret;
    }

    public boolean isEmpty() {
        return this.clause.isEmpty();
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (this.clause.size() != 0) {
            String sep = " ";
            String endcl = "0";
            for (int i = 0; i < this.clause.size(); i++) {
                ret.append(this.clause.get(i));
                ret.append(sep);
            }
            ret.append(endcl);
        }
        return ret.toString();

    }

}
