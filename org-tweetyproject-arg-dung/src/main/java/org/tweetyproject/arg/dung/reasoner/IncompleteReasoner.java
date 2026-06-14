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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;

/**
 * Reasoner for incomplete argumentation frameworks
 * @author Sebastian Franke
 * @author Lars Bengel
 */
public class IncompleteReasoner implements QualitativeReasoner<IncompleteTheory,Argument>, ModelProvider<Argument, IncompleteTheory, Extension<IncompleteTheory>> {
    /**
     * the underlying Dung reasoner
     */
    private final AbstractExtensionReasoner reasoner;

    /**
     * constructor for direct initialization of reasoner
     * @param reasoner some extension reasoner
     */
    public IncompleteReasoner(AbstractExtensionReasoner reasoner) {
        this.reasoner = reasoner;
    }

    /**
     * constructor for direct initialization of semantics
     * @param semantics the Dung semantics
     */
    public IncompleteReasoner(Semantics semantics) {
        reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
    }

    /**
     *
     * @param theory incomplete theory
     * @return all possible models
     */
    public Collection<Collection<Extension<IncompleteTheory>>> getAllModels(IncompleteTheory theory){
        Collection<Collection<Extension<IncompleteTheory>>> models = new HashSet<>();
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<IncompleteTheory>> instanceModels = new HashSet<>();
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                for (Extension<DungTheory> ext : this.reasoner.getModels(theory)) {
                    instanceModels.add(new Extension<>(ext));
                }
            }

            models.add(instanceModels);
        }
        return models;
    }

    /**
     * Computes the set of possible extensions
     * @param theory incomplete theory
     * @return all possible models
     */
    public Collection<Extension<IncompleteTheory>> getPossibleModels(IncompleteTheory theory){
        Collection<Extension<IncompleteTheory>> models = new HashSet<>();
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                for (Extension<DungTheory> ext : this.reasoner.getModels(theory)) {
                    models.add(new Extension<>(ext));
                }
            }
        }
        return models;
    }

    /**
     * Computes the set of necessary extensions
     * @param theory incomplete theory
     * @return all necessary models
     */
    public Collection<Extension<IncompleteTheory>> getNecessaryModels(IncompleteTheory theory){
        boolean first = true;
        Collection<Extension<IncompleteTheory>> models = new HashSet<>();
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (first) {
                    first = false;
                    for (Extension<DungTheory> ext : this.reasoner.getModels(theory)) {
                        models.add(new Extension<>(ext));
                    }
                }
                Collection<Extension<IncompleteTheory>> instanceModels = new HashSet<>();
                for (Extension<DungTheory> ext : this.reasoner.getModels(theory)) {
                    instanceModels.add(new Extension<>(ext));
                }
                models.retainAll(instanceModels);
            }
        }
        return models;
    }

    /**
     * Decides whether the given argument is a possible credulous conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a possible credulous conclusion
     */
    public boolean isPossibleCredulous(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            //uncertain attacks that can occur in this instance
            Collection<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (this.reasoner.query(theory, arg, InferenceMode.CREDULOUS)) return true;
            }
        }
        return false;
    }

    /**
     * Decides whether the given argument is a necessary credulous conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a necessary credulous conclusion
     */
    public boolean isNecessaryCredulous(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (!this.reasoner.query(theory, arg, InferenceMode.CREDULOUS)) return false;
            }
        }
        return true;
    }

    /**
     * Decides whether the given argument is a possible skeptical conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a skeptical credulous conclusion
     */
    public boolean isPossibleSkeptical(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (this.reasoner.query(theory, arg, InferenceMode.SKEPTICAL)) return true;
            }
        }
        return false;
    }

    /**
     * Decides whether the given argument is a necessary skeptical conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a necessary skeptical conclusion
     */
    public boolean isNecessarySkeptical(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            //uncertain attacks that can occur in this instance
            Collection<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (!this.reasoner.query(theory, arg, InferenceMode.SKEPTICAL)) return false;
            }
        }
        return true;
    }

    @Override
    public Boolean query(IncompleteTheory beliefbase, Argument formula) {
        return query(beliefbase, formula, InferenceMode.SKEPTICAL, Type.NECESSARY);
    }

    /**
     * Determines acceptability of an argument under the given parameters
     * @param beliefbase    some incomplete theory
     * @param formula       some argument
     * @param mode          the inference mode
     * @param type          the type of inference (possible or necessary)
     * @return "true" if the argument is acceptable wrt this reasoner under the given parameters
     */
    public Boolean query(IncompleteTheory beliefbase, Argument formula, InferenceMode mode, Type type) {
        if (mode.equals(InferenceMode.CREDULOUS)) {
            if (type.equals(Type.POSSIBLE)) {
                return isPossibleCredulous(beliefbase, formula);
            } else {
                return isNecessaryCredulous(beliefbase, formula);
            }
        } else {
            if (type.equals(Type.POSSIBLE)) {
                return isPossibleSkeptical(beliefbase, formula);
            } else {
                return isNecessarySkeptical(beliefbase, formula);
            }
        }
    }

    /** The type of inference */
    public enum Type {
        /** possible inference (ie must be contained in one completion */
        POSSIBLE,
        /** necessary inference (ie must be contained in all completions */
        NECESSARY
    }

    /**
     *
     * @return whether the solver is installed
     */
    public boolean isInstalled() {
        return true;
    }

    @Override
    public Collection<Extension<IncompleteTheory>> getModels(IncompleteTheory bbase) {
        return getModels(bbase, Type.NECESSARY);
    }

    /**
     * Determine the models
     * @param bbase some incomplete theory
     * @param type  the inference type
     * @return the models for the given theory and inference type
     */
    public Collection<Extension<IncompleteTheory>> getModels(IncompleteTheory bbase, Type type) {
        if (type.equals(Type.POSSIBLE)) {
            return getPossibleModels(bbase);
        } else {
            return getNecessaryModels(bbase);
        }
    }

    @Override
    public Extension<IncompleteTheory> getModel(IncompleteTheory bbase) {
        return getModels(bbase).iterator().next();
    }

    /**
     * Determines the set of arguments that is acceptable wrt this reasoner and the given parameters
     * @param bbase         some incomplete theory
     * @param type          the type of inference (possible or necessary)
     * @param inferenceMode the inference mode
     * @return The set of arguments that is acceptable wrt this reasoner and the given parameters
     */
    public Collection<Argument> queryAll(IncompleteTheory bbase, Type type, InferenceMode inferenceMode) {
        Collection<Argument> result = new HashSet<>();
        Collection<Extension<IncompleteTheory>> extensions = this.getModels(bbase, type);
        if(inferenceMode.equals(InferenceMode.CREDULOUS))
            for(Collection<Argument> extension: extensions)
                result.addAll(extension);
        else {
            result.addAll(bbase);
            for(Collection<Argument> extension: extensions)
                result.retainAll(extension);
        }
        return result;
    }
}
