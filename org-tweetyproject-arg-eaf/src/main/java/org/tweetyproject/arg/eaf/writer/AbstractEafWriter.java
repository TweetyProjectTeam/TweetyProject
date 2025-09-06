/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.writer;

import java.io.File;
import java.io.IOException;


import org.tweetyproject.arg.dung.parser.FileFormat;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * Abstract base class for writing EpistemicArgumentationFramework (EAF) instances
 * to different file formats.
 *
 * Subclasses must implement the write method for specific formats.
 * This class also provides a static factory method to retrieve a suitable writer
 * for a given file format.
 *
 * Supported formats include:
 *
 *  - TGF - Trivial Graph Format
 *  - APX - Argumentation Interchange Format (used by ASPARTIX)
 *  - CNF - Conjunctive Normal Form (logic-based encoding)
 *
 *
 *
 * Author: Sandra Hoffmann
 */
public abstract class AbstractEafWriter {

    /**
     * Standard constructor.
     */
    public AbstractEafWriter() {
        super();
    }

    /**
     * Returns an AbstractEafWriter instance for the specified file format.
     *
     * @param f the desired file format
     * @return a writer for the format, or null if the format is not supported
     */
    public static AbstractEafWriter getWriter(FileFormat f) {
        if (f.equals(FileFormat.TGF))
            return new EafTgfWriter();
        if (f.equals(FileFormat.APX))
            return new EafApxWriter();
        if (f.equals(FileFormat.CNF))
            return new EafCnfWriter();
        return null;
    }

    /**
     * Writes the given epistemic argumentation framework to the specified file.
     * If the file already exists, it will be overwritten.
     *
     * @param eaf the epistemic argumentation framework to be written
     * @param f the file to write to
     * @throws IOException if an error occurs during file writing
     */
    public abstract void write(EpistemicArgumentationFramework eaf, File f) throws IOException;
}

