package org.tweetyproject.web.services.spring.src.main.java.com.pyargservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import org.tweetyproject.web.services.spring.src.main.java.com.pyargservices.AbstractExtensionReasonerFactory.Semantics;
import org.tweetyproject.web.services.spring.src.main.java.com.pyargservices.DungReasonerCalleeFactory.Command;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;

@RestController
public class DungReasonerController {

	private final int SERVICES_TIMEOUT = 600;

	@PostMapping(value = "/dung", produces = "application/json")
	@ResponseBody
	public DungReasonerResponse handleRequest(
  	@RequestBody PyArgPost pyArgPost) {
		DungTheory dungTheory = Utils.getDungTheory(pyArgPost.getNr_of_arguments(), pyArgPost.getAttacks());
		
		AbstractExtensionReasoner reasoner = AbstractExtensionReasonerFactory.getReasoner(
							Semantics.getSemantics(pyArgPost.getSemantics()));
		ExecutorService executor = Executors.newSingleThreadExecutor();
		DungReasonerResponse reasonerResponse = new DungReasonerResponse(pyArgPost.getCmd(), pyArgPost.getEmail(), pyArgPost.getNr_of_arguments(),pyArgPost.getAttacks(), pyArgPost.getSemantics(),pyArgPost.getSolver(),null,0,"ERRORs");
		Collection<Extension<DungTheory>> models = null;
		try{
				long millis = System.currentTimeMillis();
				// handle timeout
				DungReasonerCallee callee = DungReasonerCalleeFactory.getCallee(
							Command.getCommand(pyArgPost.getCmd()), reasoner,dungTheory);
				Future<Collection<Extension<DungTheory>>> future = executor.submit(callee);
				int user_timeout = pyArgPost.getTimeout();
				if (user_timeout > SERVICES_TIMEOUT){
					user_timeout = SERVICES_TIMEOUT;
				}
			    models = future.get(user_timeout, TimeUnit.SECONDS);
			    executor.shutdownNow();
				millis = System.currentTimeMillis() - millis;
				long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
				reasonerResponse.setTime(seconds);
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


}
