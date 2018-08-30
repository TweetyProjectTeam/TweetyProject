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
package net.sf.tweety.lp.asp.beliefdynamics.selectiverevision;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sf.tweety.arg.lp.semantics.attack.Attack;
import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.arg.lp.semantics.attack.ConfidentAttack;
import net.sf.tweety.arg.lp.semantics.attack.ConfidentRebut;
import net.sf.tweety.arg.lp.semantics.attack.Defeat;
import net.sf.tweety.arg.lp.semantics.attack.StrongAttack;
import net.sf.tweety.arg.lp.semantics.attack.StrongConfidentAttack;
import net.sf.tweety.lp.asp.beliefdynamics.gui.SimpleRevisionCompareModel;
import net.sf.tweety.lp.asp.beliefdynamics.gui.SimpleRevisionComparePresenter;
import net.sf.tweety.lp.asp.beliefdynamics.gui.SimpleRevisionCompareView;
import net.sf.tweety.lp.asp.beliefdynamics.gui.SimpleRevisionComparePresenter.FileHandler;
import net.sf.tweety.lp.asp.beliefdynamics.selectiverevision.ParameterisedArgumentativeSelectiveRevisionOperator.TransformationType;
import net.sf.tweety.lp.asp.reasoner.DLVSolver;

/**
 * This class implements a comparison application for selective revision
 * operators using the SimpleRevisionCompare gui.
 * 
 * @author Sebastian Homann
 */
public class ArgumentativeSelectiveRevisionTest {
	public static void main(final String [] args) {
		SimpleRevisionCompareModel model = new SimpleRevisionCompareModel();
		SimpleRevisionCompareView view = new SimpleRevisionCompareView();
		SimpleRevisionComparePresenter presenter = new SimpleRevisionComparePresenter(model, view);
		
		presenter.setFileHandler(new FileHandler() {
			
			@Override
			public Reader load(File file) {
				if(file == null)
					return null;
				try {
					
					return new FileReader(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			public FileFilter getFilter() {
				return new FileFilter() {
					
					@Override
					public String getDescription() {
						return "*.(asp|dl|dlv)";
					}
					
					@Override
					public boolean accept(File f) {
						String path = f.getPath();
						return f.isDirectory() || path.endsWith("asp") || path.endsWith("dl") || path.endsWith("dlv");
					}
				};
			}

			@Override
			public File getCurrentDiretory() {
				return args.length >= 2 ? new File(args[1]) : new File("."); 
			}
		});
		
		JFrame frame = new JFrame("ASP selective revision test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(view);
		frame.setVisible(true);
		frame.pack();
		
		String path = "";

		
		if(args.length >= 1) {
			path = args[0];
		} else {
			String msg = "Please provide the path to the dlv binary.";
			JOptionPane.showMessageDialog(view, msg);
//			path = "/home/sese/devel/asp_solver/unix/dlv";
		}
		
		if(!new File(path).exists()) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			chooser.showOpenDialog(frame);
			File file = chooser.getSelectedFile();
			if(file == null) {
				String msg = "No dlv solver found, no revisions will be calculated!";
				JOptionPane.showMessageDialog(view, msg, "Error", JOptionPane.ERROR_MESSAGE);
				frame.dispose();
				return;
			} else {
				path = chooser.getSelectedFile().getPath();
			}
		}
		
		DLVSolver solver = new DLVSolver(path);
		model.setSolver(solver);
		
		// add revision operators
		TransformationType type = TransformationType.SCEPTICAL;
		AttackStrategy attack = Attack.getInstance();
		AttackStrategy defense = Attack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = Defeat.getInstance();
		defense = Defeat.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = StrongAttack.getInstance();
		defense = StrongAttack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = StrongAttack.getInstance();
		defense = Attack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = ConfidentRebut.getInstance();
		defense = ConfidentRebut.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = ConfidentAttack.getInstance();
		defense = ConfidentAttack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = StrongConfidentAttack.getInstance();
		defense = StrongConfidentAttack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		type = TransformationType.NAIVE;
		
		attack = StrongAttack.getInstance();
		defense = Attack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = StrongAttack.getInstance();
		defense = StrongAttack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = Defeat.getInstance();
		defense = Defeat.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		attack = Attack.getInstance();
		defense = Attack.getInstance();
		model.addOperator(new ParameterisedArgumentativeSelectiveRevisionOperator(solver, attack, defense, type));
		
		frame.pack();
	}
}
