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
package edu.yu.einstein.genplay.gui.dialog.trackChooser;

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

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Images;


/**
 * A dialog box used to choose a track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackChooser extends JDialog {

	private static final long 	serialVersionUID = 2840205300507226959L;	// Generated ID
	private static JLabel 		jlText;										// label
	private static JComboBox 	jcbTrack;									// comboBox to choose the track
	private static JButton 		jbOk;										// OK button
	private static JButton 		jbCancel;									// cancel button
	private static Track[] 		options;									// list of available tracks
	private static String		textLabel;									// text of the label
	private static boolean 		validated;									// true if OK has been pressed


	/**
	 * Private constructor. Used internally to create a TrackChooser dialog.
	 * @param parent The {@link Component} from which the dialog is displayed.
	 * @param title Title of the dialog.
	 * @param text Text of the dialog.
	 * @param tracks List of {@link Track}.
	 */
	private TrackChooser(Component parent, String title, String text, Track[] tracks) {
		super();
		options = tracks;
		validated = false;
		textLabel = text;
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle(title);
		setIconImage(Images.getApplicationImage());
		initComponent();
		setPreferredSize(new Dimension(300, 175));
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}


	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jlText = new JLabel(textLabel);
		jcbTrack = new JComboBox(options);

		jbOk = new JButton("OK");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER ;
		c.weightx = 0.5;
		c.weighty = 0.20;
		c.anchor = GridBagConstraints.CENTER;
		add(jlText, c);

		c.gridy = 1;
		add(jcbTrack, c);

		c.fill = GridBagConstraints.NONE;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);
	}


	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelActionPerformed() {
		dispose();
	}


	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected tracks.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		dispose();
	}


	/**
	 * Only public function. Displays a TrackChooser dialog, and returns a track
	 * @param parent The {@link Component} from which the dialog is displayed.
	 * @param title Title of the dialog.
	 * @param text Text of the dialog.
	 * @param tracks List of {@link Track}.
	 * @return The selected track
	 */
	public static Track getTracks(Component parent, String title, String text, Track[] tracks) {
		TrackChooser tc = new TrackChooser(parent, title, text, tracks);
		tc.setVisible(true);
		if(validated) {
			return (Track)jcbTrack.getSelectedItem();
		} else {
			return null;
		}
	}
}
