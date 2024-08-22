/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ContradictionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.TautologyAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.Visitor;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * KppADFFormatWriter class
 * @author Mathias Hofer
 * @author Lars Bengel
 */
public class KppADFFormatWriter {

    public static String write(Map<Argument,AcceptanceCondition> acceptanceConditions) {
        StringBuilder s = new StringBuilder();
        Map<Argument, String> nameMap = new HashMap<>();
        int nameCounter = 1; // use an incrementing integer if some argument name is null
        for (Argument arg : acceptanceConditions.keySet()) {
            String name = arg.getName();
            if (name == null) {
                name = String.valueOf(nameCounter++);
            }
            nameMap.put(arg, name);

            s.append("s(");
            s.append(name);
            s.append(").\n");
        }
        AccStringFormatVisitor formatter = new AccStringFormatVisitor(nameMap);
        for (Argument arg: acceptanceConditions.keySet()) {
            AcceptanceCondition acc = acceptanceConditions.get(arg);
            String str = acc.accept(formatter, null);
            s.append("ac(");
            s.append(nameMap.get(arg));
            s.append(",");
            s.append(str);
            s.append(").\n");
        }
        return s.toString();
    }

    private static void write(AbstractDialecticalFramework adf, PrintWriter writer) {
        // 1. write arguments
        Map<Argument, String> nameMap = new HashMap<>();
        int nameCounter = 1; // use an incrementing integer if some argument name is null
        for (Argument arg : adf.getArguments()) {
            String name = arg.getName();
            if (name == null) {
                name = String.valueOf(nameCounter++);
            }
            nameMap.put(arg, name);

            writer.print("s(");
            writer.print(name);
            writer.println(").");
        }
        writer.flush();

        // 2. write acceptance conditions
        AccStringFormatVisitor formatter = new AccStringFormatVisitor(nameMap);
        for (Argument arg : adf.getArguments()) {
            AcceptanceCondition acc = adf.getAcceptanceCondition(arg);
            String str = acc.accept(formatter, null);
            writer.print("ac(");
            writer.print(nameMap.get(arg));
            writer.print(",");
            writer.print(str);
            writer.println(").");
        }
        writer.flush();
    }

    /**
     *
     * The OutputStream is not closed by this method, it is up to the caller.
     *
     *
     * @param adf adf
     * @param out out
     */
    public static void writeTo(AbstractDialecticalFramework adf, OutputStream out) {
        write(adf, new PrintWriter(out));
    }

    /**
     *
     * @param adf adf
     * @param file file
     * @throws FileNotFoundException File Not Found Exception
     */
    public static void writeToFile(AbstractDialecticalFramework adf, File file) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(file)) {
            write(adf, writer);
        }
    }

    private static final class AccStringFormatVisitor implements Visitor<String, Void> {

        private final Map<Argument, String> nameMap;

        /**
         *
         * @param nameMap We cannot just use the getName() method of the arguments,
         *                since null values are allowed. In order to be consistent with
         *                the naming throughout the different accs we need a global name
         *                map.
         */
        public AccStringFormatVisitor(Map<Argument, String> nameMap) {
            this.nameMap = nameMap;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.TautologyAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(TautologyAcceptanceCondition acc, Void topDownData) {
            return "c(v)";
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.ContradictionAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(ContradictionAcceptanceCondition acc, Void topDownData) {
            return "c(f)";
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.ConjunctionAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(ConjunctionAcceptanceCondition acc, Void topDownData) {
            StringBuilder builder = new StringBuilder();
            buildString(new LinkedList<>(acc.getChildren()), "and", builder);
            return builder.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.DisjunctionAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(DisjunctionAcceptanceCondition acc, Void topDownData) {
            StringBuilder builder = new StringBuilder();
            buildString(new LinkedList<>(acc.getChildren()), "or", builder);
            return builder.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.EquivalenceAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(EquivalenceAcceptanceCondition acc, Void topDownData) {
            StringBuilder builder = new StringBuilder();
            buildString(new LinkedList<>(acc.getChildren()), "iff", builder);
            return builder.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.ExclusiveDisjunctionAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(ExclusiveDisjunctionAcceptanceCondition acc, Void topDownData) {
            StringBuilder builder = new StringBuilder();
            buildString(new LinkedList<>(acc.getChildren()), "xor", builder);
            return builder.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.ImplicationAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(ImplicationAcceptanceCondition acc, Void topDownData) {
            StringBuilder builder = new StringBuilder();
            buildString(new LinkedList<>(acc.getChildren()), "imp", builder);
            return builder.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.acc.NegationAcceptanceCondition, java.lang.Object)
         */
        @Override
        public String visit(NegationAcceptanceCondition acc, Void topDownData) {
            StringBuilder builder = new StringBuilder();
            buildString(new LinkedList<>(acc.getChildren()), "neg", builder);
            return builder.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
         * syntax.Argument, java.lang.Object)
         */
        @Override
        public String visit(Argument acc, Void topDownData) {
            return nameMap.get(acc);
        }

        private void buildString(List<AcceptanceCondition> children, String name, StringBuilder builder) {
            builder.append(name);
            builder.append("(");
            AcceptanceCondition first = children.remove(0);
            builder.append(first.accept(this, null));
            if (children.size() > 1) {
                builder.append(",");
                buildString(children, name, builder);
            } else if (children.size() > 0) {
                AcceptanceCondition second = children.remove(0);
                builder.append(",").append(second.accept(this, null));
            }
            builder.append(")");
        }

    }

}
