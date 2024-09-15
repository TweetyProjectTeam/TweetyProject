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
package org.tweetyproject.beliefdynamics.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import org.tweetyproject.beliefdynamics.BaseRevisionOperator;
import org.tweetyproject.beliefdynamics.CredibilityRevision;
import org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator;
import org.tweetyproject.commons.Formula;

/**
 * The default view for a revision compare. This class is responsible to create
 * the hierarchy of
 * widgets and delegates its inputs to the RevisionComparePresenter. Everytime
 * an update occurs
 * the output of the revision is updated.
 *
 * @author Tim Janus
 */
public class RevisionCompareView extends JPanel implements PropertyChangeListener {

	/** kill warning */
	private static final long serialVersionUID = 5699544277473453367L;

	/** Dropdown for selecting the left-hand side revision operator. */
	protected JComboBox<BaseRevisionOperator<?>> cbOperatorLeft;

	/** Dropdown for selecting the right-hand side revision operator. */
	protected JComboBox<BaseRevisionOperator<?>> cbOperatorRight;

	/** Checkbox to enable/disable iterative revision for the left operator. */
	protected JCheckBox checkIterativeLeft;

	/** Checkbox to enable/disable iterative revision for the right operator. */
	protected JCheckBox checkIterativeRight;

	/** Button to move an item up in the list of belief bases. */
	protected JButton btnUp;

	/** Button to move an item down in the list of belief bases. */
	protected JButton btnDown;

	/** Button to add a belief base to the list. */
	protected JButton btnAdd;

	/** Button to remove a belief base from the list. */
	protected JButton btnRemove;

	/** Slider to adjust the step size for the revision process. */
	protected JSlider sliderStep;

	/**
	 * Text area to display the results of the left-hand side revision operation.
	 */
	protected JTextArea txtResultLeft;

	/**
	 * Text area to display the results of the right-hand side revision operation.
	 */
	protected JTextArea txtResultRight;

	/** Text area to display the list of belief bases. */
	protected JTextArea txtBeliefBases;

	/** Model for the list of belief bases, storing collections of formulas. */
	private DefaultListModel<Collection<? extends Formula>> lstModel = new DefaultListModel<Collection<? extends Formula>>();

	/** List component to display the belief bases. */
	protected JList<Collection<? extends Formula>> lstBeliefBases;

	/** Default Ctor: Creates the view */
	public RevisionCompareView() {
		createUpperBar();
		createCompareView();
		createBeliefbaseControl();
	}

	/**
	 * Helper method: Creates the belief base control panel, there one can change
	 * the ordering
	 * of the belief bases for the revision and add or remove belief bases. It is at
	 * the bottom
	 * of this view.
	 */
	private void createBeliefbaseControl() {
		JPanel actPanel = new JPanel();
		actPanel.setLayout(new BorderLayout());
		actPanel.add(new JLabel("Textual belief base representation"), BorderLayout.NORTH);
		txtBeliefBases = new JTextArea(10, 20);
		actPanel.add(new JScrollPane(txtBeliefBases), BorderLayout.CENTER);

		JPanel lstPanel = new JPanel();
		lstPanel.setLayout(new BorderLayout());
		lstBeliefBases = new JList<Collection<? extends Formula>>();
		lstBeliefBases.setModel(lstModel);
		lstPanel.add(lstBeliefBases, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));

		btnUp = new JButton("Up");
		btnDown = new JButton("Down");
		btnAdd = new JButton("Load...");
		btnRemove = new JButton("Remove");

		btnPanel.add(btnUp);
		btnPanel.add(btnDown);
		btnPanel.add(btnAdd);
		btnPanel.add(btnRemove);

		lstPanel.add(btnPanel, BorderLayout.WEST);
		actPanel.add(lstPanel, BorderLayout.SOUTH);

		this.add(actPanel, BorderLayout.SOUTH);
	}

	/**
	 * Helper method: Creates the controls to show the comparison between the two
	 * selected revision methods
	 * it is located at the center of this view.
	 */
	private void createCompareView() {
		JPanel parentPanel = new JPanel();
		parentPanel.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(new JLabel("Step Slider"), BorderLayout.NORTH);
		sliderStep = new JSlider(0, 1);
		topPanel.add(sliderStep, BorderLayout.CENTER);
		topPanel.add(new JLabel("Comparison: revision result"), BorderLayout.SOUTH);
		parentPanel.add(topPanel, BorderLayout.NORTH);

		JPanel txtPanel = new JPanel();
		txtPanel.setLayout(new GridLayout(1, 2, 5, 5));
		txtResultLeft = new JTextArea(10, 20);
		txtResultLeft.setEditable(false);
		txtPanel.add(txtResultLeft);

		txtResultRight = new JTextArea(10, 20);
		txtResultRight.setEditable(false);
		txtPanel.add(txtResultRight);

		parentPanel.add(new JScrollPane(txtPanel), BorderLayout.CENTER);
		this.add(parentPanel, BorderLayout.CENTER);
	}

	/**
	 * Helper method: Creates the control bar at the top of this view which allows
	 * the
	 * user to change the revision operators used for comparison.
	 */
	private void createUpperBar() {
		JPanel actPanel = new JPanel();

		actPanel.add(new JLabel("Left Revisions Operator:"));
		cbOperatorLeft = new JComboBox<BaseRevisionOperator<?>>();
		checkIterativeLeft = new JCheckBox("i", true);
		actPanel.add(checkIterativeLeft);
		actPanel.add(cbOperatorLeft);

		actPanel.add(new JLabel("Right Revisions Operator:"));
		cbOperatorRight = new JComboBox<BaseRevisionOperator<?>>();
		checkIterativeRight = new JCheckBox("i", true);
		actPanel.add(checkIterativeRight);
		actPanel.add(cbOperatorRight);

		this.setLayout(new BorderLayout());
		this.add(actPanel, BorderLayout.NORTH);
	}

	/**
	 * Reacts to property change events to keep the view up to date. The presenter
	 * is
	 * responsible to register the view at the correct data-model.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("leftOperator")) {
			cbOperatorLeft.setSelectedItem(evt.getNewValue());
			checkIterativeLeft.setEnabled(evt.getNewValue() instanceof CredibilityRevision<?>);
			String content = updateRevision(evt.getNewValue(), checkIterativeLeft.isSelected());
			txtResultLeft.setText(content);
		} else if (evt.getPropertyName().equals("rightOperator")) {
			cbOperatorRight.setSelectedItem(evt.getNewValue());
			checkIterativeRight.setEnabled(evt.getNewValue() instanceof CredibilityRevision<?>);
			String content = updateRevision(evt.getNewValue(), checkIterativeRight.isSelected());
			txtResultRight.setText(content);
		} else if (evt.getPropertyName().equals("beliefBases")) {
			IndexedPropertyChangeEvent ipce = (IndexedPropertyChangeEvent) evt;
			@SuppressWarnings("unchecked")
			Collection<? extends Formula> n = (Collection<? extends Formula>) ipce.getNewValue();

			if (ipce.getNewValue() == null) {
				lstModel.removeElement(ipce.getOldValue());
			} else if (ipce.getOldValue() == null) {
				lstModel.addElement(n);
			} else {
				lstModel.set(ipce.getIndex(), n);
			}
			beliefbasesUpdated();
			updateRevision();
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
		} else if (evt.getPropertyName() == "leftIterative") {
			boolean it = (Boolean) evt.getNewValue();
			checkIterativeLeft.setSelected(it);
			if (cbOperatorLeft.getSelectedItem() != null) {
				String content = updateRevision(cbOperatorLeft.getSelectedItem(), it);
				txtResultLeft.setText(content);
			}
		} else if (evt.getPropertyName() == "rightIterative") {
			boolean it = (Boolean) evt.getNewValue();
			checkIterativeRight.setSelected(it);
			if (cbOperatorRight.getSelectedItem() != null) {
				String content = updateRevision(cbOperatorRight.getSelectedItem(), it);
				txtResultRight.setText(content);
			}
		}
	}

	/**
	 * Helper method. Updates the text field representing all the loaded belief
	 * bases in the
	 * order they revision will occur.
	 */
	private void beliefbasesUpdated() {
		String txtRepr = "";
		for (int i = 0; i < lstBeliefBases.getModel().getSize(); ++i) {
			Collection<?> col = (Collection<?>) lstBeliefBases.getModel().getElementAt(i);
			for (Object obj : col) {
				txtRepr += obj.toString() + "\n";
			}
			txtRepr += "\n";
		}
		txtBeliefBases.setText(txtRepr);
		sliderStep.setMaximum(lstModel.getSize());
	}

	/**
	 * Helper method: Updates the revision on both operators because the data set
	 * has changed.
	 */
	private void updateRevision() {
		if (cbOperatorLeft.getSelectedItem() != null) {
			String content = updateRevision(cbOperatorLeft.getSelectedItem(),
					checkIterativeLeft.isSelected());
			txtResultLeft.setText(content);
		}

		if (cbOperatorRight.getSelectedItem() != null) {
			String content = updateRevision(cbOperatorRight.getSelectedItem(),
					checkIterativeRight.isSelected());
			txtResultRight.setText(content);
		}
	}

	/**
	 * Helper method: got called if the revision process needs an update: It
	 * performs the revision for the left and right operator
	 * and saves the result to the result text areas.
	 *
	 * @param op some object
	 * @param b  some boolean
	 * @return a string
	 */
	private String updateRevision(Object op, boolean b) {
		if (lstModel.getSize() < 2)
			return "";

		Map<Integer, Collection<? extends Formula>> revisionSteps = new HashMap<Integer, Collection<? extends Formula>>();
		if (op instanceof CredibilityRevision<?> && !b) {
			CredibilityRevision<?> cop = (CredibilityRevision<?>) op;
			revisionSteps = revision(cop);
		} else if (op instanceof MultipleBaseRevisionOperator<?>) {
			MultipleBaseRevisionOperator<?> cop = (MultipleBaseRevisionOperator<?>) op;
			revisionSteps = revision(cop);
		} else {
			BaseRevisionOperator<? extends Formula> cop = (BaseRevisionOperator<?>) op;
			revisionSteps = revision(cop);
		}

		String reval = "";
		for (Integer key : revisionSteps.keySet()) {
			reval += key + ".\n";
			for (Formula f : revisionSteps.get(key)) {
				reval += f.toString() + "\n";
			}
			reval += "\n";
		}
		return reval;
	}

	/**
	 * Helper method: Runs a revision using the credibility revision operator
	 * interface. This means an ordered list
	 * of programs can be revised in one step.
	 *
	 * @param op The operator used to perform the revision.
	 * @return A map of steps of the revision process to the belief bases.
	 * @todo Kill warnings, not easy cause '?' does not works with revise
	 *       method(Collection<T>, T)
	 */
	@SuppressWarnings("unchecked")
	private <T extends Formula> Map<Integer, Collection<? extends Formula>> revision(CredibilityRevision<T> op) {
		Map<Integer, Collection<? extends Formula>> reval = new HashMap<Integer, Collection<? extends Formula>>();
		List<Collection<T>> orderedList = new LinkedList<Collection<T>>();
		for (int i = 0; i < lstModel.getSize(); ++i)
			orderedList.add(new HashSet<T>((Collection<T>) lstModel.get(i)));
		reval.put(1, op.revise(orderedList));
		return reval;
	}

	/**
	 * Helper method: Runs a revision of using multiple belief bases with the
	 * MultipleBaseRevisionOperator. It simulates
	 * the revision by iteratively revise the different belief bases
	 *
	 * @param op The operator used to perform the revision.
	 * @return A map of steps of the revision process to the belief bases.
	 * @todo Kill warnings, not easy cause '?' does not works with revise
	 *       method(Collection<T>, T)
	 */
	@SuppressWarnings("unchecked")
	private <T extends Formula> Map<Integer, Collection<? extends Formula>> revision(
			MultipleBaseRevisionOperator<T> op) {
		Map<Integer, Collection<? extends Formula>> reval = new HashMap<Integer, Collection<? extends Formula>>();

		Collection<T> base = new HashSet<T>((Collection<T>) lstModel.get(0));
		for (int i = 1; i < lstModel.getSize(); i++) {
			Collection<T> extension = new HashSet<T>((Collection<T>) lstModel.get(i));
			base = op.revise(base, extension);
			reval.put(i, base);
			base = new HashSet<T>(base);
		}

		return reval;
	}

	/**
	 * Helper method: Runs a revision on BaseRevisionOperator interfaces which only
	 * allows the revision with one
	 * formula. It simulates the revision of two belief bases by revising the
	 * formulas of the second belief base
	 * one after another.
	 *
	 * @param op The operator used to perform the revision
	 * @return A map of steps of the revision process to the belief bases.
	 * @todo Kill warnings, not easy cause '?' does not works with revise
	 *       method(Collection<T>, T)
	 */
	@SuppressWarnings("unchecked")
	private <T extends Formula> Map<Integer, Collection<? extends Formula>> revision(BaseRevisionOperator<T> op) {
		Map<Integer, Collection<? extends Formula>> reval = new HashMap<Integer, Collection<? extends Formula>>();
		Collection<T> base = new HashSet<T>((Collection<T>) lstModel.get(0));
		for (int i = 1; i < lstModel.getSize(); i++) {
			Collection<T> extension = new HashSet<T>((Collection<T>) lstModel.get(i));
			for (T f : extension) {
				base = op.revise(base, f);
			}
			reval.put(i, base);
			base = new HashSet<T>(base);
		}
		return reval;
	}
}
