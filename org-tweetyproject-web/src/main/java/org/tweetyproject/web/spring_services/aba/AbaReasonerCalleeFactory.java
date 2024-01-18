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
package org.tweetyproject.web.spring_services.aba;

import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.web.spring_services.Callee;


/**
 * *description missing*
 */
public class AbaReasonerCalleeFactory {
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
		QUERY ("query", "query aba framework"),
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
	 * @param <T> *description missing*
	 * @param cmd *description missing*
	 * @param reasoner *description missing*
	 * @param bbase *description missing*
	 * @param a *description missing*
	 * @return *description missing*
	 */
	public static <T extends Formula> Callee getCallee(Command cmd, GeneralAbaReasoner<T> reasoner,  AbaTheory<T> bbase, Assumption<T> a){
            // Create an instance of the object using the constructor
		switch(cmd){
			case GET_MODELS:
				return new AbaReasonerGetModelsCallee<T>(reasoner, bbase);

			case GET_MODEL:
				return new AbaReasonerGetModelCallee<T>(reasoner, bbase);

			case QUERY:
				return new AbaReasonerQueryCallee<T>(reasoner, bbase, a);

			default:
				throw new RuntimeException("Command not found: " + cmd.toString());
		}
	}
}
