package org.tweetyproject.web.pyargservices.dung;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.pyargservices.Callee;




public class DungReasonerCalleeFactory {
    public enum Command{
		GET_MODELS ("get_models", "Get all models"),
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
