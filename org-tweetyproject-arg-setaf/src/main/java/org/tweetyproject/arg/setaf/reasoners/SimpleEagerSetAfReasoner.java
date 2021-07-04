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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.setaf.reasoners;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.setaf.semantics.SetAfExtension;
import org.tweetyproject.arg.setaf.semantics.SetAfLabeling;
import org.tweetyproject.arg.setaf.syntax.SetAf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * simple reasoner for eager semantics
 *
 * @author Lars Bengel, Sebastian Franke
 */
public class SimpleEagerSetAfReasoner extends AbstractExtensionSetAfReasoner {
    @Override
    public Collection<SetAfExtension> getModels(SetAf bbase) {
        Collection<SetAfExtension> exts = new HashSet<SetAfExtension>();
        exts.add(this.getModel(bbase));
        return exts;
    }

    @Override
    public SetAfExtension getModel(SetAf bbase) {
        Collection<SetAfExtension> admExt = new SimpleAdmissibleSetAfReasoner().getModels(bbase);
        Collection<SetAfExtension> sstExt = new SimpleSemiStableSetAfReasoner().getModels(bbase);
        Set<SetAfLabeling> potResult = new HashSet<SetAfLabeling>();
        boolean potEager;
        for(SetAfExtension ext: admExt){
            SetAfLabeling extLab = new SetAfLabeling(bbase,ext);
            // ext is eager if
            // 1. for every semi-stable labeling L both in and out are subsets of that sets in L
            potEager = true;
            for(SetAfExtension ext2: sstExt){
                SetAfLabeling extLab2 = new SetAfLabeling(bbase, ext2);
                if(!extLab2.getArgumentsOfStatus(ArgumentStatus.IN).containsAll(extLab.getArgumentsOfStatus(ArgumentStatus.IN))){
                    potEager = false;
                    break;
                }
                if(!extLab2.getArgumentsOfStatus(ArgumentStatus.OUT).containsAll(extLab.getArgumentsOfStatus(ArgumentStatus.OUT))){
                    potEager = false;
                    break;
                }
            }
            if(potEager)
                potResult.add(extLab);
        }
        // get the one which maximizes in and out
        // Note that there is only one eager extension
        boolean eager;
        for(SetAfLabeling lab: potResult){
            eager = true;
            for(SetAfLabeling lab2: potResult){
                if(lab != lab2)
                    if(lab2.getArgumentsOfStatus(ArgumentStatus.IN).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.IN)))
                        if(lab2.getArgumentsOfStatus(ArgumentStatus.OUT).containsAll(lab.getArgumentsOfStatus(ArgumentStatus.OUT))){
                            eager = false;
                            break;
                        }
            }
            if(eager)
                return lab.getArgumentsOfStatus(ArgumentStatus.IN);
        }
        // this should not happen as there is always an eager extension;
        throw new RuntimeException("Eager extension seems to be undefined.");
    }
}
