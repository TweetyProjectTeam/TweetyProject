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

package org.tweetyproject.arg.dung.learning.syntax;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Class representing an entity with an underlying hidden argumentation framework. An agent can ask the entity for labelings
 *
 * @author Lars Bengel
 */
public class Entity {

    /** the hidden AF that only this entity has access to */
    private DungTheory hiddenFramework;

    /** structure to store the labeling of the AF */
    private Map<Semantics, List<Input>> examples;

    private List<Input> allInputs;

    /**
     * initialize the entity with the given AF
     * @param theory some argumentation framework
     */
    public Entity(DungTheory theory) {
        hiddenFramework = theory;

        // compute the labelings for all supported semantics
        this.examples = new HashMap<>();
        this.computeExamplesForSemantics(Semantics.CF);
        this.computeExamplesForSemantics(Semantics.ADM);
        this.computeExamplesForSemantics(Semantics.CO);
        this.computeExamplesForSemantics(Semantics.ST);

        this.allInputs = new LinkedList<>();
        this.allInputs.addAll(this.getAllLabelings(Semantics.CF));
        this.allInputs.addAll(this.getAllLabelings(Semantics.ADM));
        this.allInputs.addAll(this.getAllLabelings(Semantics.CO));
        this.allInputs.addAll(this.getAllLabelings(Semantics.ST));

        //System.out.println("CF: " + examples.get(Semantics.CF).size());
        //System.out.println("ADM: " + examples.get(Semantics.ADM).size());
        //System.out.println("CO: " + examples.get(Semantics.CO).size());
        //System.out.println("ST: " + examples.get(Semantics.ST).size());
    }

    public Entity(DungTheory theory, List<Input> inputs) {
        hiddenFramework = theory;
        allInputs = inputs;
    }

    /**
     * helper method to compute all labelings for the given semantics
     * @param sem some semantics
     */
    private void computeExamplesForSemantics(Semantics sem) {
        Collection<Extension<DungTheory>> exts = AbstractExtensionReasoner.getSimpleReasonerForSemantics(sem).getModels(this.hiddenFramework);
        List<Input> examples_sem = new LinkedList<>();
        for (Extension<DungTheory> ext: exts) {
            Input ex = new Input(this.hiddenFramework, ext, sem);
            examples_sem.add(ex);
        }
        this.examples.put(sem, examples_sem);
    }

    /**
     * Ask for a random labeling w.r.t. the given semantics
     * The labeling is also removed from the internal storage so it can ot be given out again
     * @param sem a semantics
     * @return a labeling
     */
    public Input getLabeling(Semantics sem) {
        List<Input> examplesSem = this.examples.get(sem);
        Random rnd = new Random(0);
        int id = rnd.nextInt(examplesSem.size());

        return examplesSem.remove(id);
    }

    public Input getAnyLabeling() {
        Random rnd = new Random();
        int id = rnd.nextInt(this.allInputs.size());

        return this.allInputs.remove(id);
    }

    /**
     * shortcut for getting all labelings wrt a semantics
     * @param sem a semantics
     * @return all labelings of the hidden AF wrt. the given semantics
     */
    public Collection<Input> getAllLabelings(Semantics sem) {
        return this.examples.get(sem);
    }

    /**
     * verify if the given theory is equivalent to the hidden framework w.r.t. the given labelings
     * very inefficiently implemented right now
     *
     * @param theory a dung theory
     * @param inputs a set of labelings
     * @return "true" if both frameworks are equivalent
     */
    public boolean verifyFramework(DungTheory theory, Collection<Input> inputs) {
        for (Semantics sem: this.examples.keySet()) {
            Collection<Extension<DungTheory>> exts = AbstractExtensionReasoner.getSimpleReasonerForSemantics(sem).getModels(theory);
            for (Input input: inputs) {
                if (!sem.equals(input.getSemantics())) {
                    continue;
                }
                Extension<DungTheory> ext = new Extension<DungTheory>(input.getArgumentsOfStatus(ArgumentStatus.IN));
                if (!exts.contains(ext)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * get the set of arguments of the hidden AF
     * @return the set of arguments
     */
    public Collection<Argument> getArguments() {
        return new HashSet<>(this.hiddenFramework);
    }

    /**
     * return the hidden AF
     * should not be used normally
     * @return the hidden AF
     */
    public DungTheory getHiddenAF() {
        return this.hiddenFramework;
    }
}
