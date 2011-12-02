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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;

/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K> class of the editing panel (left part)
 * @param <L> class of the content panel (right part)
 */
public abstract class GlobalPanel<K, L> extends JPanel implements ActionListener, MouseListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3731235601667242838L;

	
	protected JPanel editingPanel;	// the stripe editing panel (left part) 
	protected JPanel contentPanel;	// the content panel (right part)
	
	
	/**
	 * Initializes the global panel.
	 * Put the editing panel on the left and the content panel on the right part.
	 */
	protected void initializes () {
		// Set the layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Set the size of the editing panel
		Dimension editingDimension = new Dimension(200, PropertiesDialog.DIALOG_HEIGHT);
		editingPanel.setPreferredSize(editingDimension);

		// Creates the scrollable content pane
		JScrollPane scrollContentPane = new JScrollPane(contentPanel);
		Dimension contentDimension = new Dimension(600, PropertiesDialog.DIALOG_HEIGHT);
		scrollContentPane.setPreferredSize(contentDimension);

		// Adds panels
		add(editingPanel, BorderLayout.WEST);
		add(scrollContentPane, BorderLayout.CENTER);
	}
	
	
	/**
	 * Reset the panel to an "empty" state
	 */
	protected void clearSelection () {
		((EditingPanel<?>) editingPanel).clearSelection();
	}
	
	
	/**
	 * @return the editing panel
	 */
	@SuppressWarnings("unchecked")
	protected K getEditingPanel () {
		return (K)editingPanel;
	}
	
	
	/**
	 * @return the content panel
	 */
	@SuppressWarnings("unchecked")
	protected L getContentPanel () {
		return (L)contentPanel;
	}
	
	
	/**
	 * Creates the editing and the content panels
	 */
	protected abstract void createPanels ();
	
	
	/**
	 * Add the specific listener to the panels
	 */
	protected abstract void addListeners ();
	
	
	
	@Override
	public abstract void mouseClicked(MouseEvent arg0);

	@Override
	public void mouseEntered(MouseEvent arg0) {};

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void actionPerformed(ActionEvent e) {}

}
