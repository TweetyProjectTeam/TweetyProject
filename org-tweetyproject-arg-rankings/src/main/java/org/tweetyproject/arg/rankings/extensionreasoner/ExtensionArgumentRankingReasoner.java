package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;



/**
 * 
 * This Reasoner ranks Arguments according to the paper "Social Ranking Problem For Abstract Argumentation (Def.16,17,18),
 *  Kenneth Skiba 2023".
 * 
 * @author Benjamin Birner
 *
 */
public class ExtensionArgumentRankingReasoner{
	
		/**
		 * This Method ranks the given Arguments.
		 * 
		 * @param rankedExt Extensions that includes the Arguments to rank.
		 * @return ArrayList that includes the ranked Arguments.
		 */
		public ArrayList<LinkedList<Argument>> getRankedArguments(ArrayList<LinkedList<Extension<DungTheory>>> rankedExt) {
			
			LinkedList<HashMap<String, Integer>> listOfMaps = new LinkedList<>();
			LinkedList<String> argList = new LinkedList<>();
		    
		    for( LinkedList<Extension<DungTheory>> list : rankedExt) {
		    	if(list.size() > 0) {
		    		HashMap<String, Integer> map = new HashMap<>();
			    	for(Extension<DungTheory> ext : list) {
			    		Iterator<Argument> it = ext.iterator();
			    		while(it.hasNext()) {
			    			String arg = it.next().toString();
			    			if(!argList.contains(arg)) {
			    				argList.add(arg);
			    			}
			                if (map.containsKey(arg)) {
			                    int count = map.get(arg);
			                    map.put(arg, count + 1);
			                } else {
			                    map.put(arg, 1);
			                }
			    		}	
			    	}
			    	listOfMaps.add(map);
		    	}
		    }
		    
		    ArrayList<LinkedList<Argument>> rankedArgs = new ArrayList<>(argList.size());
		   
		    for( int i=0; i < argList.size(); i++ ) {
				rankedArgs.add(new LinkedList<Argument>());
			}
		    
		    int i = 0;
		    int j = 0;
		    for (HashMap<String, Integer> map : listOfMaps) {
		  
		        if (argList.isEmpty()) {
		            break;
		        }

		        
		        while (!map.isEmpty()) {
		        	
		        	List<String> keysToRemove = new ArrayList<>();

		        	for (Map.Entry<String, Integer> entry : map.entrySet()) {
		        	    String key = entry.getKey();
		        	    if (!argList.contains(key)) {
		        	        keysToRemove.add(key); 
		        	    }
		        	}

		        	
		        	for (String keyToRemove : keysToRemove) {
		        	    map.remove(keyToRemove);
		        	}
		        	
		            String maxKey = null;
		            int maxValue = Integer.MIN_VALUE;

		            for (Map.Entry<String, Integer> entry : map.entrySet()) {
		                if (entry.getValue() > maxValue) {
		                    maxValue = entry.getValue();
		                    maxKey = entry.getKey();
		                }
		            }

		         
		            int count = 0;
		            for (Map.Entry<String, Integer> entry : map.entrySet()) {
		                if (entry.getValue() == maxValue) {
		                    count++;
		                }
		            }
		     
		            if (count == 1) {
		                rankedArgs.get(i).add(new Argument(maxKey));
		                i++;
		                map.remove(maxKey);
		                argList.remove(maxKey);
		            } else {
		            	LinkedList<String> eqRank = new LinkedList<>();
		                for (Map.Entry<String, Integer> entry : map.entrySet()) {
		                    if (entry.getValue() == maxValue) {
		                    	eqRank.add(entry.getKey()); 
		                    }
		                }
		                for(String s : eqRank) {
		                	map.remove(s);
		                }
		                for(int k = j+1; k < listOfMaps.size(); k++) {
		                	HashMap<String, Integer> eqMapK = new HashMap<>();
		                	HashMap<String, Integer> rankK = listOfMaps.get(k);
		                	for( String s : eqRank) {
		                		if ( rankK.containsKey(s)) {
		                			eqMapK.put(s, rankK.get(s));
		                		}
		                	}
		                	
		                	
		                	
		                	
		                	String maxKey2 = null;
				            int maxValue2 = Integer.MIN_VALUE;

				            for (Map.Entry<String, Integer> entry : eqMapK.entrySet()) {
				                if (entry.getValue() > maxValue2) {
				                    maxValue2 = entry.getValue();
				                    maxKey2 = entry.getKey();
				                }
				            }

				         
				            int count3 = 0;
				            for (Map.Entry<String, Integer> entry : eqMapK.entrySet()) {
				                if (entry.getValue() == maxValue2) {
				                    count3++;
				                }
				            }
				    
				            if (count3 == 1) {
				                rankedArgs.get(i).add(new Argument(maxKey2));
				                i++;
				                map.remove(maxKey2);
				                argList.remove(maxKey2);
				                eqRank.remove(maxKey2);
				                rankK.remove(maxKey2);
				            }
				            if(k == listOfMaps.size()-1) {
				            	for(String a : eqRank) {
				            		rankedArgs.get(i).add(new Argument(a));
				            		map.remove(a);
					                argList.remove(a);
				            	}
				            	i++;
				            	
				            }
				            if(eqRank.size() == 0) {
				            	break;
				            }  
		                }
		            }
		        }
		        j++;
		    }
		    if (argList.size() > 0) {
		    	for(String s : argList) {
		    		rankedArgs.get(i).add(new Argument(s));
		    	}
		    }
		    return rankedArgs;	
		}
		

		
}

