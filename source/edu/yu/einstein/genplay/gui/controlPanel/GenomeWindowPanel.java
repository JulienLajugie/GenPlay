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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;


/**
 * The GenomeWindowPanel part of the {@link ControlPanel} 
 * @author Julien Lajugie
 * @version 0.1
 */
final class GenomeWindowPanel extends JPanel implements GenomeWindowEventsGenerator {

	private static final long serialVersionUID = 8279801687428218652L;  // generated ID
	private final JTextField 						jftGenomeWindow;	// text field for the GenomeWindow
	private final JButton 							jbJump;				// button jump to position
	private final ArrayList<GenomeWindowListener> 	listenerList;		// list of GenomeWindowListener
	private GenomeWindow 							currentGenomeWindow;// current GenomeWindow
	

	/**
	 * Creates an instance of {@link GenomeWindowPanel}
	 * @param genomeWindow a {@link GenomeWindow}
	 */
	GenomeWindowPanel(GenomeWindow genomeWindow) {
		this.currentGenomeWindow = genomeWindow;
		this.listenerList = new ArrayList<GenomeWindowListener>();
		jftGenomeWindow = new JTextField(20);
		jftGenomeWindow.setText(genomeWindow.toString());
		jftGenomeWindow.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					genomeWindowChanged();
				}
			}
		});
		
		jbJump = new JButton("jump");
		jbJump.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				genomeWindowChanged();				
			}
		});

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jftGenomeWindow, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jbJump, gbc);
	}


	/**
	 * Sets the current {@link GenomeWindow}
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(currentGenomeWindow)) {
			GenomeWindow oldGenomeWindow = currentGenomeWindow;
			currentGenomeWindow = newGenomeWindow;
			// we notify the gui
			jftGenomeWindow.setText(currentGenomeWindow.toString());
			genomeWindowChanged();
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, currentGenomeWindow);
			for (GenomeWindowListener currentListener: listenerList) {
				currentListener.genomeWindowChanged(evt);
			}
		}
	}


	/**
	 * Called when the current {@link GenomeWindow} changes
	 */
	void genomeWindowChanged() {
		try {
			GenomeWindow newGenomeWindow = new GenomeWindow(jftGenomeWindow.getText(), ChromosomeManager.getInstance());
			if (!newGenomeWindow.equals(currentGenomeWindow)) {
				int middlePosition = (int)newGenomeWindow.getMiddlePosition();
				if ((middlePosition < 0) || (middlePosition > newGenomeWindow.getChromosome().getLength())) {
					JOptionPane.showMessageDialog(getRootPane(), "Invalid position", "Error", JOptionPane.WARNING_MESSAGE, null);
				} else {
					setGenomeWindow(newGenomeWindow);
				}
			}			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(getRootPane(), "Invalid position", "Error", JOptionPane.WARNING_MESSAGE, null);
			jftGenomeWindow.setText(currentGenomeWindow.toString());
			e.printStackTrace();
		}		
	}


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.add(genomeWindowListener);		
	}	
	
	
	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[listenerList.size()];
		return listenerList.toArray(genomeWindowListeners);
	}
	
	
	@Override
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.remove(genomeWindowListener);		
	}
}
