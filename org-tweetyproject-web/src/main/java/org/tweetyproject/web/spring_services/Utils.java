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
package org.tweetyproject.web.spring_services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;

import javafx.util.Pair;
/**
 * *description missing*
 */
/**
 * 
 */
public final class Utils {

    /**
     * *description missing*
     */
    private Utils(){}

    /**
     * *description missing*
     * @param nr_of_arguments *description missing*
     * @param atttacks *description missing*
     * @return *description missing*
     */
    public static DungTheory getDungTheory(int nr_of_arguments, List<List<Integer>> atttacks) {
        Graph<Argument> af_graph = new DefaultGraph<Argument>();
        List<Argument> arguments = new ArrayList<Argument>();
        for (int i = 1; i <= nr_of_arguments; i++){
            arguments.add(new Argument(Integer.toString(i)));
        }

        for (Argument arg: arguments){
            af_graph.add(arg);
        }
        for (List<Integer> list : atttacks) {
            af_graph.add(new DirectedEdge<Argument>(arguments.get(list.get(0) - 1),arguments.get(list.get(1) - 1)));
        }

        DungTheory dungTheory = new DungTheory(af_graph);
        return dungTheory;
    }

    /**
     * *description missing*
     * @param stringInput *description missing*
     * @return *description missing*
     */
    public static String returnLowerCase(String stringInput) {
        return stringInput.toLowerCase();
    }

    /**
     * *description missing*
     * @param stringInput *description missing*
     * @param delimiter *description missing*
     * @return *description missing*
     */
    public static String[] splitStringInput(String stringInput, String delimiter) {
        return stringInput.split(delimiter);
    }

    /**
     * *description missing*
     * @param unit *description missing*
     * @return *description missing*
     */
    public static TimeUnit getTimoutUnit(String unit){
        switch (unit) {
			case "ms":
				System.out.println("Unit of timeout set to ms" );
				return TimeUnit.MILLISECONDS;

			case "sec":
				System.out.println("Unit of timeout set to seconds" );
				return  TimeUnit.SECONDS;
			default:
				System.out.println("Unit of timeout set to seconds" );
				return TimeUnit.SECONDS;
		}
    }

    /**
     * *description missing*
     * @param <T> *description missing*
     * @param future *description missing*
     * @param timeout *description missing*
     * @param unit *description missing*
     * @return *description missing*
     * @throws InterruptedException *description missing*
     * @throws ExecutionException *description missing*
     * @throws TimeoutException *description missing*
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

        return new Pair<T,Long>(result, time);
    }

    /**
     * *description missing*
     * @param user_timeout *description missing*
     * @param server_timeout *description missing*
     * @param unit *description missing*
     * @return *description missing*
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
