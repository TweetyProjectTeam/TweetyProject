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
package net.sf.tweety.beliefdynamics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.sf.tweety.beliefdynamics.BaseRevisionOperator;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;

/**
 * This class couples the RevisionCompare View and Model, it needs to know the implementation used to
 * load external belief base files, see FileHandler interface. The presenter reacts to user inputs on 
 * the view and proofs if the changes are valid and then delegates them to the model. It also registers
 * the view as a PropertyListener to the correct model.
 * 
 * @author Tim Janus
 */
public class RevisionComparePresenter implements ItemListener, ChangeListener, ActionListener {

	/**
	 * This interface is used by the RevisionComparePresenter to handle the file loading. Therefore
	 * it provides a FileFilter for the open file dialog and a method which converts the given file
	 * in a collection of formulas.
	 * 
	 * @author Tim Janus
	 */
	public static interface FileHandler {
		Collection<? extends Formula> load(File file);
		
		FileFilter getFilter();
		
		File getCurrentDiretory();
	}
	
	/** the data model for the revision compare */
	private RevisionCompareModel model;
	
	/** the view showing the revision compare */
	private RevisionCompareView view;
	
	/** the default file handler cannot load any files and has to be replaced */
	private FileHandler fileHandler = new DefaultFileHandler();
	
	/** Default Ctor: registers the view as listener to the correct model. 
	 * @param model a revision compare model
	 * @param view a view
	 */
	public RevisionComparePresenter(RevisionCompareModel model, RevisionCompareView view) {
		this.model = model;
		this.view = view;
		model.addListener(view);
		
		registerAsViewListener();
	}
	
	/**
	 * Sets a file handler which is responsible to filter the open file dialog and to load
	 * the selected file. 
	 * @param handler	An implementation of the FileHandler interface or null. If null is given
	 * 					then the default handler is used which cannot open any files.
	 */
	public void setFileHandler(FileHandler handler) {
		if(handler == null) {
			fileHandler = new DefaultFileHandler();
		} else {
			fileHandler = handler;
		}
	}
	
	/**
	 * Helper method: Registers the presenter as swing listener at the view components.
	 */
	private void registerAsViewListener() {
		view.cbOperatorLeft.addItemListener(this);
		view.cbOperatorRight.addItemListener(this);
		view.checkIterativeLeft.addChangeListener(this);
		view.checkIterativeRight.addChangeListener(this);
		view.sliderStep.addChangeListener(this);
		view.btnUp.addActionListener(this);
		view.btnDown.addActionListener(this);
		view.btnAdd.addActionListener(this);
		view.btnRemove.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		if(ev.getSource() == view.btnAdd) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setCurrentDirectory(fileHandler.getCurrentDiretory());
			chooser.setFileFilter(fileHandler.getFilter());
			chooser.setMultiSelectionEnabled(true);
			chooser.showOpenDialog(view);
			
			File[] choosenFiles = chooser.getSelectedFiles();
			for(File choosenFile : choosenFiles) {
				Collection<? extends Formula> beliefBase = fileHandler.load(choosenFile);
				if(beliefBase == null) {
					JOptionPane.showMessageDialog(view, "Cannot load '" + choosenFile.getPath() + "' using the file handler: '"
							+ fileHandler.getClass().getName() + "'.");
				} else {
					model.addBeliefbase(beliefBase);
				}
			}
		} else if(ev.getSource() == view.btnRemove) {
			model.removeBeliefbase((Collection<?>)view.lstBeliefBases.getSelectedValue());
		} else if(ev.getSource() == view.btnUp || ev.getSource() == view.btnDown) {
			int dir = ev.getSource() == view.btnUp ? -1 : 1;
			model.moveBeliefbase((Collection<?>)view.lstBeliefBases.getSelectedValue(), dir);
		}
	}

	@Override
	public void stateChanged(ChangeEvent ev) {
		if(ev.getSource() == view.sliderStep) {
			
		} else if(ev.getSource() == view.checkIterativeLeft) {
			model.setLeftIterative(view.checkIterativeLeft.isSelected());
		} else if(ev.getSource() == view.checkIterativeRight) {
			model.setRightIterative(view.checkIterativeRight.isSelected());
		}
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {
		if(ev.getSource() == view.cbOperatorLeft) {
			model.setLeftOperator((BaseRevisionOperator<?>)view.cbOperatorLeft.getSelectedItem());
		} else if(ev.getSource() == view.cbOperatorRight) {
			model.setRightOperator((BaseRevisionOperator<?>)view.cbOperatorRight.getSelectedItem());
		}
	}
	
	/** Functional Test method: Only shows the view in a JFrame to test resize behavior. 
	 * @param args arguments*/
	public static void main(String [] args) {
		RevisionCompareModel model = new RevisionCompareModel();
		RevisionCompareView view = new RevisionCompareView();
		new RevisionComparePresenter(model, view);
		
		JFrame frame = new JFrame("Functional Test: Revision Compare View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(view);
		frame.setVisible(true);
		
		List<Formula> c1 = new LinkedList<Formula>();
		List<Formula> c2 = new LinkedList<Formula>();
		List<Formula> c3 = new LinkedList<Formula>();
		c1.add(new MockFormula());
		c2.add(new MockFormula());
		c1.add(new MockFormula());
		c3.add(new MockFormula());
		c3.add(new MockFormula());
		
		model.addBeliefbase(c1);
		model.addBeliefbase(c2);
		model.addBeliefbase(c3);
		
		model.addOperator(new MockOperator("Preference Handling"));
		model.addOperator(new MockOperator("Credibility Aware"));
		frame.pack();
		
	}
	
	/** does not try to load the file but only return null */
	private static class DefaultFileHandler implements FileHandler {
		@Override
		public Collection<? extends Formula> load(File file) {
			return null;
		}

		@Override
		public FileFilter getFilter() {
			return new FileFilter() {
				
				@Override
				public String getDescription() {
					return "NO-FILE-HANDLER-CODDING-ERROR";
				}
				
				@Override
				public boolean accept(File f) {
					return false;
				}
			};
		}

		@Override
		public File getCurrentDiretory() {
			return new File(".");
		}
	}
	
	/**
	 * Mock formula used for functional test.
	 * @author Tim Janus
	 */
	private static class MockFormula implements Formula {

		private char mc;
		
		private static char c = 'a';
		
		public MockFormula() {
			mc = c++;
		}
		
		@Override
		public Signature getSignature() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String toString() {
			return "" + mc;
		}
		
	}
	
	/**
	 * Mock revision operator for functional test.
	 * @author Tim Janus
	 */
	private static class MockOperator implements BaseRevisionOperator<MockFormula> {

		private String name;
		
		public MockOperator(String name) {
			this.name = name;
		}
		
		@Override
		public Collection<MockFormula> revise(Collection<MockFormula> base,
				MockFormula formula) {
			base.add(formula);
			return base;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
