package org.tweetyproject.arg.rankings.examples;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.rankings.postulates.RaSkeptSigmaCompatibility;
import org.tweetyproject.arg.rankings.postulates.RankingPostulate;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.commons.postulates.PostulateEvaluationReport;
import org.tweetyproject.commons.postulates.PostulateEvaluator;
import org.tweetyproject.comparator.NumericalPartialOrder;

public class TestCategorizer {
    public static void main(String[] args) {
        RankingPostulate postulate = new RaSkeptSigmaCompatibility(Semantics.CO);
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
                new CategorizerRankingReasoner());
        evaluator.addPostulate(postulate);
        PostulateEvaluationReport<Argument> report = evaluator.evaluate(30000, true);
        System.out.println(report.prettyPrint());
        System.out.println(report.getNegativeInstances(postulate));

        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        theory.add(a,b,c,d);
        theory.addAttack(a, b);
        theory.addAttack(a, c);
        theory.addAttack(b, d);
        theory.addAttack(c, d);

        NumericalPartialOrder<Argument, DungTheory> ranking = new CategorizerRankingReasoner().getModel(theory);
        System.out.println(ranking);

    }
}
