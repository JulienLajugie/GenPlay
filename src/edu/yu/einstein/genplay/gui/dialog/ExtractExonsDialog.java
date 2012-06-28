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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import edu.yu.einstein.genplay.core.list.geneList.operation.GLOExtractExons;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAExtractExons;


/**
 * A frame to define the interval for the {@link GLAExtractExons}
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ExtractExonsDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = -2409644228796158983L;
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private final static String[] OPTIONS =
		{"Extract First Exon", "Extract Last Exon", "Extract All Exons"};		// available options in the comboBoxes
	private final static int[] OPTION_VALUES = {
		GLOExtractExons.FIRST_EXON,
		GLOExtractExons.LAST_EXON,
		GLOExtractExons.ALL_EXONS,
	};

	private final JLabel jlExonOptions;			// exon option label
	private final JComboBox jcbExonOptions;		// comboBox Exon Options
	private final JButton jbOk;					// button okay
	private final JButton jbCancel;				// button cancel
	private int	approved = CANCEL_OPTION;


	/**
	 * Creates an instance of {@link ExtractExonsDialog}
	 */
	public ExtractExonsDialog () {
		super();
		jlExonOptions = new JLabel("Extract Exon Options: ");
		jcbExonOptions = new JComboBox(OPTIONS);

		jbOk = new JButton("Ok");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(this);

		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		add(jlExonOptions);

		gbc.gridx = 1;
		gbc.gridy = 0;
		add(jcbExonOptions);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(jbOk, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, gbc);

		pack();
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("Extract Exons");
		setVisible(false);
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
	}

	/**
	 * @return an int corresponding to exon-option selected
	 */
	public int getSelectedExonOption() {
		return OPTION_VALUES[jcbExonOptions.getSelectedIndex()];
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbOk) {
			approved = APPROVE_OPTION;
		}
		setVisible(false);
	}

	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
