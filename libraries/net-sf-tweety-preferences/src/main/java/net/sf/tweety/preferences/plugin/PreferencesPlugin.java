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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.preferences.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.plugin.AbstractTweetyPlugin;
import net.sf.tweety.plugin.PluginOutput;
import net.sf.tweety.plugin.parameter.CommandParameter;
import net.sf.tweety.plugin.parameter.SelectionCommandParameter;
import net.sf.tweety.plugin.parameter.FileListCommandParameter;
import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.aggregation.BordaScoringPreferenceAggregator;
import net.sf.tweety.preferences.aggregation.DynamicBordaScoringPreferenceAggregator;
import net.sf.tweety.preferences.aggregation.DynamicPluralityScoringPreferenceAggregator;
import net.sf.tweety.preferences.aggregation.DynamicVetoScoringPreferenceAggregator;
import net.sf.tweety.preferences.aggregation.PluralityScoringPreferenceAggregator;
import net.sf.tweety.preferences.aggregation.VetoScoringPreferenceAggregator;
import net.sf.tweety.preferences.io.POParser;
import net.sf.tweety.preferences.io.ParseException;
import net.sf.tweety.preferences.io.UPParser;
import net.sf.tweety.preferences.update.Update;
import net.sf.tweety.math.opt.solver.Solver;
import net.sf.tweety.math.opt.solver.LpSolve;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * The CLI-Plugin for the Preferences-Package
 * 
 * @author Bastian Wolf
 * 
 */
@PluginImplementation
public class PreferencesPlugin extends AbstractTweetyPlugin {

	// the static identifier for this plugin
	private static final String PREF__CALL_PARAMETER = "pref";

	// Unused!
	// private static final String PREF__PLUGIN_DESCRIPTION = "";

	@Capabilities
	public String[] capabilities() {
		return new String[] { "Tweety Plugin", PREF__CALL_PARAMETER };
	}

	// preference aggregation
	private static final String PREF__AGGR_IDENTIFIER = "-aggr";

	private static final String PREF__AGGR_DESCRIPTION = "-aggr <rule>, preference aggregation command with <rule>={plurality, veto, borda}";

	private static final String[] PREF__AGGR_RULES = new String[] {
			"plurality", "borda", "veto" };

	// dynamic preference aggregation
	private static final String PREF__DYN_IDENTIFIER = "-dynaggr";

	private static final String PREF__DYN_DESCRIPTION = "-dynaggr <rule>, dynamic preference aggregation command with <rule>={dynplurality, dynveto, dynborda}";

	private static final String[] PREF__DYN_RULES = new String[] {
			"dynplurality", "dynborda", "dynveto" };

	// update for dynamic preference aggregation, never use without -dynaggr
	private static final String PREF__UP_IDENTIFIER = "-up";

	private static final String PREF__UP_DESCRIPTION = "-up <file>, update file for dynamic preference aggregation command with <rule>={weaken, strengthen}";

	// private static final File[] PREF__UP_RULES = new String[] { "weaken",
	// "strengthen" };

	// determine, if aggregation is dynamic and updates are possible
	private boolean isDynamic = false;
	
	
	
	public String getCommand() {
		return PREF__CALL_PARAMETER;
	}

	/**
	 * non-empty constructur in case of errors in JSPF
	 * 
	 * @param args some arguments
	 */
	public PreferencesPlugin(String[] args) {
		this();
	}

	// init command parameter
	/**
	 * Default empty constructor initializing this plugins parameter
	 */
	public PreferencesPlugin() {
		super();
		this.addParameter(new SelectionCommandParameter(PREF__AGGR_IDENTIFIER,
				PREF__AGGR_DESCRIPTION, PREF__AGGR_RULES));
		this.addParameter(new SelectionCommandParameter(PREF__DYN_IDENTIFIER,
				PREF__DYN_DESCRIPTION, PREF__DYN_RULES));
		this.addParameter(new FileListCommandParameter(PREF__UP_IDENTIFIER,
				PREF__UP_DESCRIPTION));
	}

	@Override
	/**
	 * executes the given parameters and files in this plugin
	 */
	public PluginOutput execute(File[] input, CommandParameter[] params) {

		Solver.setDefaultLinearSolver(new LpSolve());
		
		// Init empty output
		PluginOutput out = new PluginOutput();

		PreferenceOrder<String> result = new PreferenceOrder<String>();
		// File-Handler
		// Unused!
		// POParser parser;
		// Parsing,...

		List<PreferenceOrder<String>> poset = new ArrayList<PreferenceOrder<String>>();

		for (int i = 0; i < input.length; i++) {
			if (input[i].getAbsolutePath().endsWith(".po")) {
				// PreferenceOrder<String> po;
				try {
					PreferenceOrder<String> po = POParser.parse(input[i]);
					poset.add(po);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// parameter
		for (CommandParameter tempComParam : params) {
			// if command parameter is for aggregation
			if (tempComParam.getIdentifier().equals("-aggr")) {
				SelectionCommandParameter tmp = (SelectionCommandParameter) tempComParam;
				// plurality scoring
				if (tmp.getValue().equalsIgnoreCase("plurality")) {
					PluralityScoringPreferenceAggregator<String> pluraggr = new PluralityScoringPreferenceAggregator<String>();
					result = pluraggr.aggregate(poset);

					// borda scoring
				} else if (tmp.getValue().equalsIgnoreCase("borda")) {
					BordaScoringPreferenceAggregator<String> brdaggr = new BordaScoringPreferenceAggregator<String>(
							poset.iterator().next().size());
					result = brdaggr.aggregate(poset);

					// veto scoring
				} else if (tmp.getValue().equalsIgnoreCase("veto")) {
					VetoScoringPreferenceAggregator<String> vetoaggr = new VetoScoringPreferenceAggregator<String>(
							0);
					result = vetoaggr.aggregate(poset);
				}
				
				out.addField("Result: ", result.toString());
				out.addField("Leveling Function: ", result.getLevelingFunction().toString());
				out.addField("Ranking Function: ", result.getLevelingFunction().getRankingFunction().toString());
			}

			// if command parameter is for dynamic aggregation
			if (tempComParam.getIdentifier().equals("-dynaggr")) {
				// this aggregation is dynamic
				isDynamic = true;

				SelectionCommandParameter tmp = (SelectionCommandParameter) tempComParam;

				ArrayList<Update<String>> up = new ArrayList<Update<String>>();

				for (CommandParameter searchUpdate : params) {
					if (searchUpdate.getIdentifier().equals("-up")) {
						if ((isDynamic)
								&& (searchUpdate instanceof FileListCommandParameter)) {
							// TODO: check dynamic preference aggregation
							// throw new
							// RuntimeException("Method not correctly implemented, please check source code");

							// TODO: check the following code. The variable "up"
							// is never read!

							File[] tmpFile = ((FileListCommandParameter) searchUpdate)
									.getValue();
							// only one update file?
							if (tmpFile.length == 1) {
								if (tmpFile[0].getAbsolutePath()
										.endsWith(".up")) {
									try {
										up = UPParser.parse(tmpFile[0]
												.getAbsolutePath());
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (ParseException e) {
										e.printStackTrace();
									}
								} else {
									System.out
											.println("This is no correctly formatted update file.");
									break;
								}
							}
						}
					}
				}

				// Update counter variable
				int count = 1;

				// dynamic plurality scoring
				if (tmp.getValue().equalsIgnoreCase("dynplurality")) {
					DynamicPluralityScoringPreferenceAggregator<String> dynpluraggr = new DynamicPluralityScoringPreferenceAggregator<String>();
					result = dynpluraggr.aggregate(poset);
					out.addField("Result: ", result.toString());
						for (Update<String> update : up) {
							result = dynpluraggr.update(update, poset);
							out.addField("Update "+count+": ", result.toString());
							count++;
						}

					// dynamic borda scoring
				} else if (tmp.getValue().equalsIgnoreCase("dynborda")) {
					DynamicBordaScoringPreferenceAggregator<String> dynbrdaggr = new DynamicBordaScoringPreferenceAggregator<String>(
							poset.iterator().next().size());
					result = dynbrdaggr.aggregate(poset);
					out.addField("Result: ", result.toString());
					for (Update<String> update : up) {
						result = dynbrdaggr.update(update, poset);
						out.addField("Update "+count+": ", result.toString());
						count++;
					}

					// dynamic veto scoring
				} else if (tmp.getValue().equalsIgnoreCase("dynveto")) {
					DynamicVetoScoringPreferenceAggregator<String> dynvetoaggr = new DynamicVetoScoringPreferenceAggregator<String>(
							0);
					result = dynvetoaggr.aggregate(poset);
					out.addField("Result: ", result.toString());
					for (Update<String> update : up) {
						result = dynvetoaggr.update(update, poset);
						out.addField("Update "+count+": ", result.toString());
						count++;
					}
				}
			}

//			for (CommandParameter tempComParam : params) {
//				// if command parameter is for aggregation
//				if (tempComParam.getIdentifier().equals("-dynaggr") && ) {
//					System.err.println("You either tried to apply one or more updates"
//							"to ");
//				}
//			}
			// if command parameter is for updates of dynamic aggregation
			// if (tempComParam.getIdentifier().equals("-up")) {
			// if(!isDynamic){
			// System.out.println("No Updates allowed within non-dynamic aggregation");
			// } else if((isDynamic) && (tempComParam instanceof
			// FileListCommandParameter)) {
			// // TODO: check dynamic preference aggregation
			// // throw new
			// RuntimeException("Method not correctly implemented, please check source code");
			//
			// // TODO: check the following code. The variable "up" is never
			// read!
			// ArrayList< Update <String>> up = new ArrayList<Update<String>>();
			// File[] tmp = ((FileListCommandParameter)
			// tempComParam).getValue();
			// // only one update file?
			// if(tmp.length == 1){
			// if(tmp[0].getAbsolutePath().endsWith(".up")){
			// try{
			// up = UPParser.parse(tmp[0].getAbsolutePath());
			// } catch (FileNotFoundException e){
			// e.printStackTrace();
			// } catch (ParseException e){
			// e.printStackTrace();
			// }
			// } else {
			// System.out.println("This is no correctly formatted update file.");
			// break;
			// }
			// }
			//
			// }
			// }
		}
	
		return out;

	}

	@Override
	public List<CommandParameter> getParameters() {
		return super.getParameters();
	}

	@Override
	public void addParameter(CommandParameter cmdParameter) {
		super.addParameter(cmdParameter);
	}

}
