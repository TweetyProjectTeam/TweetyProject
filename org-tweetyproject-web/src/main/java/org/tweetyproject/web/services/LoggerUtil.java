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

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

/**
 * Utility class for configuring a logger with console and file handlers.
 *
 * The LoggerUtil class provides a static logger instance with both console and file handlers attached.
 * The log messages are formatted using SimpleFormatter. The log level is set to INFO by default but can be
 * customized. Log messages are written to both the console and a log file named "server.log" located in the
 * specified file path.
 *
 * Usage of this utility class ensures that log messages are directed to both the console and a log file
 * simultaneously, allowing for effective logging and monitoring of application activities.
 *
 * Note: The file handler is configured to append to the existing log file (if any) rather than overwriting it.
 *
 * To customize the log level or handle exceptions during configuration, users can modify the static block
 * accordingly. The logger instance is accessible as a public static field, e.g., LoggerUtil.logger.
 *
 * @see Logger
 * @see ConsoleHandler
 * @see FileHandler
 * @see SimpleFormatter
 */

public class LoggerUtil {
    public static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        try {
            // Create a console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());

            // Create a file handler
            FileHandler fileHandler = new FileHandler("TweetyProject/org-tweetyproject-web/src/main/java/org/tweetyproject/web/spring_services/server.log",true);
            fileHandler.setFormatter(new SimpleFormatter());

            // Attach handlers to the logger
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);

            // Set the log level (optional, default is INFO)
            logger.setLevel(java.util.logging.Level.INFO);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

