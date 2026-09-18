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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.serialisation;

import java.util.List;

/**
 * The SerialisationPost class represents a data structure for holding information
 * related to a serialisation request sent via HTTP POST.
 */
public class SerialisationPost {

    /** The command type for the serialisation request */
    private String cmd;

    /** The email associated with the serialisation request */
    private String email;

    /** The number of arguments in the serialisation request */
    private int nr_of_arguments;

    /** The attacks information in the serialisation request */
    private List<List<Integer>> attacks;

    /** The extension in the serialisation request */
    private List<Integer> extension;

    /** The selection function specified in the serialisation request */
    private String selectionFunction;

    /** The termination function specified in the serialisation request */
    private String terminationFunction;

    /** The timeout value (in seconds) specified in the serialisation request */
    private int timeout;

    /** The unit timeout value specified in the serialisation request */
    private String unit_timeout;

    /**
     * Gets the unit timeout value specified in the serialisation request.
     *
     * @return The unit timeout value
     */
    public String getUnit_timeout() {
        return unit_timeout;
    }

    /**
     * Sets the unit timeout value in the serialisation request.
     *
     * @param unit_timeout The unit timeout value to be set
     */
    public void setUnit_timeout(String unit_timeout) {
        this.unit_timeout = unit_timeout;
    }

    /**
     * Gets the command type in the serialisation request.
     *
     * @return The command type
     */
    public String getCmd() {
        return this.cmd;
    }

    /**
     * Sets the command type in the serialisation request.
     *
     * @param cmd The command type to be set
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Gets the email associated with the serialisation request.
     *
     * @return The email associated with the request
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email associated with the serialisation request.
     *
     * @param email The email to be set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the number of arguments in the serialisation request.
     *
     * @return The number of arguments
     */
    public int getNr_of_arguments() {
        return this.nr_of_arguments;
    }

    /**
     * Sets the number of arguments in the serialisation request.
     *
     * @param nr_of_arguments The number of arguments to be set
     */
    public void setNr_of_arguments(int nr_of_arguments) {
        this.nr_of_arguments = nr_of_arguments;
    }

    /**
     * Gets the attacks information in the serialisation request.
     *
     * @return The attacks information
     */
    public List<List<Integer>> getAttacks() {
        return this.attacks;
    }

    /**
     * Sets the attacks information in the serialisation request.
     *
     * @param attacks The attacks information to be set
     */
    public void setAttacks(List<List<Integer>> attacks) {
        this.attacks = attacks;
    }

    /**
     * Gets the extension in the serialisation request.
     *
     * @return The extension
     */
    public List<Integer> getExtension() {
        return this.extension;
    }

    /**
     * Sets the extension in the serialisation request.
     *
     * @param extension The extension to be set
     */
    public void setExtension(List<Integer> extension) {
        this.extension = extension;
    }

    /**
     * Gets the selection function specified in the serialisation request.
     *
     * @return The selection function specified
     */
    public String getSelectionFunction() {
        return this.selectionFunction;
    }

    /**
     * Sets the selection function in the serialisation request.
     *
     * @param selectionFunction The selection function to be set
     */
    public void setSelectionFunction(String selectionFunction) {
        this.selectionFunction = selectionFunction;
    }

    /**
     * Gets the termination function specified in the serialisation request.
     *
     * @return The termination function specified
     */
    public String getTerminationFunction() {
        return this.terminationFunction;
    }

    /**
     * Sets the termination function in the serialisation request.
     *
     * @param terminationFunction The termination function to be set
     */
    public void setTerminationFunction(String terminationFunction) {
        this.terminationFunction = terminationFunction;
    }

    /**
     * Gets the timeout value (in seconds) specified in the serialisation request.
     *
     * @return The timeout value
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * Sets the timeout value (in seconds) in the serialisation request.
     *
     * @param timeout The timeout value to be set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}