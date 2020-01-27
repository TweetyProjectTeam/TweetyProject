package net.sf.tweety.arg.bipolar.examples;

import net.sf.tweety.arg.bipolar.syntax.BArgument;
import net.sf.tweety.arg.bipolar.syntax.EvidentialArgumentationFramework;
import net.sf.tweety.arg.bipolar.syntax.SetAttack;
import net.sf.tweety.arg.bipolar.syntax.SetSupport;

public class AddRemoveExample {
    public static void main(String[] args) {
        EvidentialArgumentationFramework et = new EvidentialArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument c = new BArgument("c");
        BArgument d = new BArgument("d");
        BArgument e = new BArgument("e");
        BArgument f = new BArgument("f");
        et.add(a);
        et.add(b);
        et.add(c);
        et.add(d);
        et.add(e);
        et.add(f);

        SetAttack att1 = new SetAttack(b, a);
        SetAttack att2 = new SetAttack(b, c);
        SetAttack att3 = new SetAttack(c, b);
        SetAttack att4 = new SetAttack(c, d);
        SetAttack att5 = new SetAttack(d, f);
        SetAttack att6 = new SetAttack(f, f);
        et.add(att1);
        et.add(att2);
        et.add(att3);
        et.add(att4);
        et.add(att5);
        et.add(att6);

        SetSupport supp1 = new SetSupport(d, e);
        et.add(supp1);

        et.addPrimaFacie(b);
        et.addPrimaFacie(c);
        et.addPrimaFacie(d);
        et.addPrimaFacie(f);

        System.out.println(et.prettyPrint());

        et.remove(a);
        et.remove(b);
        et.remove(c);
        et.remove(d);
        et.remove(e);
        //et.remove(f);

        System.out.println(et.prettyPrint());
    }
}
