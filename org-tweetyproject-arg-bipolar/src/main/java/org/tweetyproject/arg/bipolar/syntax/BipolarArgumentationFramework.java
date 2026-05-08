/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.bipolar.syntax;

import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.graphs.Graph;

import java.util.*;

/**
 * This class implements a bipolar abstract argumentation theory with support and attack relation.
 *
 * @see "Cayrol C., A. Cohen, and M.-C. Lagasquie-Schiex. 'Higher-order interactions (bipolar or not) in abstract argumentation: A state of the art'. Handbook of Formal Argumentation Vol. 2, 2021."
 * @see "Cohen A., S. Gottifredi, A. J. Garcia, and G. R. Simari. 'A survey of different approaches to support in argumentation systems'. Knowledge Engineering Review, 29(5):513–550, 2014."
 *
 * @author Lars Bengel
 */
public class BipolarArgumentationFramework extends DungTheory {

    /**
     * explicit listing of direct supporters and supported (for efficiency reasons)
     */
    protected Map<Argument, Collection<Argument>> supportParents = new HashMap<>();
    protected Map<Argument, Collection<Argument>> supportChildren = new HashMap<>();

    /**
     * Default constructor; initializes empty sets of arguments, attacks and supports
     */
    public BipolarArgumentationFramework() {
        super();
    }

    /**
     * Determines whether 'args' is coherent, i.e., whether there exists no argument that is both (indirectly) supported and attacked (either supported or indirectly) by 'args'
     *
     * @param args some set of arguments
     * @return "true" iff 'args' is coherent
     */
    public boolean isCoherent(Collection<Argument> args){
        Collection<Argument> supported = new HashSet<>(args);
        supported.addAll(getSupported(args));

        DungTheory theory = getAssociatedTheory();

        Collection<Argument> intersect = new HashSet<>(theory.getAttacked(args));
        intersect.retainAll(supported);
        return intersect.isEmpty();
    }

    /**
     * Determines whether 'args' is strongly conflict-free, i.e., whether 'args' is conflict-free wrt. supported and indirect attacks
     *
     * @param args some set of arguments
     * @return "true" iff 'args' is strongly conflict-free
     */
    public boolean isStronglyConflictFree(Collection<Argument> args) {
        DungTheory theory = getAssociatedTheory();
        return theory.isConflictFree(args);
    }

    /**
     * Determines whether 'ext' strongly defends 'arg', i.e., whether 'arg' is defended under consideration of supported and indirect attacks
     * @param ext some set of arguments
     * @param arg some argument
     * @return "true" iff 'arg' is strongly defended by 'ext'
     */
    public boolean isStronglyDefendedBy(Collection<Argument> ext, Argument arg) {
        DungTheory theory = getAssociatedTheory();
        Collection<Argument> attackers = theory.getAttackers(arg);
        attackers.removeAll(theory.getAttacked(ext));
        return attackers.isEmpty();
    }

    /**
     * Determines whether 'args' is coherent admissible, i.e., whether 'args' is coherent and strongly defends all its arguments
     * 
     * @param args some set of arguments
     * @return "true" iff 'args' is coherent admissible
     */
    public boolean isCoherentAdmissible(Collection<Argument> args) {
        if (!isCoherent(args)) return false;
        for (Argument arg: args) {
            if (!isStronglyDefendedBy(args, arg)) return false;
        }
        return true;
    }

    /**
     * Determines whether 'args' is a coalition, i.e., whether 'args' is a maximal conflict-free set of arguments that is connected via the support relation.
     *
     * @param args some set of arguments
     * @return "true" if 'args' is a coalition of this BAF
     */
    public boolean isCoalition(Collection<Argument> args) {
        return getCoalitions().contains(args);
    }

    /**
     * Computes the set of all coalition of this BAF
     * A coalition is a maximal conflict-free set of arguments that is connected via the support relation.
     *
     * @return the set of coalitions
     */
    public Collection<Collection<Argument>> getCoalitions() {
        Collection<Collection<Argument>> candidates = new HashSet<>();
        for (Argument arg: this) {
            Extension<DungTheory> ext = new Extension<>();
            ext.add(arg);
            candidates.add(ext);
        }
        DungTheory supp_theory = new DungTheory();
        supp_theory.addAll(this.getNodes());
        for (Support supp: this.getSupports()) {
            supp_theory.addAttack(supp.getSupporter(), supp.getSupported());
        }
        Collection<Graph<Argument>> components = supp_theory.getComponents();
        Collection<Extension<DungTheory>> cfSets = new SimpleConflictFreeReasoner().getModels(this);
        for (Graph<Argument> component : components) {
            for (Collection<Argument> candidate : new SetTools<Argument>().subsets(component.getNodes())) {
                if (candidate.isEmpty()) continue;
                DungTheory candidate_theory = (DungTheory) supp_theory.getRestriction(candidate);
                if (candidate_theory.getComponents().size() == 1) {
                    if (cfSets.contains(new Extension<DungTheory>(candidate))) {
                        candidates.add(new Extension<DungTheory>(candidate));
                    }
                }
            }
        }

        Collection<Collection<Argument>> coalitions = new HashSet<>();
        boolean maximal;
        for(Collection<Argument> e1: candidates){
            maximal = true;
            for(Collection<Argument> e2: candidates)
                if(e1 != e2 && e2.containsAll(e1)){
                    maximal = false;
                    break;
                }
            if(maximal)
                coalitions.add(e1);
        }
        return coalitions;
    }

    /**
     * Computes the coalition graph for this BAF.
     * A coalition is a maximal conflict-free set of arguments that is connected via the support relation.
     * There is an attack between two coalitions iff there is an attack between members of each coalition.
     *
     * @return the coalition graph for this BAF
     */
    public DungTheory getCoalitionGraph() {
        DungTheory theory = new DungTheory();
        Collection<Collection<Argument>> coalitions = getCoalitions();
        for (Collection<Argument> coalition: coalitions) {
            theory.add(new CoalitionArgument(coalition));
        }
        for (Collection<Argument> c1 : coalitions) {
            for (Collection<Argument> c2: coalitions) {
                if (c1.equals(c2)) continue;
                Collection<Argument> attacked = getAttacked(c1);
                attacked.retainAll(c2);
                if (!attacked.isEmpty()) {
                    theory.addAttack(new CoalitionArgument(c1), new CoalitionArgument(c2));
                }
            }
        }
        return theory;
    }

    /**
     * Returns the corresponding abstract argumentation framework where all supported and indirect attacks are included directly
     * @return an argumentation framework
     */
    public DungTheory getAssociatedTheory() {
        return this.getAssociatedTheory(Support.Type.DEFAULT);
    }

    /**
     * Induces an argumentation framework by resolving the complex attacks of the given support interpretation
     *
     * @param type the support interpretation
     * @return the argumentation framework induced via the given support interpretation
     */
    public DungTheory getAssociatedTheory(Support.Type type) {
        DungTheory theory = new DungTheory();
        theory.addAll(this.getNodes());
        switch (type) {
            case DEFAULT -> {
                theory.addAllAttacks(getSupportedAttacks());
                theory.addAllAttacks(getIndirectAttacks());
            } case SIMPLE_DEDUCTIVE -> {
                theory.addAllAttacks(getMediatedAttacks());
            } case DEDUCTIVE -> {
                theory.addAllAttacks(getSupportedAttacks());
                theory.addAllAttacks(getMediatedAttacks());
            } case NECESSITY -> {
                BipolarArgumentationFramework comp_theory = new BipolarArgumentationFramework();
                comp_theory.addAll(this.getNodes());
                comp_theory.addAllAttacks(this.getAttacks());
                for (Support supp: this.getSupports()) {
                    comp_theory.addSupport(supp.getSupported(), supp.getSupporter());
                }
                return comp_theory.getAssociatedTheory(Support.Type.DEDUCTIVE);
            } case SIMPLE_NECESSITY -> {
                BipolarArgumentationFramework comp_theory = new BipolarArgumentationFramework();
                comp_theory.addAll(this.getNodes());
                comp_theory.addAllAttacks(this.getAttacks());
                for (Support supp: this.getSupports()) {
                    comp_theory.addSupport(supp.getSupported(), supp.getSupporter());
                }
                return comp_theory.getAssociatedTheory(Support.Type.SIMPLE_DEDUCTIVE);
            }
        }
        return theory;
    }

    public boolean isSupportedAttack(Argument arg1, Argument arg2) {
        if (getAttackers(arg2).contains(arg1)) return true;
        Collection<Argument> supported = getSupported(arg1);
        Collection<Argument> attackers = getAttackers(arg2);
        attackers.retainAll(supported);
        return !attackers.isEmpty();
    }

    public boolean isIndirectAttack(Argument arg1, Argument arg2) {
        Collection<Argument> supporters = getSupporters(arg2);
        Collection<Argument> attacked = getAttacked(arg1);
        attacked.retainAll(supporters);
        return !attacked.isEmpty();
    }

    public boolean isMediatedAttack(Argument arg1, Argument arg2){
        if (getAttackers(arg2).contains(arg1)) return true;
        Collection<Argument> attacked = getAttacked(arg1);
        Collection<Argument> supported = getSupported(arg2);
        attacked.retainAll(supported);
        return !attacked.isEmpty();
    }


    public Collection<Attack> getSupportedAttacks(){
        Collection<Attack> attacks = new HashSet<>();
        for (Argument a: this) {
            for (Argument b: this) {
                if (isSupportedAttack(a, b)) {
                    attacks.add(new Attack( a, b));
                }
            }
        }
        return attacks;
    }

    public Collection<Attack> getIndirectAttacks() {
        Collection<Attack> attacks = new HashSet<>();
        for (Argument a: this) {
            for (Argument b: this) {
                if (isIndirectAttack(a, b)) {
                    attacks.add(new Attack(a, b));
                }
            }
        }
        return attacks;
    }

    public Collection<Attack> getMediatedAttacks(){
        Collection<Attack> attacks = new HashSet<>();
        for (Argument a: this) {
            for (Argument b: this) {
                if (isMediatedAttack(a, b)) {
                    attacks.add(new Attack(a, b));
                }
            }
        }
        return attacks;
    }

    public Collection<Argument> getSupported(Collection<Argument> ext){
        Collection<Argument> supported = new HashSet<>();
        Stack<Argument> unvisited = new Stack<>();
        unvisited.addAll(ext);
        while (!unvisited.isEmpty()) {
            Argument arg = unvisited.pop();
            Collection<Argument> candidates = supportChildren.getOrDefault(arg, new HashSet<>());
            for (Argument b: candidates) {
                if (!supported.contains(b)) {
                    supported.add(b);
                    unvisited.add(b);
                }
            }
        }
        return supported;
    }

    public Collection<Argument> getSupporters(Collection<Argument> ext) {
        Collection<Argument> supporters = new HashSet<>();
        Stack<Argument> unvisited = new Stack<>();
        unvisited.addAll(ext);
        while (!unvisited.isEmpty()) {
            Argument arg = unvisited.pop();
            Collection<Argument> candidates = supportParents.getOrDefault(arg, new HashSet<>());
            for (Argument b: candidates) {
                if (!supporters.contains(b)) {
                    supporters.add(b);
                    unvisited.add(b);
                }
            }
        }
        return supporters;
    }

    public Collection<Argument> getSupported(Argument arg){
        Extension<DungTheory> ext = new Extension<>();
        ext.add(arg);
        return getSupported(ext);
    }

    public Collection<Argument> getSupporters(Argument arg) {
        Extension<DungTheory> ext = new Extension<>();
        ext.add(arg);
        return getSupporters(ext);
    }

    /**
     * Adds the given support to this bipolar argumentation framework.
     * @param supp a support
     * @return "true" if the set of supports has been modified.
     */
    public boolean add(Support supp) {
        return this.addSupport(supp.getSupporter(), supp.getSupported());
    }

    /**
     * Adds a support from the first argument to the second to this bipolar argumentation framework.
     * @param supporter some argument
     * @param supported some argument
     * @return "true" if the set of supports has been modified.
     */
    public boolean addSupport(Argument supporter, Argument supported){
        boolean result = false;
        if(!supportParents.containsKey(supported))
            supportParents.put(supported, new HashSet<>());
        result |= supportParents.get(supported).add(supporter);
        if(!supportChildren.containsKey(supporter))
            supportChildren.put(supporter, new HashSet<>());
        result |= supportChildren.get(supporter).add(supported);
        return result;
    }

    public Collection<Support> getSupports(){
        Collection<Support> supports = new HashSet<>();
        for(Argument a: this) {
            if(this.supportChildren.containsKey(a)) {
                for(Argument b: this.supportChildren.get(a))
                    supports.add(new Support(a, b));
            }
        }
        return supports;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(org.tweetyproject.arg.bipolar.syntax.DeductiveArgumentationFramework)
     */
    public int compareTo(BipolarArgumentationFramework o) {
        return this.hashCode() - o.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = super.hashCode();
        result = 43 * result + (this.supportParents == null ? 0 : this.supportParents.hashCode());
        return result;
    }

    public String prettyPrint(){
        StringBuilder output = new StringBuilder();
        for (Argument argument : this) output.append("argument(").append(argument.toString()).append(").\n");
        output.append("\n");
        for (Attack attack : this.getAttacks()) output.append("attack").append(attack.toString()).append(".\n");
        output.append("\n");
        for (Support bArguments : this.getSupports())
            output.append("support").append(bArguments.toString()).append(".\n");
        return output.toString();
    }

    /**
     * Check whether <code>arg1</code> is directly supported by <code>arg2</code>
     * @param arg1 some argument
     * @param arg2 some argument
     * @return 'true' if <code>arg1</code> is directly supported by <code>arg2</code>
     */
    public boolean isSupportedBy(Argument arg1, Argument arg2) {
        return supportParents.getOrDefault(arg1, new HashSet<>()).contains(arg2);
    }
}

