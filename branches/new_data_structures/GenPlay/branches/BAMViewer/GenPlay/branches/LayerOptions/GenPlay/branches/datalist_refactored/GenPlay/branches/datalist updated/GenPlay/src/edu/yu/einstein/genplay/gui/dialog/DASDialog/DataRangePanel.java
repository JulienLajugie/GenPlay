/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.DASDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * Panel of a DAS dialog allowing the select the range of the data to retrieve
 * @author Julien Lajugie
 */
public class DataRangePanel extends JPanel {

	private static final long serialVersionUID = 1156023604789758319L; // generated ID
	private final NumberFormatter 			formatter;					// text formatter for the stop and start position input
	private final JRadioButton			jrbGenomeWide;				// radio button for the genome wide range
	private final JRadioButton			jrbUserSpecifiedRange;		// radio button user specified range
	private final JFormattedTextField	jtfUserStart;				// text filed for user specified start value
	private final JFormattedTextField	jtfUserStop;				// text filed for user specified stop value
	private final JComboBox				jcbChromosomeNumber;		// combo box to select choromosome number
	private final JRadioButton			jrbCurrentRange;			// radio button for current range
	private Chromosome					userSpecifiedChromo = null;	// user specified chromosome
	private int 						userSpecifiedStart = 0;		// user specified start
	private int 						userSpecifiedStop = 0;		// user specified stop
	private int 						dataRange =
			DASDialog.GENERATE_CURRENT_LIST;						// the type of the data range to be considered


	/**
	 * Creates an instance of {@link DataRangePanel}
	 */
	public DataRangePanel() {
		super();

		// create the formatter for the start and stop input boxes
		formatter = new NumberFormatter(NumberFormat.getInstance());
		formatter.setAllowsInvalid(false);

		// create start input
		jtfUserStart = new JFormattedTextField(formatter);
		jtfUserStart.setEditable(false);
		jtfUserStart.setEnabled(false);
		jtfUserStart.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (jtfUserStart.getText().equals("")) {
					jtfUserStart.setText("Start");
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				if (jtfUserStart.getText().equals("Start")) {
					jtfUserStart.setText("");
				}
			}
		});

		// create stop input
		jtfUserStop = new JFormattedTextField(formatter);
		jtfUserStop.setEditable(false);
		jtfUserStop.setEnabled(false);
		jtfUserStop.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (jtfUserStop.getText().equals("")) {
					jtfUserStop.setText("Stop");
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				if (jtfUserStop.getText().equals("Stop")) {
					jtfUserStop.setText("");
				}
			}
		});

		// combo box for the chromosome
		jcbChromosomeNumber= new JComboBox(ProjectManager.getInstance().getProjectChromosome().toArray());
		jcbChromosomeNumber.setEnabled(false);
		jcbChromosomeNumber.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedChromosomeChanged();
			}
		});

		// create the panel for the user specified range input
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(1, 3));
		content.add(jtfUserStart);
		content.add(jtfUserStop);
		content.add(jcbChromosomeNumber);

		// create genome wide radio button
		jrbGenomeWide = new JRadioButton("Genome Wide");
		jrbGenomeWide.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dataRangeChanged();
			}
		});

		// create current range radio button
		jrbCurrentRange = new JRadioButton("Current Range");
		jrbCurrentRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dataRangeChanged();
			}
		});

		// create user specified radio button
		jrbUserSpecifiedRange = new JRadioButton("From");
		jrbUserSpecifiedRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dataRangeChanged();
			}
		});

		// add the buttons to a button group
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(jrbGenomeWide);
		radioGroup.add(jrbCurrentRange);
		radioGroup.add(jrbUserSpecifiedRange);
		radioGroup.setSelected(jrbCurrentRange.getModel(), true);

		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrbGenomeWide, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrbCurrentRange, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrbUserSpecifiedRange, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(content, c);

		setBorder(BorderFactory.createTitledBorder("Select Data Range"));
	}


	/**
	 * Method called when the selected data range changes
	 */
	protected void dataRangeChanged() {
		if (jrbCurrentRange.isSelected()) {
			dataRange = DASDialog.GENERATE_CURRENT_LIST;
			jtfUserStop.setEnabled(false);
			jtfUserStart.setEnabled(false);
			jcbChromosomeNumber.setEnabled(false);
		}
		else if (jrbGenomeWide.isSelected()) {
			dataRange = DASDialog.GENERATE_GENOMEWIDE_LIST;
			jtfUserStop.setEnabled(false);
			jtfUserStart.setEnabled(false);
			jcbChromosomeNumber.setEnabled(false);
		}
		else if(jrbUserSpecifiedRange.isSelected()) {
			dataRange = DASDialog.GENERATE_USER_SPECIFIED_LIST;
			jtfUserStop.setEnabled(true);
			jtfUserStart.setEnabled(true);
			jtfUserStart.setEditable(true);
			jtfUserStop.setEditable(true);
			jcbChromosomeNumber.setEnabled(true);
			userSpecifiedChromo = (Chromosome) jcbChromosomeNumber.getSelectedItem();
			formatter.setMinimum(new Long(0));
			formatter.setMaximum(new Long(userSpecifiedChromo.getLength()));
		}
	}


	/**
	 * @return the genome window specified by the user
	 */
	public GenomeWindow getUserSpecifiedGenomeWindow() {
		if(jtfUserStart.isEnabled() && jtfUserStop.isEnabled()) {
			userSpecifiedStart = ((Number)jtfUserStart.getValue()).intValue();
			userSpecifiedStop = ((Number)jtfUserStop.getValue()).intValue();
			userSpecifiedChromo = (Chromosome) jcbChromosomeNumber.getSelectedItem();
			return new GenomeWindow(userSpecifiedChromo, userSpecifiedStart, userSpecifiedStop);
		} else {
			return null;
		}
	}


	/**
	 * @return dataRange(GENERATE_GENOMEWIDE_LIST or GENERATE_CURRENT_LIST or GENERATE_USER_SPECIFIED_LIST)
	 */
	public final int getDataRange() {
		return dataRange;
	}


	/**
	 * Method called when the selected Chromozome changes
	 */
	protected void selectedChromosomeChanged() {
		userSpecifiedChromo = (Chromosome) (jcbChromosomeNumber.getSelectedItem());
		formatter.setMaximum(new Long(userSpecifiedChromo.getLength()));
		if (((Long) jtfUserStop.getValue()) > userSpecifiedChromo.getLength()) {
			jtfUserStop.setValue((long) userSpecifiedChromo.getLength());
		}
	}
}
