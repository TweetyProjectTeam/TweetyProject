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
package org.tweetyproject.logics.mln.reasoner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.semantics.HerbrandBase;
import org.tweetyproject.logics.fol.semantics.HerbrandInterpretation;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;

/**
 * This class implements a simple reasoner for MLNs.
 *
 * @author Matthias Thimm
 */
public class SimpleMlnReasoner extends AbstractMlnReasoner {

	/** Default */
	public SimpleMlnReasoner(){
		super();
	}

	/** Directory for temporary files. */
	private String tempDirectory = null;

	/** Sets the path of the directory for temporary files.
	 * @param str a file path
	 */
	public void setTempDirectory(String str){
		this.tempDirectory = str;
	}

	/** Computes the model of the given MLN.
	 * @param mln The MLN
	 * @param signature the signature
	 * @return a file where the model is stored.
	 */
	private File computeModel(MarkovLogicNetwork mln, FolSignature signature){
		//1.) write all possible worlds of the signature into a text file
		// (Note: we avoid doing this in memory due to exponential size)
		try {
			HerbrandBase hBase = new HerbrandBase(signature);
			FileWriter fstream;
			FileInputStream inStream;
			boolean isFirst = true;
			File currentFile = File.createTempFile("naive_mln",null,new File(this.tempDirectory));
			currentFile.deleteOnExit();
			for(FolAtom a: hBase.getAtoms()){
				if(isFirst){
					fstream = new FileWriter(currentFile.getAbsoluteFile());
					BufferedWriter out = new BufferedWriter(fstream);
					out.append(a.toString());
					out.newLine();
					out.newLine();
					isFirst = false;
					out.close();
				}else{
					File temp = File.createTempFile("naive_mln",null,new File(this.tempDirectory));
					temp.deleteOnExit();
					fstream = new FileWriter(temp.getAbsoluteFile());
					inStream = new FileInputStream(currentFile.getAbsoluteFile());
					BufferedWriter out = new BufferedWriter(fstream);
					DataInputStream in = new DataInputStream(inStream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					while((strLine = br.readLine()) != null){
						out.append(strLine);
						out.newLine();
						if(strLine.equals(""))
							out.append(a.toString());
						else out.append(strLine + ";" + a.toString());
						out.newLine();
					}
					in.close();
					out.close();
					currentFile = temp;
				}
			}
			// 2.) for each possible world compute its impact; also, sum up all impacts
			double sum = 0;
			File temp = File.createTempFile("naive_mln",null,new File(this.tempDirectory));
			temp.deleteOnExit();
			fstream = new FileWriter(temp.getAbsoluteFile());
			inStream = new FileInputStream(currentFile.getAbsoluteFile());
			BufferedWriter out = new BufferedWriter(fstream);
			DataInputStream in = new DataInputStream(inStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			boolean emptyLine = false;
			double weight;
			while((strLine = br.readLine()) != null){
				if(strLine.equals("")){
					if(emptyLine) continue;
					else emptyLine = true;
				}
				HerbrandInterpretation hInt = this.parseInterpretation(strLine,signature);
				weight = this.computeWeight(mln,hInt,signature);
				sum += weight;
				out.append(strLine + "#" + weight);
				out.newLine();
			}
			in.close();
			out.close();
			currentFile = temp;

			// 3.) normalize by sum
			temp = File.createTempFile("naive_mln",null,new File(this.tempDirectory));
			temp.deleteOnExit();
			fstream = new FileWriter(temp.getAbsoluteFile());
			inStream = new FileInputStream(currentFile.getAbsoluteFile());
			out = new BufferedWriter(fstream);
			in = new DataInputStream(inStream);
			br = new BufferedReader(new InputStreamReader(in));
			while((strLine = br.readLine()) != null){
				if(strLine.equals("")) break;
				StringTokenizer tokenizer = new StringTokenizer(strLine,"#");
				try{
					if(tokenizer.countTokens() == 1)
						out.append("#" + (Double.parseDouble(tokenizer.nextToken())/sum));
					else
						out.append(tokenizer.nextToken() + "#" + (Double.parseDouble(tokenizer.nextToken())/sum));
					out.newLine();
				}catch(Exception e){

				}
			}
			in.close();
			out.close();
			return temp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.mln.AbstractMlnReasoner#doQuery(org.tweetyproject.logics.mln.MarkovLogicNetwork, org.tweetyproject.logics.fol.syntax.FolFormula, org.tweetyproject.logics.fol.syntax.FolSignature)
	 */
	@Override
	public double doQuery(MarkovLogicNetwork mln, FolFormula query, FolSignature signature) {
		FileInputStream inStream;
		File model = this.computeModel(mln, signature);
		try {
			inStream = new FileInputStream(model.getAbsoluteFile());
			DataInputStream in = new DataInputStream(inStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			double prob = 0;
			String strLine;
			while((strLine = br.readLine()) != null){
				StringTokenizer tokenizer = new StringTokenizer(strLine,"#");
				try{
					HerbrandInterpretation hInt;
					if(tokenizer.countTokens() == 1)
						hInt = new HerbrandInterpretation();
					else hInt = this.parseInterpretation(tokenizer.nextToken(),signature);
					if(hInt.satisfies(query))
						prob += Double.parseDouble(tokenizer.nextToken());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			in.close();
			return prob;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/** Constructs a Herbrand interpretation from the given string
	 * @param str a string.
	 * @param signature the signature
	 * @return a Herbrand interpretation
	 */
	private HerbrandInterpretation parseInterpretation(String str,FolSignature signature){
		StringTokenizer tokenizer = new StringTokenizer(str, ";");
		Collection<FolAtom> atoms = new HashSet<FolAtom>();
		FolParser parser = new FolParser();
		parser.setSignature(signature);
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			try {
				atoms.add( (FolAtom)parser.parseFormula(token));
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new HerbrandInterpretation(atoms);
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
