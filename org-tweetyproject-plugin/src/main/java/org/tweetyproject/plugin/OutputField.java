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

/**
 * This class models individual fields used for the PluginOutput.
 * Each field consists of a description and its corresponding value.
 *
 * <p>
 * For example, in the following case:
 * </p>
 *
 * <pre>
 * Query:
 * a + b || !a + !b
 * </pre>
 *
 * <p>
 * "Query:" is the description and "a + b || !a + !b" is the value.
 * </p>
 *
 * <p><b>Authors:</b> Bastian Wolf</p>
 */
public class OutputField {

    /** The description of the field's value. */
    private String description;

    /** The value associated with the field. */
    private String value;

    /**
     * Constructs an {@code OutputField} with an empty value and no description.
     */
    public OutputField() {
        this.value = "";
    }

    /**
     * Constructs an {@code OutputField} with the specified value and no description.
     *
     * @param val the value to be associated with this field.
     */
    public OutputField(String val) {
        this.value = val;
    }

    /**
     * Constructs an {@code OutputField} with the specified description and value.
     *
     * @param description the description of the field's value.
     * @param val the value to be associated with this field.
     */
    public OutputField(String description, String val) {
        this.description = description;
        this.value = val;
    }

    /**
     * Merges the description and value into a single string representation.
     *
     * <p>
     * The resulting string is formatted as:
     * </p>
     *
     * <pre>
     * description:
     * value
     * </pre>
     *
     * @return a string that merges the description and value.
     */
    public String merge() {
        String s = "";
        s += description + ":\n";
        s += value;
        return s;
    }
}
