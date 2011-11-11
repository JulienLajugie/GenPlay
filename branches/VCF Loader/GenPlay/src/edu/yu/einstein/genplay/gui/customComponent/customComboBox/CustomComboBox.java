/**
 * 
 */
package edu.yu.einstein.genplay.gui.customComponent.customComboBox;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxListener;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class CustomComboBox extends JComboBox implements CustomComboBoxListener {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 6864223363370055711L;

	/** Text to select for adding a value */
	public static final String ADD_TEXT = "Add...";

	private List<Object> 	elements;		// List of elements


	/**
	 * Constructor of {@link CustomComboBox}
	 */
	public CustomComboBox () {
		super();
		elements = new ArrayList<Object>();
		resetCombo();
		setSelectedItem(ADD_TEXT);
		CustomComboBoxRenderer renderer = new CustomComboBoxRenderer();
		renderer.addCustomComboBoxListener(this);
		setRenderer(renderer);
	}
	
	
	/**
	 * Adds an element to the combo box
	 * @param o the element
	 */
	public void addElement (Object o) {
		if (o != null) {
			elements.add(o);
		}
	}
	
	
	/**
	 * Resets the combo list removing all items and adding the new ones.
	 * It also adds the ADD_TEXT value.
	 */
	public void resetCombo () {
		this.removeAllItems();
		for (Object element: elements) {
			if (!element.toString().equals("")) {
				this.addItem(element);
			}
		}
		this.addItem(ADD_TEXT);
	}


	@Override
	public void customComboBoxChanged(CustomComboBoxEvent evt) {
		System.out.println(evt.getAction() + " " + evt.getElement());
		if (evt.getAction() == CustomComboBoxEvent.SELECT_ACTION) {
			setSelectedItem(evt.getElement());
		} else if (evt.getAction() == CustomComboBoxEvent.ADD_ACTION) {
			String element = JOptionPane.showInputDialog(this,
					"Please type a new entry.",
					"Entry insertion",
					JOptionPane.PLAIN_MESSAGE);

			if (element != null && element.length() > 0) {
				addElement(element);
				resetCombo();
				setSelectedItem(element);
			}
		} else if (evt.getAction() == CustomComboBoxEvent.REMOVE_ACTION) {
			Object[] options = {"Yes", "No"};
			int n = JOptionPane.showOptionDialog(this,
					"Do you really want to erase '" + evt.getElement() + "' ?",
					"Entry deletion",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null,
					options,
					options[0]);
			
			if (n == JOptionPane.YES_OPTION) {
				elements.remove(evt.getElement());
				resetCombo();
				setSelectedIndex(0);
			}
		} else if (evt.getAction() == CustomComboBoxEvent.REPLACE_ACTION) {
			String element = (String)JOptionPane.showInputDialog(
                    this,
                    "The new entry will replace '" + evt.getElement() + "'.",
                    "Entry modification",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    evt.getElement());

			if (element != null && element.length() > 0) {
				elements.remove(evt.getElement());
				addElement(element);
				resetCombo();
				setSelectedItem(element);
			}
		}
	}

}
