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
 package net.sf.tweety.arg.dung.solver;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.tweety.arg.dung.parser.FileFormat;

/**
 * An abstract implementation of a solver that provides
 * a command line interface.<br/>
 * NOTE: An actual implementation of a solver must then only contain a main method
 * of the form "public static void main(String[] args){ new MySolver().execute(args); }". 
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractSolver implements InterfaceSolver {

	/* (non-Javadoc)
	 * @see argc.Solver#versionInfo()
	 */
	@Override
	public abstract String versionInfo();

	/* (non-Javadoc)
	 * @see argc.Solver#supportedFormats()
	 */
	@Override
	public abstract Collection<FileFormat> supportedFormats();
		
	/* (non-Javadoc)
	 * @see argc.Solver#supportedProblems()
	 */
	@Override
	public abstract Collection<Problem> supportedProblems();

	/* (non-Javadoc)
	 * @see argc.Solver#solve(argc.constants.Problem, java.io.File, argc.constants.FileFormat, java.lang.String)
	 */
	@Override
	public abstract String solve(Problem problem, File input, FileFormat format, String additionalParameters) throws IOException, IllegalArgumentException;

	/* (non-Javadoc)
	 * @see argc.Solver#execute(java.lang.String[])
	 */
	@Override
	public void execute(String[] args) {
		// if no arguments are given just print out the version info
		if(args.length == 0){
			System.out.println(this.versionInfo());
			return;
		}
		// for the parameter "--formats" print out the formats
		if(args[0].toLowerCase().equals("--formats")){
			System.out.println(this.supportedFormats());
			return;
		}
		// for the parameter "--problems" print out the formats
		if(args[0].toLowerCase().equals("--problems")){
			System.out.println(this.supportedProblems());
			return;
		}
		// otherwise parse for a problem
		String p = null, f = null, fo = null, a = null;
		for(int i = 0; i < args.length; i++){
			if(args[i].toLowerCase().equals("-p")){
				p = args[++i];
				continue;
			}
			if(args[i].toLowerCase().equals("-f")){
				f = args[++i];
				continue;
			}
			if(args[i].toLowerCase().equals("-fo")){
				fo = args[++i];
				continue;
			}
			if(args[i].toLowerCase().equals("-a")){
				a = args[++i];
				continue;
			}
		}
		// if some parameter is missing exit with error (additional parameter is optional)
		if(p == null || f == null || fo == null){
			System.out.println("Error: unrecognized command parameters");
			return;
		}
		Problem problem = Problem.getProblem(p);
		FileFormat format = FileFormat.getFileFormat(fo);
		// check if the problem is supported		
		try {
			if(!this.supportedProblems().contains(problem)){
				System.out.println("Error: problem instance not supported");
				System.exit(1);
			}
			if(!this.supportedFormats().contains(format)){
				System.out.println("Error: file format not supported");
				System.exit(1);
			}
			System.out.println(this.solve(problem, new File(f), format, a));
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			System.out.println("Error: unforeseen exception \"" + e.getMessage() + "\"");
		} catch (IOException e){
			System.out.println("Error: IO error in reading file " + f);			
		}				
	}
}
