/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.yu.einstein.genplay.util.Images;

/**
 * A dialog box for Distance Calculation between Base Pairs
 * @author Chirag Gorasia
 * @version 0.1
 */
public class DistanceCalculatorDialog extends JDialog {

	private static final long serialVersionUID = 6243891166900722503L;
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 300;

	private final JPanel jpstrandSelection;
	private final JPanel jprelAbsSelection;
	private final JPanel jptrack1Panel;
	private final JPanel jptrack2Panel;

	private final JRadioButton positiveStrand;
	private final JRadioButton negativeStrand;
	private final JRadioButton bothStrand;

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 1;

	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 0;

	private static final int POSITIVE_STRAND = 1;
	private static final int NEGATIVE_STRAND = 2;
	private static final int BOTH_STRAND = 3;

	private final JRadioButton relative;
	private final JRadioButton absolute;

	private static final int RELATIVE = 1;
	private static final int ABSOLUTE = 2;

	private final JRadioButton track1Start;
	private final JRadioButton track1Middle;
	private final JRadioButton track1Stop;

	private static final int START_1 = 1;
	private static final int MIDDLE_1 = 2;
	private static final int STOP_1 = 3;

	private final JRadioButton track2Start;
	private final JRadioButton track2Middle;
	private final JRadioButton track2Stop;

	private final JButton jbOK;
	private final JButton jbCancel;

	private static final int START_2 = 1;
	private static final int MIDDLE_2 = 2;
	private static final int STOP_2 = 3;

	private int strand;
	private int relabs;
	private int t1pos;
	private int t2pos;

	private int selectionFlag;
	private int approved = CANCEL_OPTION;


	/**
	 * Creates an instance of {@link DistanceCalculatorDialog}
	 */
	public DistanceCalculatorDialog() {
		super();

		jpstrandSelection = new JPanel();
		jprelAbsSelection = new JPanel();
		jptrack1Panel = new JPanel();
		jptrack2Panel = new JPanel();

		jpstrandSelection.setPreferredSize(new Dimension(150,60));
		jprelAbsSelection.setPreferredSize(new Dimension(150,60));
		jptrack1Panel.setPreferredSize(new Dimension(150,60));
		jptrack2Panel.setPreferredSize(new Dimension(150,60));

		positiveStrand = new JRadioButton("Positive Strand");
		negativeStrand = new JRadioButton("Negative Strand");
		bothStrand = new JRadioButton("Both Strands");

		relative = new JRadioButton("Relative");
		absolute = new JRadioButton("Absolute");

		track1Start = new JRadioButton("Start");
		track1Middle = new JRadioButton("Middle");
		track1Stop = new JRadioButton("Stop");

		track2Start = new JRadioButton("Start");
		track2Middle = new JRadioButton("Middle");
		track2Stop = new JRadioButton("Stop");

		jbOK = new JButton("OK");
		jbOK.setPreferredSize(new Dimension(100, 30));
		jbOK.setDefaultCapable(true);
		jbOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getStrand() == POSITIVE_STRAND) {
					if (getT1pos() == START_1) {
						if (getT2pos() == START_2) {
							setSelectionFlag(1);
						} else if (getT2pos() == MIDDLE_2) {
							setSelectionFlag(2);
						} else {
							setSelectionFlag(3);
						}
					} else if (getT1pos() == MIDDLE_1) {
						if (getT2pos() == START_2) {
							setSelectionFlag(4);
						} else if (getT2pos() == MIDDLE_2) {
							setSelectionFlag(5);
						} else {
							setSelectionFlag(6);
						}
					} else {
						if (getT2pos() == START_2) {
							setSelectionFlag(7);
						} else if (getT2pos() == MIDDLE_2) {
							setSelectionFlag(8);
						} else {
							setSelectionFlag(9);
						}
					}
				} else if (getStrand() == NEGATIVE_STRAND) {
					if (getT1pos() == START_1) {
						if (getT2pos() == START_2) {
							setSelectionFlag(10);
						} else if (getT2pos() == MIDDLE_2) {
							setSelectionFlag(11);
						} else {
							setSelectionFlag(12);
						}
					} else if (getT1pos() == MIDDLE_1) {
						if (getT2pos() == START_2) {
							setSelectionFlag(13);
						} else if (getT2pos() == MIDDLE_2) {
							setSelectionFlag(14);
						} else {
							setSelectionFlag(15);
						}
					} else {
						if (getT2pos() == START_2) {
							setSelectionFlag(16);
						} else if (getT2pos() == MIDDLE_2) {
							setSelectionFlag(17);
						} else {
							setSelectionFlag(18);
						}
					}
				} else {
					if (getRelabs() == RELATIVE) {
						setSelectionFlag(19);
					} else {
						setSelectionFlag(20);
					}
				}
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectionFlag(1);
				setVisible(false);
			}
		});

		positiveStrand.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				strandChanged();
			}
		});

		negativeStrand.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				strandChanged();
			}
		});

		bothStrand.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				strandChanged();
			}
		});

		ButtonGroup bg = new ButtonGroup();
		bg.add(positiveStrand);
		bg.add(negativeStrand);
		bg.add(bothStrand);
		bg.setSelected(positiveStrand.getModel(), true);

		relative.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				relabsChanged();
			}
		});

		absolute.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				relabsChanged();
			}
		});

		bg = new ButtonGroup();
		bg.add(relative);
		bg.add(absolute);
		bg.setSelected(relative.getModel(), true);

		track1Start.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				t1posChanged();
			}
		});

		track1Middle.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				t1posChanged();
			}
		});

		track1Stop.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				t1posChanged();
			}
		});

		bg = new ButtonGroup();
		bg.add(track1Start);
		bg.add(track1Middle);
		bg.add(track1Stop);
		bg.setSelected(track1Start.getModel(), true);

		track2Start.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				t2posChanged();
			}
		});

		track2Middle.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				t2posChanged();
			}
		});

		track2Stop.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				t2posChanged();
			}
		});

		bg = new ButtonGroup();
		bg.add(track2Start);
		bg.add(track2Middle);
		bg.add(track2Stop);
		bg.setSelected(track2Start.getModel(), true);

		jpstrandSelection.setLayout(new GridBagLayout());
		//jpstrandSelection.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		jpstrandSelection.setBorder(BorderFactory.createTitledBorder("Strand"));
		//strandSelection.setBorder(BorderFactory.createLineBorder(Color.black));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		jpstrandSelection.add(positiveStrand,c);
		c.gridy = 1;
		jpstrandSelection.add(negativeStrand,c);
		c.gridy = 2;
		jpstrandSelection.add(bothStrand,c);
		jpstrandSelection.setVisible(true);

		jprelAbsSelection.setLayout(new GridBagLayout());
		//jprelAbsSelection.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		jprelAbsSelection.setBorder(BorderFactory.createTitledBorder("Relative/Absolute"));
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		jprelAbsSelection.add(relative,c);
		c.gridy = 1;
		jprelAbsSelection.add(absolute,c);
		jprelAbsSelection.setEnabled(false);
		jprelAbsSelection.setVisible(false);

		jptrack1Panel.setLayout(new GridBagLayout());
		//jptrack1Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		jptrack1Panel.setBorder(BorderFactory.createTitledBorder("Track 1 position"));
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		jptrack1Panel.add(track1Start,c);
		c.gridy = 1;
		jptrack1Panel.add(track1Middle,c);
		c.gridy = 2;
		jptrack1Panel.add(track1Stop,c);
		jptrack1Panel.setEnabled(false);
		jptrack1Panel.setVisible(true);

		jptrack2Panel.setLayout(new GridBagLayout());
		//jptrack2Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		jptrack2Panel.setBorder(BorderFactory.createTitledBorder("Track 2 position"));
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		jptrack2Panel.add(track2Start,c);
		c.gridy = 1;
		jptrack2Panel.add(track2Middle,c);
		c.gridy = 2;
		jptrack2Panel.add(track2Stop,c);
		jptrack2Panel.setEnabled(false);
		jptrack2Panel.setVisible(true);

		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jpstrandSelection, c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jprelAbsSelection, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jptrack1Panel, c);

		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jptrack2Panel, c);

		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbOK, c);

		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, c);

		pack();
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Distance Calculator Paramters");
		setIconImages(Images.getApplicationImages());
		setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().x - (WINDOW_WIDTH/2), GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().y - (WINDOW_HEIGHT/2));
		setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setResizable(false);
		setVisible(false);
		jbOK.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOK);
	}

	/**
	 * @param strand the strand to set
	 */
	public void setStrand(int strand) {
		this.strand = strand;
	}


	/**
	 * @return the strand
	 */
	public int getStrand() {
		return strand;
	}

	/**
	 * @param relabs the relabs to set
	 */
	public void setRelabs(int relabs) {
		this.relabs = relabs;
	}

	/**
	 * @return the relabs
	 */
	public int getRelabs() {
		return relabs;
	}


	/**
	 * @param t1pos the t1pos to set
	 */
	public void setT1pos(int t1pos) {
		this.t1pos = t1pos;
	}

	/**
	 * @return the t1pos
	 */
	public int getT1pos() {
		return t1pos;
	}

	/**
	 * @param t2pos the t2pos to set
	 */
	public void setT2pos(int t2pos) {
		this.t2pos = t2pos;
	}

	/**
	 * @return the t2pos
	 */
	public int getT2pos() {
		return t2pos;
	}


	/**
	 * Method to handle changes in the strand selection
	 */
	private void strandChanged() {
		//System.out.println("Strand: " + getStrand());
		if (positiveStrand.isSelected()) {
			setStrand(POSITIVE_STRAND);
			jprelAbsSelection.setEnabled(false);
			jprelAbsSelection.setVisible(false);
		} else if (negativeStrand.isSelected()) {
			setStrand(NEGATIVE_STRAND);
			jprelAbsSelection.setEnabled(false);
			jprelAbsSelection.setVisible(false);
		} else {
			setStrand(BOTH_STRAND);
			jprelAbsSelection.setVisible(true);
			jprelAbsSelection.setEnabled(true);
		}
	}

	/**
	 * Method to handle changes in the rel/abs selection
	 */
	private void relabsChanged() {
		//System.out.println("RelAbs: " + getRelabs());
		if (relative.isSelected()) {
			setRelabs(RELATIVE);
		} else {
			setRelabs(ABSOLUTE);
		}
	}

	/**
	 * Method to handle changes in the track 1 position
	 */
	private void t1posChanged() {
		//System.out.println("T1 pos: " + getT1pos());
		if (track1Start.isSelected()) {
			setT1pos(START_1);
		} else if (track1Middle.isSelected()) {
			setT1pos(MIDDLE_1);
		} else {
			setT1pos(STOP_1);
		}
	}

	/**
	 * Method to handle changes in the track 2 position
	 */
	private void t2posChanged() {
		//System.out.println("T2 pos: " + getT2pos());
		if (track2Start.isSelected()) {
			setT2pos(START_2);
		} else if (track2Middle.isSelected()) {
			setT2pos(MIDDLE_2);
		} else {
			setT2pos(STOP_2);
		}
	}

	/**
	 * @param selectionFlag the selectionFlag to set
	 */
	public void setSelectionFlag(int selectionFlag) {
		this.selectionFlag = selectionFlag;
	}

	/**
	 * @return the selectionFlag
	 */
	public int getSelectionFlag() {
		return selectionFlag;
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION if OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
