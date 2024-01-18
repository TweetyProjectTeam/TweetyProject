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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.spring_services.dung;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.spring_services.Callee;




/**
 * *description missing*
 */
public class DungReasonerCalleeFactory {
    /**
     * *description missing*
     */
    public enum Command{
		/**
		 * *description missing*
		 */
		GET_MODELS ("get_models", "Get all models"),
		/**
		 * *description missing*
		 */
		GET_MODEL ("get_model", "Get some model");
		/**id*/
		public String id;
		/**label*/
		public String label;

		Command(String id, String label){
			this.id = id;
			this.label = label;
		}
		/**
		 *
		 * @param id ID
		 * @return the measure
		 */
		public static Command getCommand(String id){
			for(Command m: Command.values())
				if(m.id.equals(id))
					return m;
			return null;
		}
	}

    /**
     * *description missing*
     * @return *description missing*
     */
    public static Command [] getCommands(){
        return Command.values();
    }

	/**
	 * *description missing*
	 * @param cmd *description missing*
	 * @param reasoner *description missing*
	 * @param bbase *description missing*
	 * @return *description missing*
	 */
	public static  Callee getCallee(Command cmd, AbstractExtensionReasoner reasoner, DungTheory bbase){
		switch(cmd){
			case GET_MODELS:
				return new DungReasonerGetModelsCallee(reasoner, bbase);
            case GET_MODEL:
                return new DungReasonerGetModelCallee(reasoner, bbase);
			default:
				throw new RuntimeException("Command not found: " + cmd.toString());
		}
	}

}
