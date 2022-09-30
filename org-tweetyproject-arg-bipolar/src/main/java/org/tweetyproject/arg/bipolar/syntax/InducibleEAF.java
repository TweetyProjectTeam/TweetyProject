package org.tweetyproject.arg.bipolar.syntax;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class is an intermediate representation of EAFs that are induced from a PEAF.
 * This is used for making analysis and storing intermediate values while making probabilistic justification
 * of some arguments.
 *
 * @author Taha Dogan Gunes
 */
public class InducibleEAF {
    /**
     * A subset of arguments from PEAF
     */
    public final Set<BArgument> arguments;
    /**
     * A subset of supports from PEAF
     */
    public final Set<Support> supports;
    /**
     * A subset of attacks from PEAF
     */
    public final Set<EAttack> attacks;
    /**
     * A subset of arguments from PEAF that can be used to create a new EAF (connected to InducibleEAF)
     */
    public final Set<BArgument> newArguments;
    /**
     *
     */
    public final double pInside;
    /**
     *
     */
    public final double inducePro;

    /**
     * @param arguments
     * @param supports
     * @param attacks
     * @param newArguments
     * @param pInside
     * @param inducePro
     */
    public InducibleEAF(Set<BArgument> arguments,
                        Set<Support> supports,
                        Set<EAttack> attacks,
                        Set<BArgument> newArguments,
                        double pInside, double inducePro) {

        this.arguments = arguments;
        this.supports = supports;
        this.attacks = attacks;
        this.newArguments = newArguments;
        this.pInside = pInside;
        this.inducePro = inducePro;
    }

    public Set<BArgument> getArguments() {
        return arguments;
    }

    public Set<Support> getSupports() {
        return supports;
    }

    public Set<BArgument> getNewArguments() {
        return newArguments;
    }

    public double getpInside() {
        return pInside;
    }

    public double getInducePro() {
        return Math.exp(inducePro);
    }

    public EAFTheory toNewEAFTheory() {
        EAFTheory eafTheory = new EAFTheory();
        Set<BArgument> arguments = new HashSet<BArgument>();

        arguments.addAll(this.arguments);

        for (Support support : supports) {
            eafTheory.addSupport(support);
        }

        for (EAttack attack : attacks) {
            eafTheory.addAttack(attack);
        }

        List<BArgument> argsSorted = new ArrayList<BArgument>();
        argsSorted.addAll(arguments);
        argsSorted.sort(Comparator.comparing(BArgument::getName));

        for (BArgument argument : argsSorted) {
            eafTheory.addArgument(argument);
        }

        return eafTheory;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("InducibleEAF{");
        builder.append("arguments=[");
        int i = 0;

        List<BArgument> sortedArgs = new ArrayList<BArgument>();
        sortedArgs.addAll(this.arguments);
        sortedArgs.sort(Comparator.comparing(BArgument::getName));

        for (BArgument argument : sortedArgs) {
            builder.append(argument.getName());
            if (i != arguments.size() - 1) {
                builder.append(",");
            }
            i++;
        }
        builder.append("], supports=[");
        i = 0;

        List<Support> sortedSupports = new ArrayList<Support>();
        sortedSupports.addAll(this.supports);
       

        for (Support support : sortedSupports) {
            builder.append(support.toString());
            if (i != supports.size() - 1) {
                builder.append(",");
            }
            i++;
        }

        List<EAttack> sortedAttacks = new ArrayList<EAttack>();
        sortedAttacks.addAll(this.attacks);

        builder.append("], attacks=[");
        i = 0;
        for (EAttack attack : sortedAttacks) {
            builder.append(attack.toString());
            if (i != attacks.size() - 1) {
                builder.append(",");
            }
            i++;
        }
        builder.append("] induce result=");
        builder.append(Math.exp(inducePro));
        builder.append("}");


        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InducibleEAF that = (InducibleEAF) o;
        return arguments.containsAll(that.arguments)
                && supports.containsAll(that.supports)
                && attacks.containsAll(that.attacks)
                && that.arguments.containsAll(arguments)
                && that.supports.containsAll(supports)
                && that.attacks.containsAll(attacks);
    }

    @Override
    public int hashCode() {
        List<String> all = new ArrayList<String>();
        for(BArgument arg : this.arguments) {
        	Integer i = arg.hashCode();
        	all.add(i.toString());
        }
        for(EAttack att : this.attacks) {
        	Integer i = att.hashCode();
        	all.add(i.toString());
        }
        for(Support sup : this.supports) {
        	Integer i = sup.hashCode();
        	all.add(i.toString());
        }

        return Objects.hash(all.toArray());
    }

    public void addAttackLinks() {
        this.attacks.clear();
        Set<BArgument> args = new HashSet<BArgument>();
        args.addAll(this.getArguments());

        for (BArgument arg : args) {
            for (Attack attack : this.attacks) {
            	if(attack.contains(arg)) {
	                BipolarEntity froms = attack.getAttacker();
	                BipolarEntity tos = attack.getAttacked();
	
	                if (args.containsAll((HashSet<BArgument>) froms) && args.contains(tos)) {
	                    this.attacks.add((EAttack) attack);
	                }
            	}
            }
        }
    }
}
