package net.sf.tweety.arg.bipolar.examples;

import net.sf.tweety.arg.bipolar.reasoner.necessity.*;
import net.sf.tweety.arg.bipolar.syntax.*;

import java.util.HashSet;
import java.util.Set;

public class NecessityArgumentation {
    public static void main(String[] args) {
        // Example from Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
        NecessityArgumentationFramework nt = new NecessityArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument c = new BArgument("c");
        BArgument d = new BArgument("d");
        BArgument e = new BArgument("e");
        nt.add(a);
        nt.add(b);
        nt.add(c);
        nt.add(d);
        nt.add(e);

        ArgumentSet aS1 = new ArgumentSet();
        aS1.add(b);
        aS1.add(d);
        Attack att1 = new BinaryAttack(b, a);
        Attack att2 = new BinaryAttack(e, a);
        Attack att3 = new BinaryAttack(c, d);
        Support supp1 = new BinarySupport(a, c);
        Support supp2 = new BinarySupport(b, b);
        Support supp3 = new SetSupport(aS1, e);
        nt.add(att1);
        nt.add(att2);
        nt.add(att3);
        nt.add(supp1);
        nt.add(supp2);
        nt.add(supp3);

        System.out.println(nt.prettyPrint());

        System.out.println("Admissible extensions: " + new AdmissibleReasoner().getModels(nt));
        System.out.println("Grounded extensions: " + new GroundedReasoner().getModels(nt));
        System.out.println("Complete extensions: " + new CompleteReasoner().getModels(nt));
        System.out.println("Preferred extensions: " + new PreferredReasoner().getModels(nt));
        System.out.println("Stable extensions: " + new StableReasoner().getModels(nt));
    }
}
