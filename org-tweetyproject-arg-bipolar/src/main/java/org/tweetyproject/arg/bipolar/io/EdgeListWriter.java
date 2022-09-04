package org.tweetyproject.arg.bipolar.io;

import org.tweetyproject.arg.bipolar.syntax.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.StringJoiner;


/**
 * The EdgeList writer is a utility class for testing purposes to write networkx compatible EdgeList file format.
 * <p>
 * More details are at @see EdgeListReader.
 *
 * @author Taha Dogan Gunes
 */
public class EdgeListWriter {

    /**
     * Writes a PEAFTheory in EdgeList format
     *
     * @param path       the path in string
     * @param peafTheory the PEAFTheory object
     */
    public static void write(String path, PEAFTheory peafTheory) {
        try {
            FileWriter writer = new FileWriter(path);

            write(peafTheory, writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Writes a PEAFTheory and the query to a EdgeList format given the path
     *
     * @param path       the path in string
     * @param peafTheory the PEAFTheory object
     * @param query      a set of arguments for query
     */
    public static void write(String path, PEAFTheory peafTheory, Set<BArgument> query) {
        try {
            FileWriter writer = new FileWriter(path);

            writer.write("# Query: ");
            boolean first = true;
            for (BArgument argument : query) {
                if (first) {
                    first = false;
                } else {
                    writer.write(" ");
                }
                writer.write(argument.getName());
            }
            writer.write(System.lineSeparator());

            write(peafTheory, writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write EAFTheory to EdgeList file format
     *
     * @param path      the path in string
     * @param eafTheory the EAFTheory object
     */
    public static void write(String path, EAFTheory eafTheory) {
        try {
            FileWriter writer = new FileWriter(path);

            for (Support o : eafTheory.getSupports()) {
                StringBuilder builder = getStringBuilder(o.getSupporter(), o.getSupported());
                if (builder != null) {
                    builder.append(" {'color': 'green'}");
                    writer.write(builder.toString());
                    writer.write(System.lineSeparator());
                }
            }


            for (EAttack o : eafTheory.getAttacks()) {
                StringBuilder builder = getStringBuilder(o.getAttacker(), o.getAttacked());
                if (builder != null) {
                    builder.append(" {'color': 'red'}");
                    writer.write(builder.toString());
                    writer.write(System.lineSeparator());
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Combines arguments and builds the first line
     *
     * @param froms           a set of arguments that originate the link
     * @param tos             a set of arguments that gets targeted
     * @param fromToDelimiter the delimiter for froms argument to tos argument
     * @return StringBuilder object
     */
    public static StringBuilder getStringBuilder(ArgumentSet froms, ArgumentSet tos, String fromToDelimiter) {
        StringBuilder builder = new StringBuilder();
        StringJoiner joiner = new StringJoiner("-");
        for (BArgument from : froms) {
            joiner.add(from.getName());
        }
        if (froms.isEmpty()) {
            joiner.add("E");
            return null;
        }
        builder.append(joiner);
        builder.append(fromToDelimiter);
        joiner = new StringJoiner("-");
        for (BArgument from : tos) {
            joiner.add(from.getName());
        }
        builder.append(joiner);
        return builder;
    }

    /**
     * Utility method for getStringBuilder
     *
     * @param froms a set of arguments that originate the link
     * @param tos   a set of arguments that gets targets
     * @return StringBuilder object
     */
    public static StringBuilder getStringBuilder(ArgumentSet froms, ArgumentSet tos) {
        return EdgeListWriter.getStringBuilder(froms, tos, " ");
    }

    /**
     * Utility method for writing PEAFTheory to a file
     *
     * @param peafTheory PEAFTheory object
     * @param writer     writes to a file
     * @throws IOException when writing to a file fails, this exception is thrown.
     */
    private static void write(PEAFTheory peafTheory, FileWriter writer) throws IOException {
        for (Support o : peafTheory.getSupports()) {
            StringBuilder builder = getStringBuilder(o.getSupporter(), o.getSupported());
            if (builder != null) {
                builder.append(" {'color': 'green', 'weight': ").append(o.getConditionalProbability()).append("  }");
                writer.write(builder.toString());
                writer.write(System.lineSeparator());
            }
        }
        for (Attack o : peafTheory.getAttacks()) {
            StringBuilder builder = getStringBuilder(o.getAttacker(), o.getAttacked());
            if (builder != null) {
                builder.append(" {'color': 'red' }");
                writer.write(builder.toString());
                writer.write(System.lineSeparator());
            }
        }
    }
}
