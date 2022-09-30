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

import java.util.*;
import java.util.Map.Entry;

public class Labelling {
    public static final String lab_in = "in";
    public static final String lab_out = "out";
    public static final String lab_undec = "undec";
    private Map<String, String> labelling = null;
    private Set<String> in = null;
    private Set<String> out = null;
    private Set<String> undec = null;

    public Labelling() {
        this.labelling = new HashMap<String, String>();
        this.in = new HashSet<String>();
        this.out = new HashSet<String>();
        this.undec = new HashSet<String>();
    }

    public boolean copyFrom(Labelling another) {
        this.labelling.clear();
        this.in.clear();
        this.out.clear();
        this.undec.clear();

        for (Iterator<String> inarg = another.inargs().iterator(); inarg
                .hasNext(); )
            this.add_label(inarg.next(), Labelling.lab_in);

        for (Iterator<String> outarg = another.outargs().iterator(); outarg
                .hasNext(); )
            this.add_label(outarg.next(), Labelling.lab_out);

        for (Iterator<String> undecarg = another.undecargs()
                .iterator(); undecarg.hasNext(); )
            this.add_label(undecarg.next(), Labelling.lab_undec);

        return true;
    }

    public void add_label(String arg, String l) {
        this.labelling.put(arg, l);
        if (l.compareTo(lab_in) == 0)
            this.in.add(arg);
        else if (l.compareTo(lab_out) == 0)
            this.out.add(arg);
        else if (l.compareTo(lab_undec) == 0)
            this.undec.add(arg);
    }

    public Set<String> inargs() {
        return this.in;
    }

    public Set<String> outargs() {
        return this.out;
    }

    public Set<String> undecargs() {
        return this.undec;
    }

    public Labelling clone() {
        Labelling ret = new Labelling();

        Iterator<Entry<String, String>> it = this.labelling.entrySet()
                .iterator();

        while (it.hasNext()) {
            Entry<String, String> pair = it
                    .next();
            ret.add_label(pair.getKey(), pair.getValue());
        }

        return ret;
    }

    public boolean empty() {
        return this.labelling.isEmpty() && this.in.isEmpty()
                && this.out.isEmpty() && this.undec.isEmpty();
    }

    public HashSet<String> getExtension() {
        return (HashSet<String>) (this.in);
    }

    @Override
    public String toString() {
        String ret = "[";
        Iterator<String> it = this.in.iterator();

        while (it.hasNext()) {
            ret += it.next();
            if (it.hasNext())
                ret += ",";
        }
        ret += "]";
        return ret;

    }

    public boolean equals(Labelling l) {
        return this.inargs().equals(l.inargs())
                && this.outargs().equals(l.outargs())
                && this.undecargs().equals(l.undecargs());
    }
}
