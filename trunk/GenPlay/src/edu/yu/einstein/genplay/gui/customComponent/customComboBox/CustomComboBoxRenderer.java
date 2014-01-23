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
package edu.yu.einstein.genplay.gui.customComponent.customComboBox;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEventsGenerator;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxListener;
import edu.yu.einstein.genplay.util.Images;

/**
 * This class is the custom renderer for {@link CustomComboBox}.
 * Selected item is in bold and italic. It is editable clicking on icons for editing and removing.
 * Items can also be added.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class CustomComboBoxRenderer implements ListCellRenderer, CustomComboBoxEventsGenerator {

	private final 	List<CustomComboBoxListener> 	listenerList;		// list of GenomeWindowListener
	private final 		CustomComboBoxRenderer 			instance;			// instance of the class, needed for the CustomComboBoxEventsGenerator.
	private 		int 							x;					// x position of the mouse

	/**
	 * Constructor of {@link CustomComboBoxRenderer}
	 */
	public CustomComboBoxRenderer () {
		listenerList = new ArrayList<CustomComboBoxListener>();
		instance = this;
	}


	@Override
	public void addCustomComboBoxListener(
			CustomComboBoxListener customComboBoxListener) {
		listenerList.add(customComboBoxListener);
		//System.out.println(customComboBoxListener.toString());
	}


	@Override
	public CustomComboBoxListener[] getCustomComboBoxListeners() {
		CustomComboBoxListener[] customComboBoxListeners = new CustomComboBoxListener[listenerList.size()];
		return listenerList.toArray(customComboBoxListeners);
	}


	/**
	 * Creates a square icon using the given path
	 * @param path	icon path
	 * @param side	size of the side
	 * @return		the icon
	 */
	private ImageIcon getIcon (Image image, int side) {
		//ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource(path)));
		//Image img = icon.getImage();
		Image newImg = image.getScaledInstance(side, side, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(newImg);
		return icon;
	}


	@Override
	public Component getListCellRendererComponent(final JList list, final Object value,
			final int index, final boolean isSelected, final boolean cellHasFocus) {
		list.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				x = e.getX();	// we always store the x position of the mouse on the jlist.
			}
		});

		list.addMouseListener(new MouseAdapter() {

			// The principle is to save the x position of the mouse on the jlist,
			// when the user clicks we know where it was on the list and it is possible to know which button was activated.
			@Override
			public void mouseReleased(MouseEvent e) {
				Object element = list.getSelectedValue();					// gets the selected value of the jlist

				int side = getSide(list);									// button side calculation (dependent of the jlist height)

				int action = CustomComboBoxEvent.NO_ACTION;					// action involved by the position of the mouse

				if (element.toString().equals(CustomComboBox.ADD_TEXT)) {	// if the item corresponds to the adding action.
					if (x < list.getWidth()) {
						action = CustomComboBoxEvent.ADD_ACTION;
					}
				} else {													// if not,
					if (x < side) {											// user clicked on the left button (edit)
						action = CustomComboBoxEvent.REPLACE_ACTION;		// the item must be replaced
					} else if ( x < (side * 2)) {							// user clicked on the right button (delete)
						action = CustomComboBoxEvent.REMOVE_ACTION;			// the item must be deleted
					} else {												// user clicked on the item
						action = CustomComboBoxEvent.SELECT_ACTION;			// the item must be simply selected
					}
				}

				x = list.getWidth() + 50;														// mouse position set to 0

				if (action != CustomComboBoxEvent.NO_ACTION) {
					CustomComboBoxEvent event = new CustomComboBoxEvent(instance, list.getSelectedValue(), action);	// creates the custom combo box event
					for (CustomComboBoxListener currentListener: listenerList) {	// warns the listener
						currentListener.customComboBoxChanged(event);
					}
				}
			}
		});

		JPanel panel = new JPanel();				// panel used for displaying: item, buttons (add or edit and delete)
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel("");				// label displaying the item
		panel.add(label, BorderLayout.CENTER);

		if (value != null) {						// if there is a value
			String text = value.toString();			// we get it into a string
			label.setText(text);					// and set it into the label

			// index '-1' means a simple click on the combo box (not its JList), there is no need to return a full panel.
			// if the full panel is returned, button(s) will be displayed in the combo box and it will look like a bug.
			if (index == -1) {
				return panel;
			}

			if (isSelected) {						// if the item is selected (mouse is over it)
				Font font = new Font(label.getFont().getName(), Font.BOLD | Font.ITALIC, label.getFont().getSize());	// creates a different Font
				label.setFont(font);									// sets the font to the label
				JPanel buttonPanel = new JPanel();						// creates a new panel for buttons
				int side = getSide(list);								// gets the side of a button (button is square)
				Insets buttonInset = new Insets(0, 0, 0, 0);			// button insets are set to 0

				if (text.equals(CustomComboBox.ADD_TEXT)) {				// if the value is the one related to the adding action
					// Sets the panel
					buttonPanel.setLayout(new GridLayout(1, 1));		// with one line and two columns
					Dimension buttonDim = new Dimension(side , side);	// creates a dimension for the panel (contains two buttons max)
					buttonPanel.setPreferredSize(buttonDim);			// sets the dimension to the panel size

					ImageIcon addIcon = getIcon(Images.getAddImage(), side);	// get the add icon
					JButton addButton = new JButton(addIcon);			// creates the button containing the icon
					addButton.setContentAreaFilled(false);				// set the button background to transparent
					addButton.setBorder(null);							// disable any border
					addButton.setMargin(buttonInset);					// sets the insets
					buttonPanel.add(addButton);							// add the button on the second cell of the panel
				} else {
					// Sets the panel
					buttonPanel.setLayout(new GridLayout(1, 2));			// with one line and two columns
					Dimension buttonDim = new Dimension(side * 2, side);	// creates a dimension for the panel (contains two buttons max)
					buttonPanel.setPreferredSize(buttonDim);				// sets the dimension to the panel size

					// Creates the edit button (same principle as the add button above)
					ImageIcon editIcon = getIcon(Images.getEditImage(), side);
					JButton replaceButton = new JButton(editIcon);
					replaceButton.setContentAreaFilled(false);
					replaceButton.setBorder(null);
					replaceButton.setMargin(buttonInset);

					// Creates the delete button (same principle as the add button above)
					ImageIcon deleteIcon = getIcon(Images.getDeleteImage(), side);
					JButton deleteButton = new JButton(deleteIcon);
					deleteButton.setContentAreaFilled(false);
					deleteButton.setBorder(null);
					deleteButton.setMargin(buttonInset);

					// Adds buttons to the panel
					buttonPanel.add(replaceButton);
					buttonPanel.add(deleteButton);
				}

				// add the button panel to the global panel
				panel.add(buttonPanel, BorderLayout.WEST);
			}

		}

		return panel;
	}


	/**
	 * Calculation of the button side size according to the size of the list.
	 * The side is equal to the height of a line of the list.
	 * @param list	the list
	 * @return		the size of a side
	 */
	private int getSide (JList list) {
		return (list.getHeight() / list.getModel().getSize());
	}


	@Override
	public void removeCustomComboBoxListener(
			CustomComboBoxListener customComboBoxListener) {
		listenerList.remove(customComboBoxListener);
	}

}
