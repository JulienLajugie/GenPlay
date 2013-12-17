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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K>
 */
abstract public class EditingPanel<K> extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = -8874837321344338625L;

	protected static final int TITLE_HEIGHT 		= 30;
	protected static final int TITLE_LEFT_INSET 	= 20;
	protected static final int MINIMUM_WIDTH 		= 200;
	protected static final int CONTENT_HEIGHT 		= 200;
	protected static final int SCROLL_WIDTH 		= 17;

	protected 	JPanel contentPanel;							// The content panel (to put anything it is needed to put in child classes)
	protected 	K element;										// This object represents the parameter for updating other panel. Each panel may need to transfer ONE object to others. It can be a file, a genome, a list of genomes, filters...

	private final 	JLabel 					titleLabel;					// The label for the title
	private final		JScrollPane 			scrollPane;					// The scroll pane that contains the content pane
	private final 	List<EditingPanel<?>> 	editingPanelListeners;		// Editing panels listening the current panel (can be null)


	/**
	 * Constructor of {@link EditingPanel}
	 * @param title
	 */
	public EditingPanel (String title) {
		// Initializes parameters
		editingPanelListeners = new ArrayList<EditingPanel<?>>();

		// Initializes the title label
		titleLabel = new JLabel(title);

		// Initializes the content panel
		contentPanel = new JPanel();
		initializeContentPanel();

		// Initializes the scroll pane
		scrollPane = new JScrollPane(contentPanel);

		initializeContentPanelSize(MINIMUM_WIDTH, CONTENT_HEIGHT);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Adds the title label
		gbc.insets = new Insets(0, TITLE_LEFT_INSET, 0, 0);
		add(titleLabel, gbc);

		// Adds the scroll pane
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(scrollPane, gbc);
	}


	/**
	 * Adds a panel to the listeners
	 * @param editingPanel the panel to add
	 */
	public void addPanelListener (EditingPanel<?> editingPanel) {
		editingPanelListeners.add(editingPanel);
	}


	/**
	 * Initializes the content panel, the one about the editing part.
	 */
	protected abstract void initializeContentPanel();


	/**
	 * Updates the editing panels listening the current panel
	 */
	private void refreshListeners() {
		for (EditingPanel<?> panelListener: editingPanelListeners) {
			panelListener.update(element);
		}
	}


	/**
	 * Updates the current panel
	 * @param object object used for updating the current panel
	 */
	public abstract void update(Object object);


	/**
	 * @return a list of error gathered in one String
	 */
	public abstract String getErrors ();


	/**
	 * Reset the panel to an empty state
	 */
	public abstract void reset ();


	/**
	 * Initializes the panel with an object
	 * @param element the object to use
	 */
	public abstract void initialize (K element);


	/**
	 * Resets the content panel using an empty panel.
	 */
	protected void resetContentPanel () {
		setNewContentPanel(new JPanel());
	}


	/**
	 * @param strings string array
	 * @return the maximum length among the strings
	 */
	protected int getMaxStringLength (String[] strings) {
		FontMetrics fm = getFontMetrics(getFont());
		int max = 0;
		for (String string: strings) {
			int currentLength = fm.stringWidth(string);
			if (currentLength > max) {
				max = currentLength;
			}
		}
		return max;
	}


	/**
	 * @return the height of the string according to the current font
	 */
	protected int getStringHeight () {
		return getFontMetrics(getFont()).getHeight() + 3;
	}


	/**
	 * Sets the size of the content panel. Before that, the method checks the new width and height according to the minimum size allowed.
	 * It sizes also other components.
	 * @param width		the new width
	 * @param height	the new height
	 * @return			return the dimension used.
	 */
	protected Dimension initializeContentPanelSize (int width, int height) {
		int contentPanelWidth;
		int scrollPanelWidth;
		int contentPanelHeight;
		int scrollPanelHeight;

		if (width <= MINIMUM_WIDTH) {
			contentPanelWidth = MINIMUM_WIDTH - 3;
			scrollPanelWidth = MINIMUM_WIDTH;
		} else {
			contentPanelWidth = width;
			scrollPanelWidth = MINIMUM_WIDTH;
		}

		if (height <= CONTENT_HEIGHT) {
			contentPanelHeight = CONTENT_HEIGHT - 3;
			scrollPanelHeight = CONTENT_HEIGHT;
		} else {
			contentPanelHeight = height;
			scrollPanelHeight = CONTENT_HEIGHT;
		}

		Dimension panelDimension = new Dimension(scrollPanelWidth, scrollPanelHeight + TITLE_HEIGHT);
		Dimension titleDimension = new Dimension(scrollPanelWidth - TITLE_LEFT_INSET, TITLE_HEIGHT);
		Dimension scrollPanelDimension = new Dimension(scrollPanelWidth, scrollPanelHeight);
		Dimension contentPanelDimension = new Dimension(contentPanelWidth, contentPanelHeight);

		setSize(this, panelDimension);
		setSize(titleLabel, titleDimension);
		setSize(scrollPane, scrollPanelDimension);
		setSize(contentPanel, contentPanelDimension);

		repaint();

		return contentPanelDimension;
	}


	protected void setSize (Component comp, Dimension dim) {
		comp.setSize(dim);
		comp.setMinimumSize(dim);
		comp.setMaximumSize(dim);
		comp.setPreferredSize(dim);
	}


	/**
	 * Set a new content panel.
	 * @param panel the new content panel
	 */
	protected void setNewContentPanel (JPanel panel) {
		contentPanel = panel;
		scrollPane.setViewportView(panel);
	}


	/**
	 * @param element the object to set
	 */
	public void setElement(K element) {
		this.element = element;
		refreshListeners();
	}

}
