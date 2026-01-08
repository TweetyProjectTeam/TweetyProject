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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.writer;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Writer for exporting an argumentation framework into LaTeX-code via the argumentation package.
 *
 * @see "https://ctan.org/pkg/argumentation"
 *
 * @author Lars Bengel
 */
public class TikzWriter extends AbstractDungWriter {

    protected Argument[] intToArg;
    protected Map<Argument, Integer> argToInt;

    protected void initialize(DungTheory aaf) {
        // Initialize mapping from Argument to ID and vice versa
        intToArg = new Argument[aaf.size() + 1];
        argToInt = new HashMap<>();
        int v = 1;
        for (Argument argument : aaf) {
            intToArg[v] = argument;
            argToInt.put(argument, v);
            v++;
        }
    }

    protected void findPlanarMapping(DungTheory aaf, int width, int height) {

    }

    protected boolean isSufficientlyPlanar(DungTheory aaf, int width, int height) {
        for (Argument argument : aaf) {
            Collection<Argument> neighboring = new HashSet<>();
            int arg_id = argToInt.get(argument);
            int arg_row = (arg_id-1) % width;
            int arg_col = (arg_id-1) / width;

            // check whether all neighbors in the graph are also neighbors in the grid
            for (int x : Arrays.asList(-1,0,1)) {
                for (int y : Arrays.asList(-1,0,1)) {
                    if (arg_row+x < 0 || arg_row+x >= width || arg_col+y < 0 || arg_col+y >= height) {
                        continue;
                    }
                    int new_arg = (arg_col+y)*width + arg_row+x + 1;
                    neighboring.add(intToArg[new_arg]);
                }
            }

            if (!neighboring.containsAll(aaf.getNeighbors(argument))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void write(DungTheory aaf, File f) throws IOException {
        PrintWriter writer = new PrintWriter(f, StandardCharsets.UTF_8);
        writer.write(this.write(aaf));
        writer.close();
    }

    /**
     * Writes an argumentation framework into a LaTeX-code string. The AF is shaped in a square grid
     * @param aaf an argumentation framework
     * @return String with the LaTeX representation of the AF
     */
    public String write(DungTheory aaf) {
        // square grid
        int width = (int) Math.ceil(Math.sqrt(aaf.size()));
        int height = (int) Math.floor(Math.sqrt(aaf.size()));
        if (width*height < aaf.size()) {
            height++;
            if (width*height < aaf.size()) throw new IllegalArgumentException("Problem");
        }
        return write(aaf, width, height);
    }

    /**
     * Writes an argumentation framework into a LaTeX-code string. The AF is shaped in a grid with the given dimensions
     * @param aaf an argumentation framework
     * @param width width of the AF-grid
     * @param height height of the AF-grid
     * @return String with the LaTeX representation of the AF
     */
    public String write(DungTheory aaf, int width, int height) {
        initialize(aaf);

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("\\begin{af}");
        writer.print(writeArguments(aaf, width, height));
        writer.print(writeSelfAttacks(aaf, width, height));
        writer.print(writeAttacks(aaf));
        writer.println("\\end{af}");
        writer.close();
        return out.toString();
    }

    protected String writeArguments(Collection<Argument> aaf, int width, int height) {
        StringBuilder s = new StringBuilder();
        // Write arguments
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int arg_id = i * width + j + 1;
                if (arg_id > aaf.size()) {
                    continue;
                }
                if (arg_id == 1) {
                    s.append(writeArgument(intToArg[arg_id], arg_id, ""));
                    continue;
                }
                String pos;
                int rel_arg_id;
                if (j == 0) {
                    if (i == 0) {
                        continue;
                    }
                    rel_arg_id = (i - 1) * width + j + 1;
                    pos = "below=of a";
                } else {
                    rel_arg_id = i * width + j - 1 + 1;
                    pos = "right=of a";
                }
                pos = pos + rel_arg_id;
                s.append(writeArgument(intToArg[arg_id], arg_id, pos));
            }
        }
        s.append("\n");
        return s.toString();
    }

    /**
     * Method that converts an argument to the corresponding tikz string to create a tikz-node
     * @param arg some argument
     * @param arg_id the id of the given argument
     * @param pos the relative position of the argument
     * @return string with tikz code
     */
    protected String writeArgument(Argument arg, int arg_id, String pos) {
        return String.format("\t\\argument[%s]{%s}%n", pos, writeArgument(arg));
    }

    /**
     * Writes a string representation in LaTeX-code for a given argument
     * @param arg some argument
     * @return LaTeX-compatible string representation of the argument
     */
    protected String writeArgument(Argument arg) {
        if (arg.getName().contains("_")) {
            // argument name of the form 'a_1, a_2, ...'
            String[] a = arg.getName().split("_");
            return String.format("%s_{%s}",a[0],a[1]);
        } else if (arg.getName().substring(0,1).matches("[a-zA-Z]+") && arg.getName().substring(1).matches("[0-9]+")) {
            // argument name of the form 'a1, a2, ...'
            return String.format("%s_{%s}",arg.getName().charAt(0),arg.getName().substring(1));
        } else {
            // other name formats
            return arg.getName();
        }
    }

    protected String writeAttacks(DungTheory aaf) {
        StringBuilder s = new StringBuilder();
        // Write attacks
        Collection<Attack> attacksAdded = new HashSet<>();
        for (Attack att : aaf.getAttacks()) {
            if (att.getAttacker().equals(att.getAttacked())) {
                continue;
            } else if (aaf.isAttackedBy(att.getAttacker(), att.getAttacked())) {
                if (!attacksAdded.contains(new Attack(att.getAttacked(), att.getAttacker()))) {
                    s.append("\t").append(writeAttack(argToInt.get(att.getAttacker()), argToInt.get(att.getAttacked()), "dual"));
                    attacksAdded.add(att);
                }
            } else {
                s.append("\t").append(writeAttack(argToInt.get(att.getAttacker()), argToInt.get(att.getAttacked())));
                attacksAdded.add(att);
            }
        }
        //s.append("\n");
        return s.toString();
    }

    protected String writeAttack(int argId1, int argId2) {
        return writeAttack(argId1, argId2, "single");
    }

    protected String writeAttack(int argId1, int argId2, String mode) {
        return switch (mode) {
            case "single" -> String.format("\\attack{a%s}{a%s}%n", argId1, argId2);
            case "dual" -> String.format("\\dualattack{a%s}{a%s}%n", argId1, argId2);
            case "bend" -> String.format("\\attack[bend right]{a%s}{a%s}%n", argId1, argId2);
            default -> throw new IllegalArgumentException("Unknown attack mode");
        };
    }

    protected String writeSelfAttacks(DungTheory aaf, int width, int height) {
        StringBuilder s = new StringBuilder();
        // Write self-attacks
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int arg_id = i * width + j + 1;
                if (arg_id > aaf.size()) {
                    continue;
                }
                Argument arg = intToArg[arg_id];
                if (!aaf.isAttackedBy(arg, arg)) {
                    continue;
                }
                if (i == 0) {
                    // argument in first row of grid -> print loop above
                    s.append(String.format(
                            "\t\\selfattack[out=60,in=120,looseness=6]{a%s}",
                            arg_id)
                    ).append("\n");
                } else if (i == height - 1) {
                    // argument in last row of grid -> print below
                    s.append(String.format(
                            "\t\\selfattack[out=240,in=300,looseness=6]{a%s}",
                            arg_id)
                    ).append("\n");
                } else if (j == width - 1) {
                    // argument on last column of grid -> print to the right
                    s.append(String.format(
                            "\t\\selfattack[out=330,in=30,looseness=6]{a%s}",
                            arg_id)
                    ).append("\n");
                } else if (j == 0) {
                    // argument in first column of grid -> print to the left
                    s.append(String.format(
                            "\t\\selfattack[out=150,in=210,looseness=6]{a%s}",
                            arg_id)
                    ).append("\n");
                } else {
                    // somewhere in the center -> print top-right (NOTE: likely to be overlapping with something)
                    s.append(String.format(
                            "\t\\selfattack{a%s}",
                            arg_id)
                    ).append("\n");
                }
            }
        }
        return s.toString();
    }
}
