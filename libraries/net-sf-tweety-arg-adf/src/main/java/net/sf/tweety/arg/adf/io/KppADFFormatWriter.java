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
package net.sf.tweety.arg.adf.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ContradictionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.TautologyAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.Visitor;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class KppADFFormatWriter {

	public static void writeToFile(AbstractDialecticalFramework adf, File file) throws IOException {
		try (PrintWriter writer = new PrintWriter(file)) {
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
				writer.flush();
			}
		}
	}

	private static final class AccStringFormatVisitor implements Visitor<String, Void> {

		private final Map<Argument, String> nameMap;

		/**
		 * 
		 * @param nameMap
		 *            We cannot just use the getName() method of the arguments,
		 *            since null values are allowed. In order to be consistent
		 *            with the naming throughout the different accs we need a
		 *            global name map.
		 */
		public AccStringFormatVisitor(Map<Argument, String> nameMap) {
			this.nameMap = nameMap;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
		 * net.sf.tweety.arg.adf.syntax.acc.Visitor#visit(net.sf.tweety.arg.adf.
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
