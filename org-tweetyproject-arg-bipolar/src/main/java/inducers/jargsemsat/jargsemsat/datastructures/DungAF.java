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

import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.alg.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DungAF {
    private HashMap<String, Set<String>> children = null;
    private HashMap<String, Set<String>> parents = null;
    private Vector<String> arguments = null;

    public DungAF() {
        this.arguments = new Vector<String>();
        this.children = new HashMap<String, Set<String>>();
        this.parents = new HashMap<String, Set<String>>();

    }

    public DungAF(Collection<String> args, Collection<String[]> att) {
        this.arguments = new Vector<String>();
        this.children = new HashMap<String, Set<String>>();
        this.parents = new HashMap<String, Set<String>>();

        for (String a : args) {
            this.arguments.add(a);
            this.children.put(a, new HashSet<String>());
            this.parents.put(a, new HashSet<String>());
        }

        for (String[] d : att) {
            this.addChild(d[0], d[1]);
            this.addParent(d[1], d[0]);
        }
    }

    public DungAF(Vector<String> args, Vector<Pair<String, String>> input) {
        this.arguments = args;
        this.children = new HashMap<String, Set<String>>();
        this.parents = new HashMap<String, Set<String>>();

        for (String a : args) {
            this.children.put(a, new HashSet<String>());
            this.parents.put(a, new HashSet<String>());
        }

        Iterator<Pair<String, String>> it = input.iterator();
        while (it.hasNext()) {
            Pair<String, String> att = it.next();
            this.addChild(att.getFirst(), att.getSecond());
            this.addParent(att.getSecond(), att.getFirst());
        }
    }

    public Collection<String> getArguments() {
        return this.arguments;
    }

    private void addChild(String n, String c) {
        if (!this.children.containsKey(n))
            this.children.put(n, new HashSet<String>());

        this.children.get(n).add(c);

    }

    private void addParent(String n, String p) {
        if (!this.parents.containsKey(n))
            this.parents.put(n, new HashSet<String>());

        this.parents.get(n).add(p);
    }

    public Set<String> getChildren(String arg) {
        if (this.children.get(arg) == null)
            return new HashSet<String>();
        return this.children.get(arg);
    }

    public Set<String> getParents(String arg) {
        if (this.parents.get(arg) == null)
            return new HashSet<String>();
        return this.parents.get(arg);
    }

    public int InVar(String arg) {
        return this.arguments.indexOf(arg) + 1;
    }

    public int NotInVar(String arg) {
        return -1 * this.InVar(arg);
    }

    public int OutVar(String arg) {
        return this.InVar(arg) + this.arguments.size();
    }

    public int NotOutVar(String arg) {
        return -1 * this.OutVar(arg);
    }

    public int UndecVar(String arg) {
        return this.InVar(arg) + 2 * this.arguments.size();
    }

    public int NotUndecVar(String arg) {
        return -1 * this.UndecVar(arg);
    }

    public String getArgumentByVar(int var) {
        return this.arguments.get(var - 1);
    }

    public String getArgument(int index) {
        return this.arguments.get(index);
    }

    private HashSet<HashSet<String>> labellingsToHashs(Vector<Labelling> exts) {
        HashSet<HashSet<String>> ret = new HashSet<HashSet<String>>();
        for (Labelling l : exts) {
            ret.add(l.getExtension());
        }
        return ret;
    }

    public HashSet<HashSet<String>> getCompleteExts() {
        return this.getCompleteExts(Encoding.defaultEncoding());
    }

    public HashSet<HashSet<String>> getCompleteExts(Encoding enc) {
        Vector<Labelling> exts = new Vector<Labelling>();
        CompleteSemantics.extensions(exts, this, enc, null, false);
        return this.labellingsToHashs(exts);
    }

    public HashSet<String> getGroundedExt() {
        return this.getGroundedExt(Encoding.defaultEncoding());
    }

    public HashSet<String> getGroundedExt(Encoding enc) {
        Vector<Labelling> exts = new Vector<Labelling>();
        GroundedSemantics.extensions(exts, this, enc, null, false);
        return exts.firstElement().getExtension();
    }

    public HashSet<HashSet<String>> getPreferredExts() {
        return this.getPreferredExts(Encoding.defaultEncoding());
    }

    public HashSet<HashSet<String>> getPreferredExts(Encoding enc) {
        Vector<Labelling> exts = new Vector<Labelling>();
        PreferredSemantics.extensions(exts, this, enc, null, false);
        return this.labellingsToHashs(exts);
    }

    public HashSet<HashSet<String>> getSemiStableExts() {
        return this.getSemiStableExts(Encoding.defaultEncoding());
    }

    public HashSet<HashSet<String>> getSemiStableExts(Encoding enc) {
        Vector<Labelling> exts = new Vector<Labelling>();
        SemiStableSemantics.extensions(exts, this, enc, null, false);
        return this.labellingsToHashs(exts);
    }


    public HashSet<HashSet<String>> getStableExts() {
        return this.getStableExts(Encoding.defaultEncoding());
    }

    public HashSet<HashSet<String>> getStableExts(Encoding enc) {
        Vector<Labelling> exts = new Vector<Labelling>();
        StableSemantics.extensions(exts, this, enc, null, false);
        return this.labellingsToHashs(exts);
    }

    public boolean readFile(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            for (String line; (line = br.readLine()) != null; ) {
                int init;
                if ((init = line.indexOf("arg")) != -1) {
                    int open = line.indexOf("(", init + 3);

                    if (open == -1) {
                        br.close();
                        return false;
                    }

                    int close = line.indexOf(")", open + 1);

                    if (close == -1) {
                        br.close();
                        return false;
                    }


                    this.arguments.add((line.substring(open + 1,
                            close)).trim());

                } else if ((init = line.indexOf("att")) != -1) {

                    int open = line.indexOf("(", init + 3);

                    if (open == -1) {
                        br.close();
                        return false;
                    }

                    int comma = line.indexOf(",", open + 1);

                    if (comma == -1) {
                        br.close();
                        return false;
                    }

                    int close = line.indexOf(")", comma + 1);

                    if (close == -1) {
                        br.close();
                        return false;
                    }


                    String source = line.substring(open + 1, comma)
                            .trim();
                    String target = line
                            .substring(comma + 1, close).trim();

                    this.addChild(source, target);
                    this.addParent(target, source);
                }
            }
        } catch (Exception e) {
            br.close();
            e.printStackTrace();
            return false;
        }
        br.close();
        return true;
    }
}
