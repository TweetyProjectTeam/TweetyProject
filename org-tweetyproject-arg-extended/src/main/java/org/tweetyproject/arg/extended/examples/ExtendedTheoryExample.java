package org.tweetyproject.arg.extended.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.extended.reasoner.SimpleExtendedCompleteReasoner;
import org.tweetyproject.arg.extended.syntax.ExtendedTheory;

/**
 * Example usage of Extended Argumentation Frameworks
 *
 * @author Lars Bengel
 */
public class ExtendedTheoryExample {
    public static void main(String[] args) {
        ExtendedTheory theory = new ExtendedTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        theory.add(a,b,c,d,e);

        // standard attacks
        theory.addAttack(a, b);
        theory.addAttack(b, a);
        theory.addAttack(d, c);
        theory.addAttack(c, d);

        // extended attacks
        Attack ab = new Attack(a, b);
        Attack ba = new Attack(b, a);
        Attack cd = new Attack(c, d);
        theory.addAttack(c, ba);
        theory.addAttack(d, ab);
        theory.addAttack(e, cd);

        System.out.println(theory.prettyPrint());

        System.out.println("Complete Extensions: " + new SimpleExtendedCompleteReasoner().getModels(theory));
    }
}
