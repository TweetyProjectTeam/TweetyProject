package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationState;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DirectedEdge;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.GeneralEdge;

import java.util.*;

public class AigJsonWriter {

    public static String writeTheory(DungTheory theory) {
        return writeTheory(theory, false);
    }

    public static String writeTheory(DungTheory theory, boolean fixedPositions) {
        Map<Argument,AigNode> argToInt = new HashMap<>();
        StringBuilder s = new StringBuilder();
        s.append("nodes: [\n");
        int id = 0;
        int x=0, y=0;
        int defaultDistance=200;
        int offset_x=400;
        int offset_y=400;
        int width = (int) Math.ceil(Math.sqrt(theory.size()));
        //int height = (int) Math.floor(Math.sqrt(af.getNodes().size()));
        for (Argument argument : theory) {
            argToInt.put(argument, new AigNode(id++, argument));
            AigNode node = argToInt.get(argument);
            if (fixedPositions) {
                x = (id-1) % width;
                y = (id-1) / width;
                node.setX(offset_x + x*defaultDistance);
                node.setY(offset_y + y*defaultDistance);
                node.setFixedPositionX(true);
                node.setFixedPositionY(true);
            }
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

    public static String writeSerialisation(DungTheory theory, Semantics semantics) {
        SerialisedExtensionReasoner reasoner = new SerialisedExtensionReasoner(semantics);
        SerialisationGraph graph = reasoner.getSerialisationGraph(theory);
        StringBuilder s = new StringBuilder("nodes: [\n");

        Map<SerialisationState,AigNode> stateToInt = new HashMap<>();
        int id = 0;
        for (SerialisationState state : graph) {
            stateToInt.put(state, new AigNode(id++, state.toString().replace("{", "\\{").replace("}", "\\}"))); // TODO properly write latex format
        }

        Stack<SerialisationState> frontier = new Stack<>();
        SerialisationState root = new SerialisationState(theory, new Extension<>(), reasoner.isTerminal(theory, new Extension<>()));
        frontier.push(root);
        int level = 0;
        int levelDistance = 300;
        while (!frontier.isEmpty()) {
            Collection<SerialisationState> states = new HashSet<>(frontier);
            frontier.clear();
            for (SerialisationState state : states) {
                frontier.addAll(graph.getChildren(state));
                AigNode node = stateToInt.get(state);
                node.setX(levelDistance + (level * levelDistance));
                node.setFixedPositionX(true);
                s.append(node.toJson()).append(",\n");
            }
            level++;
        }
        s.append("],\n");

        s.append("links: [\n");
        for (GeneralEdge<? extends SerialisationState> e : graph.getEdges()) {
            DirectedEdge<SerialisationState> edge = (DirectedEdge<SerialisationState>) e;
            AigLink link = new AigLink(stateToInt.get(edge.getNodeA()), stateToInt.get(edge.getNodeB()), "");
            s.append(link.toJson()).append(",\n");
        }
        s.append("]\n");
        return s.toString();
    }
}
