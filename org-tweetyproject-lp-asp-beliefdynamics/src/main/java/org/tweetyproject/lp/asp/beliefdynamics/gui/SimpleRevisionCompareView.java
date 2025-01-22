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
package org.tweetyproject.lp.asp.beliefdynamics.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tweetyproject.beliefdynamics.BaseRevisionOperator;
import org.tweetyproject.lp.asp.semantics.AnswerSet;

/**
 * The default view for a revision compare. This class is responsible to create
 * the hierarchy of
 * widgets and delegates its inputs to the RevisionComparePresenter. Everytime
 * an update occurs
 * the output of the revision is updated.
 * This class is based in spirit on the RevisionCompareView in the
 * beliefdynamics.gui package.
 *
 * @author Sebastian Homann
 * @author Tim Janus
 */
public class SimpleRevisionCompareView extends JPanel implements PropertyChangeListener {

	/** kill warning */
	private static final long serialVersionUID = 5699544277473453367L;

	/**
	 * GUI components for managing and displaying revision operations and results.
	 * <p>
	 * This class contains various Swing components used for selecting revision
	 * operators, executing revisions,
	 * and displaying results and answer sets.
	 * </p>
	 */
	protected JComboBox<BaseRevisionOperator<?>> cbOperatorLeft;

	/**
	 * ComboBox for selecting the revision operator to be used for the left
	 * operation.
	 * <p>
	 * This component allows users to choose from a list of available
	 * `BaseRevisionOperator` instances for the left side of the revision process.
	 * </p>
	 */
	protected JComboBox<BaseRevisionOperator<?>> cbOperatorRight;

	/**
	 * Button to add the selected revision operator for the left operation.
	 * <p>
	 * When clicked, this button triggers the addition of the currently selected
	 * `BaseRevisionOperator` in `cbOperatorLeft`.
	 * </p>
	 */
	protected JButton btnAddLeft;

	/**
	 * Button to add the selected revision operator for the right operation.
	 * <p>
	 * When clicked, this button triggers the addition of the currently selected
	 * `BaseRevisionOperator` in `cbOperatorRight`.
	 * </p>
	 */
	protected JButton btnAddRight;

	/**
	 * Button to execute the revision process.
	 * <p>
	 * This button initiates the revision process based on the selected operators
	 * and inputs provided.
	 * </p>
	 */
	protected JButton btnRunRevision;

	/**
	 * TextArea for displaying the result of the left revision operation.
	 * <p>
	 * This component is used to show the outcome or results from the revision
	 * process executed with the left-side operator.
	 * </p>
	 */
	protected JTextArea txtResultLeft;

	/**
	 * TextArea for displaying the result of the right revision operation.
	 * <p>
	 * This component is used to show the outcome or results from the revision
	 * process executed with the right-side operator.
	 * </p>
	 */
	protected JTextArea txtResultRight;

	/**
	 * TextArea for displaying or editing the belief base.
	 * <p>
	 * This component is used to input or display the current belief base used in
	 * the revision operations.
	 * </p>
	 */
	protected JTextArea txtBeliefBase;

	/**
	 * TextArea for displaying or editing new beliefs to be used in the revision.
	 * <p>
	 * This component is used to input or display new beliefs that will be used to
	 * update the belief base.
	 * </p>
	 */
	protected JTextArea txtNewBeliefs;

	/**
	 * List for displaying answer sets resulting from the left revision operation.
	 * <p>
	 * This component shows the collection of `AnswerSet` objects resulting from the
	 * revision operation performed with the left-side operator.
	 * </p>
	 */
	protected JList<AnswerSet> lstLeftAnswerSets;

	/**
	 * List for displaying answer sets resulting from the right revision operation.
	 * <p>
	 * This component shows the collection of `AnswerSet` objects resulting from the
	 * revision operation performed with the right-side operator.
	 * </p>
	 */
	protected JList<AnswerSet> lstRightAnswerSets;

	/** Default Ctor: Creates the view */
	public SimpleRevisionCompareView() {
		this.setLayout(new BorderLayout());
		this.add(guiGetOperatorControls(), BorderLayout.NORTH);

		// input
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		centerPanel.add(guiGetInput());
		centerPanel.add(guiGetRevisionResult());
		centerPanel.add(guiGetAnswerSetView());
		this.add(centerPanel, BorderLayout.CENTER);

		this.add(guiGetButtons(), BorderLayout.SOUTH);
	}

	private JPanel guiGetAnswerSetView() {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		JPanel lblPanel = new JPanel();
		lblPanel.setLayout(new GridLayout(1, 2, 5, 5));
		lblPanel.add(new JLabel("Resulting answer sets"));
		lblPanel.add(new JLabel("Resulting answer sets"));
		result.add(lblPanel, BorderLayout.NORTH);

		JPanel txtPanel = new JPanel();
		txtPanel.setLayout(new GridLayout(1, 2, 5, 5));
		lstLeftAnswerSets = new JList<AnswerSet>();
		lstLeftAnswerSets.setPreferredSize(new Dimension(10, 20));
		lstRightAnswerSets = new JList<AnswerSet>();
		lstRightAnswerSets.setPreferredSize(new Dimension(10, 20));
		txtPanel.add(lstLeftAnswerSets);
		txtPanel.add(lstRightAnswerSets);
		result.add(new JScrollPane(txtPanel), BorderLayout.CENTER);
		result.setPreferredSize(new Dimension(20, 80));
		return result;
	}

	private JPanel guiGetRevisionResult() {
		JPanel parentPanel = new JPanel();
		parentPanel.setLayout(new BorderLayout());

		JPanel lblPanel = new JPanel();
		lblPanel.setLayout(new GridLayout(1, 2, 5, 5));
		lblPanel.add(new JLabel("Left revision result"));
		lblPanel.add(new JLabel("Right revision result"));
		parentPanel.add(lblPanel, BorderLayout.NORTH);

		JPanel txtPanel = new JPanel();
		txtPanel.setLayout(new GridLayout(1, 2, 5, 5));
		txtResultLeft = new JTextArea(10, 20);
		txtResultLeft.setEditable(false);
		txtPanel.add(txtResultLeft);

		txtResultRight = new JTextArea(10, 20);
		txtResultRight.setEditable(false);
		txtPanel.add(txtResultRight);

		parentPanel.add(new JScrollPane(txtPanel), BorderLayout.CENTER);
		return parentPanel;
	}

	private JPanel guiGetInput() {
		JPanel actPanel = new JPanel();
		actPanel.setLayout(new BorderLayout());
		JPanel lblPanel = new JPanel();
		lblPanel.setLayout(new GridLayout(1, 2, 5, 5));
		lblPanel.add(new JLabel("Beliefbase"));
		lblPanel.add(new JLabel("New beliefs"));
		actPanel.add(lblPanel, BorderLayout.NORTH);

		JPanel txtPanel = new JPanel();
		txtPanel.setLayout(new GridLayout(1, 2, 5, 5));

		txtBeliefBase = new JTextArea(10, 40);
		txtBeliefBase.setEditable(true);
		txtPanel.add(txtBeliefBase);

		txtNewBeliefs = new JTextArea(10, 40);
		txtNewBeliefs.setEditable(true);
		txtPanel.add(txtNewBeliefs);

		actPanel.add(new JScrollPane(txtPanel), BorderLayout.CENTER);

		return actPanel;
	}

	private JPanel guiGetOperatorControls() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
		result.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		result.add(new JLabel("Left revision operator:"));
		result.add(Box.createRigidArea(new Dimension(10, 0)));
		cbOperatorLeft = new JComboBox<BaseRevisionOperator<?>>();
		result.add(cbOperatorLeft);

		result.add(Box.createRigidArea(new Dimension(10, 0)));
		result.add(new JLabel("Right revision operator:"));
		result.add(Box.createRigidArea(new Dimension(10, 0)));
		cbOperatorRight = new JComboBox<BaseRevisionOperator<?>>();
		result.add(cbOperatorRight);
		return result;
	}

	private JPanel guiGetButtons() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
		result.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		btnAddLeft = new JButton("Load beliefBase");
		btnAddRight = new JButton("Load new beliefs");
		btnRunRevision = new JButton("Run Revision");
		btnRunRevision.setMnemonic(KeyEvent.VK_R);

		result.add(btnAddLeft);
		result.add(Box.createRigidArea(new Dimension(10, 0)));
		result.add(btnAddRight);
		result.add(Box.createHorizontalGlue());
		result.add(btnRunRevision);
		return result;
	}

	/**
	 * Reacts to property change events to keep the view up to date. The presenter
	 * is
	 * responsible to register the view at the correct data-model.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("leftOperator")) {
			cbOperatorLeft.setSelectedItem(evt.getNewValue());
		} else if (evt.getPropertyName().equals("rightOperator")) {
			cbOperatorRight.setSelectedItem(evt.getNewValue());
		} else if (evt.getPropertyName().equals("beliefBase")) {
			PropertyChangeEvent ipce = (PropertyChangeEvent) evt;
			String newValue = (String) ipce.getNewValue();

			if (newValue == null) {
				txtBeliefBase.setText("");
			} else {
				txtBeliefBase.setText(newValue);
			}
		} else if (evt.getPropertyName().equals("newbeliefs")) {
			PropertyChangeEvent ipce = (PropertyChangeEvent) evt;
			String newValue = (String) ipce.getNewValue();

			if (newValue == null) {
				txtNewBeliefs.setText("");
			} else {
				txtNewBeliefs.setText(newValue);
			}
		} else if (evt.getPropertyName() == "selectableOperators") {
			if (evt.getOldValue() == null) {
				BaseRevisionOperator<?> op = (BaseRevisionOperator<?>) evt.getNewValue();
				cbOperatorLeft.addItem(op);
				cbOperatorRight.addItem(op);
			} else if (evt.getNewValue() == null) {
				BaseRevisionOperator<?> op = (BaseRevisionOperator<?>) evt.getOldValue();
				cbOperatorLeft.removeItem(op);
				cbOperatorRight.removeItem(op);
			}
		} else if (evt.getPropertyName() == "leftresult") {
			Collection<?> result = (Collection<?>) evt.getNewValue();
			String newResult = "";
			for (Object entry : result) {
				newResult += entry + "\n";
			}
			txtResultLeft.setText(newResult);
		} else if (evt.getPropertyName() == "rightresult") {
			Collection<?> result = (Collection<?>) evt.getNewValue();
			String newResult = "";
			for (Object entry : result) {
				newResult += entry + "\n";
			}
			txtResultRight.setText(newResult);
		} else if (evt.getPropertyName() == "error") {
			JOptionPane.showMessageDialog(this, evt.getNewValue(), "Error", JOptionPane.ERROR_MESSAGE);
		} else if (evt.getPropertyName() == "leftASL") {
			Collection<AnswerSet> asl = (Collection<AnswerSet>) evt.getNewValue();
			DefaultListModel<AnswerSet> model = new DefaultListModel<AnswerSet>();
			for (AnswerSet as : asl) {
				model.addElement(as);
			}
			lstLeftAnswerSets.setModel(model);
		} else if (evt.getPropertyName() == "rightASL") {
			Collection<AnswerSet> asl = (Collection<AnswerSet>) evt.getNewValue();
			DefaultListModel<AnswerSet> model = new DefaultListModel<AnswerSet>();
			for (AnswerSet as : asl) {
				model.addElement(as);
			}
			lstRightAnswerSets.setModel(model);
		}
	}
}
