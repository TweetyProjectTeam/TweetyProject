package org.tweetyproject.web.pyargservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.web.pyargservices.AbstractExtensionReasonerFactory.Semantics;
import org.tweetyproject.web.pyargservices.DungReasonerCalleeFactory.Command;
import org.tweetyproject.web.services.DelpService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.tweetyproject.arg.delp.parser.DelpParser;
import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.ComparisonCriterion;
import org.tweetyproject.arg.delp.semantics.DelpAnswer;
import org.tweetyproject.arg.delp.semantics.EmptyCriterion;
import org.tweetyproject.arg.delp.semantics.GeneralizedSpecificity;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;

@RestController
public class RequestController {

	private final int SERVICES_TIMEOUT = 600;

	@PostMapping(value = "/dung", produces = "application/json")
	@ResponseBody
	public DungReasonerResponse handleRequest(
  	@RequestBody PyArgPost pyArgPost) {
		DungTheory dungTheory = Utils.getDungTheory(pyArgPost.getNr_of_arguments(), pyArgPost.getAttacks());
		
		AbstractExtensionReasoner reasoner = AbstractExtensionReasonerFactory.getReasoner(
							Semantics.getSemantics(pyArgPost.getSemantics()));
		ExecutorService executor = Executors.newSingleThreadExecutor();
		DungReasonerResponse reasonerResponse = new DungReasonerResponse(pyArgPost.getCmd(), pyArgPost.getEmail(), pyArgPost.getNr_of_arguments(),pyArgPost.getAttacks(), pyArgPost.getSemantics(),pyArgPost.getSolver(),null,0,pyArgPost.getUnit_timeout(),"ERRORs");
		Collection<Extension<DungTheory>> models = null;
		TimeUnit unit = Utils.getTimoutUnit(pyArgPost.getUnit_timeout());
		System.out.println(pyArgPost.getUnit_timeout());
		try{
			// handle timeout
			DungReasonerCallee callee = DungReasonerCalleeFactory.getCallee(
				Command.getCommand(pyArgPost.getCmd()), reasoner,dungTheory);
				Future<Collection<Extension<DungTheory>>> future = executor.submit(callee);
				int user_timeout = pyArgPost.getTimeout();
				if (user_timeout > SERVICES_TIMEOUT){
					user_timeout = SERVICES_TIMEOUT;
				}

				
				
				long millis = System.currentTimeMillis();
			    models = future.get(user_timeout, unit);
			    executor.shutdownNow();
				millis = System.currentTimeMillis() - millis;
				long time = 0;
				if (pyArgPost.getUnit_timeout().equals("sec")){
					System.out.println("converting millis to seconds");
					time = TimeUnit.MILLISECONDS.toSeconds(millis);
					System.out.println(time);

				}
				else {
					time = millis;
				}
				reasonerResponse.setTime(time);
				reasonerResponse.setAnswer(models.toString());
				reasonerResponse.setStatus("SUCCESS");
			} catch (TimeoutException e) {
				reasonerResponse.setTime(pyArgPost.getTimeout());
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("TIMEOUT");
				executor.shutdownNow();
			} catch (Exception e){
				reasonerResponse.setTime(0.0);
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("Error");
				
				executor.shutdownNow();
			}
		return reasonerResponse;
	}


	@PostMapping(value = "/info", produces = "application/json")
	@ResponseBody
	public DungServicesInfoResponse getInfo(@RequestBody PyArgPost pyArgPost){
		DungServicesInfoResponse response = new DungServicesInfoResponse();
		response.setReply("info");
		response.setEmail(pyArgPost.getEmail());
		response.setBackend_timeout(SERVICES_TIMEOUT);
		Semantics [] sem = AbstractExtensionReasonerFactory.getSemantics();
		ArrayList<String> semantics_ids = new ArrayList<String>();
		for (Semantics s : sem){
			semantics_ids.add(s.id);
		}
		response.setSemantics(semantics_ids);

		Command [] com = DungReasonerCalleeFactory.getCommands();
		ArrayList<String> command_ids = new ArrayList<String>();
		for (Command c : com){
			command_ids.add(c.id);
		}
		response.setCommands(command_ids);


		//response.setSemantics(AbstractExtensionReasonerFactory.Semantics.values());
		return response;
	}

	@PostMapping(value = "/delp", produces = "application/json")
	@ResponseBody
	public DelpResponse handleRequest(
  	@RequestBody DelpPost delpPost) {
		  DelpResponse delpResponse =  new DelpResponse("query", delpPost.getEmail(), delpPost.getCompcriterion(), delpPost.getKb(), delpPost.getQuery(), delpPost.getTimeout(),null, 0.0, delpPost.getUnit_timeout(),null);
		  try {

			DelpParser parser = new DelpParser();
			DefeasibleLogicProgram delp = parser.parseBeliefBase(delpPost.getKb());
				ComparisonCriterion comp = null;
				if(delpPost.getCompcriterion().equals(DelpService.JSON_ATTR_COMP_EMPTY))
						comp = new EmptyCriterion();
				if(delpPost.getCompcriterion().equals(DelpService.JSON_ATTR_COMP_GENSPEC))
						comp = new GeneralizedSpecificity();
				if(comp == null)
					throw new JSONException("Malformed JSON: unknown value for attribute \"compcriterion\"");
				DelpReasoner reasoner = new DelpReasoner(comp);
				FolParser folParser = new FolParser();
				folParser.setSignature(parser.getSignature());
				String qString = delpPost.getQuery().trim();
				Formula f = null;
				if(qString.startsWith("~"))
						f = new Negation((FolFormula)folParser.parseFormula(qString.substring(1)));
				else f = folParser.parseFormula(qString);
				
				DelpAnswer.Type ans = reasoner.query(delp,(FolFormula) f);
				System.out.println(ans.toString());

				delpResponse.setAnswer(ans.toString());
				delpResponse.setStatus("SUCCESS");

				System.out.println(delpResponse.toString());


				return delpResponse;
		
	} catch (ParserException e) {			
		throw new JSONException("Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
	} catch (IOException e) {			
		throw new JSONException("Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
	} catch(Exception e){
		e.printStackTrace();
		throw new JSONException("An unexpected error occured. Please contact an administrator.");
	}		
	}


}
