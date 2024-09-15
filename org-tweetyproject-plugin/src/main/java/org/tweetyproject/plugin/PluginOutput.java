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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.plugin;

import java.util.ArrayList;

/**
 * This class provides the output for each plugin to be used in the CLI.
 * Only meant for command line output, not for writing into output files (those are handled within each project
 * as well as parsing input files)
 *
 * @author Bastian Wolf
 *
 */

 public class PluginOutput {

    /**
     * A list of all `OutputField` objects that this output contains.
     */
    private ArrayList<OutputField> fields;

    /**
     * A merged output string that combines the content of all fields.
     */
    private String output;

    /**
     * Constructs an empty `PluginOutput` object.
     *
     * <p>
     * This constructor initializes the `fields` list to an empty `ArrayList` and sets the `output` string
     * to an empty string. It is useful when starting with an empty output that will be populated later.
     * </p>
     */
    public PluginOutput() {
        fields = new ArrayList<OutputField>();
        output = new String();
    }

    /**
     * Constructs a `PluginOutput` object with a predefined list of `OutputField` objects.
     *
     * <p>
     * This constructor initializes the `PluginOutput` with a provided list of `OutputField` objects.
     * The `output` string is not immediately merged; it can be generated later using the `mergeFields` method.
     * </p>
     *
     * @param fields an `ArrayList` of `OutputField` objects to initialize the `PluginOutput`.
     */
    public PluginOutput(ArrayList<OutputField> fields) {
        this.fields = fields;
    }

    /**
     * Adds a new `OutputField` to the list of fields.
     *
     * <p>
     * This method appends a new `OutputField` object to the `fields` list. It allows dynamically adding
     * fields to the `PluginOutput` after it has been created.
     * </p>
     *
     * @param field the `OutputField` object to be added.
     */
    public void addField(OutputField field) {
        fields.add(field);
    }

    /**
     * Adds a new `OutputField` to the list of fields with the specified description and value.
     *
     * <p>
     * This method creates a new `OutputField` object with the given description and value, and then appends
     * it to the `fields` list. It provides a convenient way to add fields without explicitly creating
     * `OutputField` objects.
     * </p>
     *
     * @param description a `String` representing the description of the output field.
     * @param value a `String` representing the value of the output field.
     */
    public void addField(String description, String value) {
        fields.add(new OutputField(description, value));
    }

    /**
     * Merges all fields into a single output string.
     *
     * <p>
     * This method concatenates the merged output of each `OutputField` in the `fields` list into a single
     * `output` string, separated by new lines. This merged string can then be retrieved using the `getOutput` method.
     * </p>
     */
    public void mergeFields() {
        output = "";
        for (OutputField f : fields) {
            output += f.merge() + "\n";
        }
    }

    /**
     * Retrieves the merged output string.
     *
     * <p>
     * This method first calls `mergeFields` to ensure the output string is up-to-date, then returns
     * the merged output string that contains the content of all fields.
     * </p>
     *
     * @return a `String` containing the merged output from all fields.
     */
    public String getOutput() {
        mergeFields();
        return output;
    }

}
