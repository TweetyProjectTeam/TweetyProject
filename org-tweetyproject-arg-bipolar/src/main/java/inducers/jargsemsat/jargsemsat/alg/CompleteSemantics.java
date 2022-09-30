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

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.*;

import java.io.*;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

public class CompleteSemantics {

    protected static SATFormulae basicComplete(DungAF af, Encoding encoding) {
        SATFormulae ret = new SATFormulae(af.getArguments().size());

        Iterator<String> it_args = null;
        Iterator<String> it_pred = null;

        for (it_args = af.getArguments().iterator(); it_args.hasNext(); ) {
            String currentArg = it_args.next();
            Set<String> pred = af.getParents(currentArg);

            if (pred.isEmpty()) {
                // c2
                ret.appendOrClause(new OrClause(new int[]{af.InVar(currentArg)}));

                ret.appendOrClause(new OrClause(new int[]{af.NotOutVar(currentArg)}));

                ret.appendOrClause(new OrClause(new int[]{af.NotUndecVar(currentArg)}));

            } else {
                OrClause c3_last_clause = new OrClause();
                OrClause c6_last_clause = new OrClause();
                OrClause c8_or_undec_clause = new OrClause();
                OrClause c7_bigor_clause = new OrClause();
                // c1
                {
                    ret.appendOrClause(new OrClause(
                            new int[]{af.InVar(currentArg), af.OutVar(currentArg), af.UndecVar(currentArg)}));

                    ret.appendOrClause(new OrClause(new int[]{af.NotInVar(currentArg), af.NotOutVar(currentArg)}));

                    ret.appendOrClause(new OrClause(new int[]{af.NotInVar(currentArg), af.NotUndecVar(currentArg)}));

                    ret.appendOrClause(
                            new OrClause(new int[]{af.NotOutVar(currentArg), af.NotUndecVar(currentArg)}));
                }

                // cycle among the predecessors of the node
                for (it_pred = pred.iterator(); it_pred.hasNext(); ) {
                    String parent = it_pred.next();

                    // c4
                    if (encoding.get_C_in_right()) {
                        ret.appendOrClause(new OrClause(new int[]{af.NotInVar(currentArg), af.OutVar(parent)}));
                    }

                    // c3-last
                    if (encoding.get_C_in_left()) {
                        c3_last_clause.appendVariable(af.NotOutVar(parent));
                    }

                    // c5
                    if (encoding.get_C_out_left()) {
                        ret.appendOrClause(new OrClause(new int[]{af.NotInVar(parent), af.OutVar(currentArg)}));
                    }

                    // c6-last
                    if (encoding.get_C_out_right()) {
                        c6_last_clause.appendVariable(af.InVar(parent));
                    }

                    // c8-part
                    if (encoding.get_C_undec_right()) {
                        ret.appendOrClause(new OrClause(new int[]{af.NotUndecVar(currentArg), af.NotInVar(parent)}));

                        c8_or_undec_clause.appendVariable(af.UndecVar(parent));
                    }

                    // c7-end
                    if (encoding.get_C_undec_left()) {
                        c7_bigor_clause.appendVariable(af.InVar(parent));
                    }
                } // end cycle among the predecessors of the node

                // c3-last
                if (encoding.get_C_in_left()) {
                    c3_last_clause.appendVariable(af.InVar(currentArg));
                    ret.appendOrClause(c3_last_clause);
                }

                // c6-last
                if (encoding.get_C_out_right()) {
                    c6_last_clause.appendVariable(af.NotOutVar(currentArg));
                    ret.appendOrClause(c6_last_clause);
                }

                // c8
                if (encoding.get_C_undec_right()) {
                    c8_or_undec_clause.appendVariable(af.NotUndecVar(currentArg));
                    ret.appendOrClause(c8_or_undec_clause);
                }

                // c7
                if (encoding.get_C_undec_left()) {
                    for (it_pred = pred.iterator(); it_pred.hasNext(); ) {
                        String parent = it_pred.next();

                        OrClause to_add = c7_bigor_clause.clone();
                        to_add.appendVariable(af.NotUndecVar(parent));
                        to_add.appendVariable(af.UndecVar(currentArg));
                        ret.appendOrClause(to_add);
                    }
                }

            }
        }

        return ret;
    }

    protected static boolean satlab(SATFormulae cnf, Labelling lab, DungAF af) {

        if (ProboMain.sat == null) {

            ISolver solver = SolverFactory.newDefault();
            solver.newVar(cnf.getNumVars());
            solver.setExpectedNumberOfClauses(cnf.getNumClause());

            for (OrClause c : cnf) {
                try {
                    solver.addClause(new VecInt(c.toArray()));
                } catch (ContradictionException e) {
                    // e.printStackTrace();
                    return false;
                }
            }

            IProblem problem = solver;

            solver.setTimeout(3600);

            try {
                if (problem.isSatisfiable()) {
                    if (lab != null) {
                        int[] results = problem.model();

                        for (int i = 0; i < af.getArguments().size(); i++) {
                            if (results[i] > 0) {
                                lab.add_label(af.getArgument(i), Labelling.lab_in);
                                continue;
                            }
                            if (results[i + af.getArguments().size()] > 0) {
                                lab.add_label(af.getArgument(i), Labelling.lab_out);
                                continue;
                            }
                            if (results[i + 2 * af.getArguments().size()] > 0) {
                                lab.add_label(af.getArgument(i), Labelling.lab_undec);
                                continue;
                            }
                        }
                    }
                    return true;
                }
            } catch (TimeoutException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String satsolver = ProboMain.sat;
            String parameters = "";

            if (satsolver.contains(" ")) {
                parameters = satsolver.substring(satsolver.indexOf(' ') + 1);
                satsolver = satsolver.substring(0, satsolver.indexOf(' '));
            }

            ProcessBuilder builder = new ProcessBuilder(satsolver, parameters);
            builder.redirectErrorStream(true);
            Process solver = null;
            try {
                solver = builder.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.exit(-1);
            }

            OutputStream stdin = solver.getOutputStream();
            InputStream stdout = solver.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

            try {
                writer.write(cnf.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String buf = null;
            int retsat = 0; // neither sat nor unsat by default
            Vector<Integer> lastcompfound = new Vector<Integer>();
            try {
                while ((buf = reader.readLine()) != null) {
                    if (buf.isEmpty())
                        continue;

                    if (buf.charAt(0) == 'c')
                        continue;

                    if (buf.charAt(0) == 's') {
                        if (buf.contains("UNSAT")) {
                            retsat = 20; // unsat
                        } else {
                            retsat = 10; // sat
                        }
                    }

                    if (buf.charAt(0) == 'v') {
                        StringTokenizer st = new StringTokenizer(buf.substring(2));
                        while (st.hasMoreTokens()) {
                            lastcompfound.add(Integer.parseInt(st.nextToken()));
                        }
                    }
                }

                stdin.close();
                stdout.close();
                solver.destroy();

                if (retsat == 0 || (retsat == 10 && lastcompfound.isEmpty())) // ||
                // !fexists(satsolver.c_str()))
                {
                    System.err.println(
                            "Cannot communicate with SAT or SAT error \n" + ProboMain.sat + "\n" + cnf);
                    System.exit(-1);
                }

                if (retsat != 20) {
                    if (lab != null) {
                        for (int i = 0; i < af.getArguments().size(); i++) {
                            if (lastcompfound.get(i) > 0) {
                                lab.add_label(af.getArgument(i), Labelling.lab_in);
                                continue;
                            }
                            if (lastcompfound.get(i + af.getArguments().size()) > 0) {
                                lab.add_label(af.getArgument(i), Labelling.lab_out);
                                continue;
                            }
                            if (lastcompfound.get(i + 2 * af.getArguments().size()) > 0) {
                                lab.add_label(af.getArgument(i), Labelling.lab_undec);
                                continue;
                            }
                        }
                    }
                    return true;
                }
                return false;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return false;
    }

    public static boolean extensions(Vector<Labelling> ret, DungAF af, Encoding enc, String arg, boolean firstonly) {


        SATFormulae pi = basicComplete(af, enc);

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
        pi.appendOrClause(new OrClause(new int[]{af.InVar(arg)}));

        return satlab(pi, null, af);
    }

    public static boolean skepticalAcceptance(String arg, DungAF af, Encoding enc) {

        SATFormulae cnf = basicComplete(af, enc);

        while (true) {
            Labelling res = new Labelling();
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
