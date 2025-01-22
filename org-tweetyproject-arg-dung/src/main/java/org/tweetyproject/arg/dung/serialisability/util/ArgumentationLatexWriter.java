package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.arg.dung.syntax.Argument;

import java.util.Collection;

public abstract class ArgumentationLatexWriter {
    /**
     * Writes a string representation in LaTeX-code for a given argument
     * @param name some argument name
     * @return LaTeX-compatible string representation of the argument
     */
    public static String writeArgument(String name) { // TODO generalise to general latex math string method
        String result;
        if (name.contains("_")) {
            String[] a = name.split("_");
            result = String.format("%s_{%s}",a[0],a[1]);
        } else if (name.substring(0,1).matches("[a-zA-Z]+") && name.substring(1).matches("[0-9]+")) {
            result = String.format("%s_{%s}",name.charAt(0), name.substring(1));
        } else {
            result = name;
        }
        return String.format("$%s$", result);
    }

    public static String writeArgument(Argument arg) {
        return writeArgument(arg.getName());
    }

    /**
     * Writes the given collection of arguments into a LaTeX-compatible string of the form:
     * arg1,...,argn.
     * @param args some collection of arguments
     * @return a string representation of the list of arguments
     */
    public static String writeArgumentList(Collection<Argument> args) {
        if (args.isEmpty()) return "";
        StringBuilder s = new StringBuilder();
        boolean first = true;
        for(Argument a: args) {
            if (first) {
                s.append(writeArgument(a));
                first = false;
            } else s.append(",").append(writeArgument(a));
        }
        return s.toString();
    }

    /**
     * Writes the given collection of arguments into a LaTeX-compatible string of the form:
     * {arg1,...,argn}.
     * @param args some collection of arguments
     * @return a string representation of the collection of arguments
     */
    public static String writeArguments(Collection<Argument> args) {
        return writeExtension(args);
    }

    /**
     * Writes the given collection of arguments into a LaTeX-compatible string of the form:
     * {arg1,...,argn}.
     * @param args some collection of arguments
     * @return a string representation of the collection of arguments
     */
    public static String writeExtension(Collection<Argument> args) {
        if (args.isEmpty()) return "\\emptyset";
        return String.format("\\{%s\\}",writeArgumentList(args));
    }
}
