package org.tweetyproject.web.pyargservices.aba;

import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.web.pyargservices.Callee;




public class AbaReasonerCalleeFactory {
    public enum Command{
		GET_MODELS ("get_models", "Get all models"),
		QUERY ("query", "query aba framework"),
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

    public static Command [] getCommands(){
        return Command.values();
    }
	/**
	 * Creates a new inconsistency measure of the given type with default
	 * settings.
	 * @param im some identifier of an inconsistency measure.
	 * @return the requested inconsistency measure.
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
	// public static Callee getCallee(org.tweetyproject.web.pyargservices.dung.DungReasonerCalleeFactory.Command command,
	// 		GeneralAbaReasoner<PlFormula> r1, AbaTheory<PlFormula> abat1) {
	// 	switch(command){
	// 		case GET_MODELS:
	// 			return new AbaReasonerGetModelsCallee<PlFormula>(r1, abat1);
	// 		default:
	// 			throw new RuntimeException("Command not found: " + command.toString());
	// 	}
	// }

}
