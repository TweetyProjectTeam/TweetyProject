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
package org.tweetyproject.lp.asp.beliefdynamics.revision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.tweetyproject.beliefdynamics.gui.RevisionCompareModel;
import org.tweetyproject.beliefdynamics.gui.RevisionComparePresenter;
import org.tweetyproject.beliefdynamics.gui.RevisionComparePresenter.FileHandler;
import org.tweetyproject.beliefdynamics.gui.RevisionCompareView;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.lp.asp.parser.ASPParser;
import org.tweetyproject.lp.asp.parser.InstantiateVisitor;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.DLVSolver;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * Uses the RevisionCompareView in a JFrame to compare the different revision methods in ASP.
 * The first program argument can be used to point to the dlv-complex exectuable. The second
 * program argument can be used to set the current directory of the open belief bases file
 * dialog.
 * @author Tim Janus
 */
public class RevisionTest {
	public static void main(final String [] args) {
		RevisionCompareModel model = new RevisionCompareModel();
		RevisionCompareView view = new RevisionCompareView();
		RevisionComparePresenter presenter = new RevisionComparePresenter(model, view);

		presenter.setFileHandler(new FileHandler() {

			@Override
			public Collection<? extends Formula> load(File file) {
				if(file == null)
					return null;
				try {
					ASPParser parser = new ASPParser(new FileInputStream(file)); //TODO test with new parser
					InstantiateVisitor visitor = new InstantiateVisitor();
					return (Program)parser.Program().jjtAccept(visitor, null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ParseException e) {
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

		JFrame frame = new JFrame("ASP - Revision Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(view);
		frame.setVisible(true);
		frame.pack();

		String path = "";
		String msg = "";

		if(args.length >= 1) {
			path = args[0];
		} else {
			msg += "You can set the first program argument to point to your dlv-complex binary. Now you have to use the file-chooser.";
		}
		if(args.length < 2) {
			if(!msg.isEmpty()) msg += "\n";
			msg += "You can set the second program argument to a path which is used as current directory by the load belief base dialog.";
		}
		if(!msg.isEmpty()) {
			JOptionPane.showMessageDialog(view, msg);
		}

		if(!new File(path).exists()) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			chooser.showOpenDialog(frame);
			path = chooser.getSelectedFile().getPath();
		}

		DLVSolver solver = new DLVSolver(path); //TODO should be DLVComplex
		model.addOperator(new PreferenceHandling(solver));
		model.addOperator(new CredibilityRevision(solver));

		frame.pack();
	}

    /** Default Constructor */
    public RevisionTest(){}
}
