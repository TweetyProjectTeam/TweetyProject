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
package org.tweetyproject.web.services;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * Utility class for configuring a Java {@link Logger} with both console and file handlers.
 *
 * <p>This logger is initialized statically and can be accessed through the public
 * {@code logger} field. It is configured to output log messages to both the console
 * and a rolling log file named {@code logs/server.log}.</p>
 *
 * <p>The log file is created within a {@code logs} directory relative to the working
 * directory. If this directory does not exist, it will be created automatically.</p>
 *
 * <p>The logger uses {@link SimpleFormatter} for formatting both console and file outputs,
 * and it is configured at {@code Level.INFO} by default.</p>
 *
 * <p>This utility is intended for use in TweetyProject's web services module.</p>
 * @author Jonas Klein
 * @see Logger
 * @see ConsoleHandler
 * @see FileHandler
 * @see SimpleFormatter
 */
public class LoggerUtil {
   /** The logger instance for this utility class */
    public static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        try {
            // 1) Console handler
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(new SimpleFormatter());

            // 2) File handler — ensure parent dirs exist first
            //    (you probably don't want to write into your src folder;
            //     better to use a dedicated "logs" directory)
            String logPath = "logs/server.log";
            File logFile = new File(logPath);
            File parent = logFile.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    System.err.println("Could not create log directory: " + parent);
                }
            }

            FileHandler fh = new FileHandler(logFile.getAbsolutePath(), true);
            fh.setFormatter(new SimpleFormatter());

            // 3) Attach handlers & set level
            logger.addHandler(ch);
            logger.addHandler(fh);
            logger.setLevel(java.util.logging.Level.INFO);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
