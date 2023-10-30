package org.tweetyproject.web.services.spring.src.main.java.com.pyargservices;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;
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
  
        //     for (String item : list) { 
        //         System.out.print("  "
        //                          + item 
        //                          + ", "); 
        //     } 
        //     System.out.println("], "); 
        // } 
        // System.out.println("]"); 
        // return "";
        }
        
        DungTheory dungTheory = new DungTheory(af_graph);
        return dungTheory;
    } 

    //    Gson gson = new Gson();
    //     List<List<String>> firstList = gson.fromJson(atttacks, new TypeToken<List<List<Float>>>() {}.getType());
    //     System.out.println(firstList.toString());


    public static String returnLowerCase(String stringInput) {
        return stringInput.toLowerCase();
    }

    public static String[] splitStringInput(String stringInput, String delimiter) {
        return stringInput.split(delimiter);
    }

}
