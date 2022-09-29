package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.BurdenBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.comparator.LatticePartialOrder;
import org.tweetyproject.comparator.NumericalPartialOrder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Example on how to utilize the "AcceptabilityBasedExtensionReasoner".
 *
 * @author Daniel Letkemann
 */
public class AcceptabilityBasedExtensionReasonerExample {
    public static void main(String[] args) throws Exception {

        DungTheory example = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        example.add(a);
        example.add(b);
        example.add(c);
        example.add(d);
        example.add(e);
        example.add(f);
        example.add(g);
        example.addAttack(a, d);
        example.addAttack(a, e);
        example.addAttack(a, f);
        example.addAttack(b, a);
        example.addAttack(c, b);
        example.addAttack(c, g);
        example.addAttack(d, c);
        example.addAttack(d, e);
        example.addAttack(e, c);
        example.addAttack(e, d);
        example.addAttack(f, g);
        example.addAttack(g, f);

        DungTheory figure1 = new DungTheory();
        Argument a1 = new Argument("1");
        Argument a2 = new Argument("2");
        Argument a3 = new Argument("3");
        Argument a4 = new Argument("4");
        Argument a5 = new Argument("5");
        Argument a6 = new Argument("6");
        Argument a7 = new Argument("7");
        figure1.add(a1);
        figure1.add(a2);
        figure1.add(a3);
        figure1.add(a4);
        figure1.add(a5);
        figure1.add(a6);
        figure1.add(a7);
        figure1.addAttack(a1, a2);
        figure1.addAttack(a2, a3);
        figure1.addAttack(a3, a4);
        figure1.addAttack(a3, a5);
        figure1.addAttack(a3, a6);
        figure1.addAttack(a4, a3);
        figure1.addAttack(a4, a6);
        figure1.addAttack(a5, a5);
        figure1.addAttack(a6, a7);
        figure1.addAttack(a7, a6);

        AcceptabilityBasedExtensionReasoner ABER = new AcceptabilityBasedExtensionReasoner();
        Collection<Extension<DungTheory>> prExtensions = new SimplePreferredReasoner().getModels(example);

        NumericalPartialOrder<Argument, DungTheory> catOrder = new CategorizerRankingReasoner().getModel(example);
        System.out.println("ABE_pr,CAT:" + ABER.getModels(prExtensions, catOrder, example));

        LatticePartialOrder<Argument, DungTheory> bbrOrder = new BurdenBasedRankingReasoner().getModel(example);
        System.out.println("ABE_pr,BBR:" + ABER.getModels(prExtensions, bbrOrder, example));
        System.out.println(ABER.getScoreToExtensionsMap(prExtensions, catOrder, example));

        //Powerset example

        Set<Set<Argument>> powerset = new SetTools<Argument>().subsets(figure1);
        Set<Extension<DungTheory>> extensions = new HashSet<>();
        for(Set<Argument> set : powerset){
            Extension<DungTheory> ext = new Extension<>(set);
            extensions.add(ext);
        }
        NumericalPartialOrder<Argument, DungTheory> catOrderFigure1 = new CategorizerRankingReasoner().getModel(figure1);
        System.out.println("ABE_2^n,CAT:" + ABER.getModels(extensions, catOrderFigure1, figure1));
        System.out.println(ABER.getScoreToExtensionsMap(extensions, catOrderFigure1, figure1));
    }
}
