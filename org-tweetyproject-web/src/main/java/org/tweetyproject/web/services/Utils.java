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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import javafx.util.Pair;

/**
 * Utility class providing various helper methods for common operations.
 * This class includes methods for creating DungTheory from attack relations,
 * manipulating strings, handling timeouts, and converting time units.
 *
 */

public final class Utils {

    /**
     * Default constructor
     */
    private Utils(){}


    /**
     * Converts the input string to lowercase.
     *
     * @param stringInput The input string to be converted.
     * @return The input string in lowercase.
     */
    public static String returnLowerCase(String stringInput) {
        return stringInput.toLowerCase();
    }



    /**
     * Splits the input string using the specified delimiter.
     *
     * @param stringInput The input string to be split.
     * @param delimiter   The delimiter used for splitting the string.
     * @return An array of strings resulting from the split operation.
     */
    public static String[] splitStringInput(String stringInput, String delimiter) {
        return stringInput.split(delimiter);
    }

    /**
     * Gets the corresponding TimeUnit based on the provided string representation.
     *
     * @param unit The string representation of the time unit ("ms" for milliseconds, "sec" for seconds).
     * @return The TimeUnit corresponding to the input string.
     */
    public static TimeUnit getTimeoutUnit(String unit){
        return switch (unit) {
            case "ms" -> {
                System.out.println("Unit of timeout set to ms");
                yield TimeUnit.MILLISECONDS;
            }
            default -> {
                System.out.println("Unit of timeout set to seconds");
                yield TimeUnit.SECONDS;
            }
        };
    }

     /**
     * Runs a service with a specified timeout and returns the result along with the execution time.
     *
     * @param future  The Future representing the result of an asynchronous computation.
     * @param timeout The timeout duration.
     * @param unit    The TimeUnit of the timeout.
     * @param <T>     The type of the result.
     * @return A Pair containing the result and the execution time.
     * @throws InterruptedException If the execution is interrupted.
     * @throws ExecutionException   If the computation threw an exception.
     * @throws TimeoutException     If the computation did not complete before the timeout.
     */
    public static <T> Pair<T,Long> runServicesWithTimeout(Future<T> future, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException{
        long millis = System.currentTimeMillis();
		T result = future.get(timeout, unit);
		millis = System.currentTimeMillis() - millis;
		long time = millis;
		if (unit.equals(TimeUnit.SECONDS)){
            System.out.println("converting millis to seconds");
            time = TimeUnit.MILLISECONDS.toSeconds(millis);
            System.out.println(time);
		}

        return new Pair<>(result, time);
    }

    /**
     * Checks and adjusts the user-defined timeout based on the server timeout and time unit.
     *
     * @param user_timeout   The user-defined timeout.
     * @param server_timeout The server-defined timeout.
     * @param unit           The TimeUnit of the timeouts.
     * @return The adjusted user-defined timeout.
     */
    public static int checkUserTimeout(int user_timeout, int server_timeout, TimeUnit unit){
        if(unit.equals(TimeUnit.MILLISECONDS))
         server_timeout = server_timeout * 1000;

        if (user_timeout > server_timeout){
			user_timeout = server_timeout;
		}
        return user_timeout;
    }


}
