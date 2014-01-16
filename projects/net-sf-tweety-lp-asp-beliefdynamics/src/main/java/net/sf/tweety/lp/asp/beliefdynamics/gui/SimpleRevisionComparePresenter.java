package net.sf.tweety.lp.asp.beliefdynamics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.Reader;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.sf.tweety.beliefdynamics.BaseRevisionOperator;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.syntax.Rule;
//import javax.swing.JOptionPane;

/**
 * This class couples the SimpleRevisionCompare View and Model, it needs to know the implementation used to
 * load external belief base files, see FileHandler interface. The presenter reacts to user inputs on 
 * the view and proofs if the changes are valid and then delegates them to the model. It also registers
 * the view as a PropertyListener to the correct model.
 * This gui is based on the RevisionCompare gui from the beliefdynamics.gui package.
 * 
 * @author Sebastian Homann
 * @author Tim Janus
 */
public class SimpleRevisionComparePresenter implements ItemListener, ChangeListener, ActionListener {

	/**
	 * This interface is used by the SimpleRevisionComparePresenter to handle the file loading.
	 * 
	 * @author Tim Janus
	 */
	public static interface FileHandler {
		Reader load(File file);
		
		FileFilter getFilter();
		
		File getCurrentDiretory();
	}
	
	/** the data model for the revision compare */
	private SimpleRevisionCompareModel model;
	
	/** the view showing the revision compare */
	private SimpleRevisionCompareView view;
	
	/** the default file handler cannot load any files and has to be replaced */
	private FileHandler fileHandler = new DefaultFileHandler();
	
	/** Default Ctor: registers the view as listener to the correct model. */
	public SimpleRevisionComparePresenter(SimpleRevisionCompareModel model, SimpleRevisionCompareView view) {
		this.model = model;
		this.view = view;
		model.addListener(view);
		
		registerAsViewListener();
	}
	
	/**
	 * Sets a file handler responsible for filtering the open file dialog and loading
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
	 * Helper method: Register the presenter with the view components.
	 */
	private void registerAsViewListener() {
		view.cbOperatorLeft.addItemListener(this);
		view.cbOperatorRight.addItemListener(this);

		view.btnAddLeft.addActionListener(this);
		view.btnAddRight.addActionListener(this);
		view.btnRunRevision.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		if(ev.getSource() == view.btnAddLeft) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setCurrentDirectory(fileHandler.getCurrentDiretory());
			chooser.setFileFilter(fileHandler.getFilter());
			chooser.setMultiSelectionEnabled(true);
			chooser.showOpenDialog(view);
			
			File[] chosenFiles = chooser.getSelectedFiles();
			for(File chosenFile : chosenFiles) {
				Reader beliefBase = fileHandler.load(chosenFile);
				if(beliefBase == null) {
//					JOptionPane.showMessageDialog(view, "Cannot load '" + chosenFile.getPath() + "' using the file handler: '"
//							+ fileHandler.getClass().getName() + "'.");
				} else {
					try {
						model.setBeliefbase(beliefBase);
					} catch (ParseException e) {
//						JOptionPane.showMessageDialog(view, "Parser Error", "Parser Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} else if(ev.getSource() == view.btnAddRight) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setCurrentDirectory(fileHandler.getCurrentDiretory());
			chooser.setFileFilter(fileHandler.getFilter());
			chooser.setMultiSelectionEnabled(true);
			chooser.showOpenDialog(view);
			
			File[] choosenFiles = chooser.getSelectedFiles();
			for(File choosenFile : choosenFiles) {
				Reader beliefBase = fileHandler.load(choosenFile);
				if(beliefBase == null) {
//					JOptionPane.showMessageDialog(view, "Cannot load '" + choosenFile.getPath() + "' using the file handler: '"
//							+ fileHandler.getClass().getName() + "'.");
				} else {
					try {
						model.setNewBeliefs(beliefBase);
					} catch (ParseException e) {
//						JOptionPane.showMessageDialog(view, "Parser Error", "Parser Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} else if(ev.getSource() == view.btnRunRevision) {
			try {
				model.setBeliefbase(view.txtBeliefBase.getText());
			} catch (ParseException e) {
//				JOptionPane.showMessageDialog(view, "Parser Error", "Parser Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			}
			try {
				String bel = view.txtNewBeliefs.getText();
				model.setNewBeliefs(bel);
			} catch (ParseException e) {
//				JOptionPane.showMessageDialog(view, "Parser Error", "Parser Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			}
			model.setLeftOperator((BaseRevisionOperator<?>) view.cbOperatorLeft.getSelectedItem());
			model.setRightOperator((BaseRevisionOperator<?>) view.cbOperatorRight.getSelectedItem());
			
			model.runRevisions();

			model.calculateResultingAnswersets();
		}
	}

	@Override
	public void stateChanged(ChangeEvent ev) {
		
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {
		if(ev.getSource() == view.cbOperatorLeft) {
			model.setLeftOperator((BaseRevisionOperator<?>)view.cbOperatorLeft.getSelectedItem());
		} else if(ev.getSource() == view.cbOperatorRight) {
			model.setRightOperator((BaseRevisionOperator<?>)view.cbOperatorRight.getSelectedItem());
		}
	}
	
	/** Functional Test method: Only shows the view in a JFrame to test resize behavior. */
	public static void main(String [] args) {
		SimpleRevisionCompareModel model = new SimpleRevisionCompareModel();
		SimpleRevisionCompareView view = new SimpleRevisionCompareView();
		new SimpleRevisionComparePresenter(model, view);
		
		JFrame frame = new JFrame("Functional Test: Revision Compare View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(view);
		frame.setVisible(true);
		
		String c1 = "a.\n b.";
		String c2 = "c.\n d.";
		
		try {
			model.setBeliefbase(c1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			model.setNewBeliefs(c2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		model.addOperator(new MockOperator("Preference Handling"));
		model.addOperator(new MockOperator("Credibility Aware"));
		frame.pack();
		
	}
	
	/** does not try to load the file, returns null */
	private static class DefaultFileHandler implements FileHandler {
		@Override
		public Reader load(File file) {
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
	 * Mock revision operator for functional test.
	 * @author Tim Janus
	 */
	private static class MockOperator implements BaseRevisionOperator<Rule> {

		private String name;
		
		public MockOperator(String name) {
			this.name = name;
		}
		
		@Override
		public Collection<Rule> revise(Collection<Rule> base,
				Rule formula) {
			base.add(formula);
			return base;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
