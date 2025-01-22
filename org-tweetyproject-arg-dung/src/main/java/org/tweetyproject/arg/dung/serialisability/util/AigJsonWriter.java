package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.HashMap;
import java.util.Map;

public class AigJsonWriter {

    public static String writeTheory(DungTheory theory) {
        Map<Argument,Integer> argToInt = new HashMap<>();
        StringBuilder s = new StringBuilder();
        s.append("nodes: [\n");
        int id = 0;
        for (Argument argument : theory) {
            argToInt.put(argument, id++);
            AigNode node = new AigNode(argToInt.get(argument), argument.getName());
            s.append(node.toJson()).append(",\n");
        }
        s.append("],\n");

        s.append("links: [\n");
        for (Attack attack : theory.getAttacks()) {
            AigLink link = new AigLink(argToInt.get(attack.getAttacker()), argToInt.get(attack.getAttacked()), "");
            s.append(link.toJson()).append(",\n");
        }
        s.append("]\n");
        return s.toString();
    }
}
