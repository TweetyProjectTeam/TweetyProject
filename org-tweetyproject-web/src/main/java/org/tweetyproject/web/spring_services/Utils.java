package org.tweetyproject.web.spring_services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;

import javafx.util.Pair;
public final class Utils {

    private Utils(){}

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

    public static String returnLowerCase(String stringInput) {
        return stringInput.toLowerCase();
    }

    public static String[] splitStringInput(String stringInput, String delimiter) {
        return stringInput.split(delimiter);
    }

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

    public static int checkUserTimeout(int user_timeout, int server_timeout, TimeUnit unit){
        if(unit.equals(TimeUnit.MILLISECONDS))
         server_timeout = server_timeout * 1000;

        if (user_timeout > server_timeout){
			user_timeout = server_timeout;
		}
        return user_timeout;
    }



}
