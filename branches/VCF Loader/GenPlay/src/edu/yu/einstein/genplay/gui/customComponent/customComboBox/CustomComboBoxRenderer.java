package edu.yu.einstein.genplay.gui.customComponent.customComboBox;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEventsGenerator;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxListener;

/**
 * @author Nicolas Fourel
 *
 */
public class CustomComboBoxRenderer implements ListCellRenderer, CustomComboBoxEventsGenerator {

	private final List<CustomComboBoxListener> 	listenerList;		// list of GenomeWindowListener
	private CustomComboBoxRenderer instance;
	private int x;


	/**
	 * Constructor of {@link CustomComboBoxRenderer}
	 */
	public CustomComboBoxRenderer () {
		this.listenerList = new ArrayList<CustomComboBoxListener>();
		this.instance = this;
	}


	@Override
	public Component getListCellRendererComponent(final JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		list.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				x = e.getX();
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Object element = list.getSelectedValue();

				int width = list.getWidth();
				int side = getSide(list);

				int action;

				if (element.toString().equals(CustomComboBox.ADD_TEXT)) {
					if (x > (width - side)) {
						action = CustomComboBoxEvent.ADD_ACTION;
					} else {
						action = CustomComboBoxEvent.SELECT_ACTION;
					}
				} else {
					if (x > (width - side)) {
						action = CustomComboBoxEvent.REMOVE_ACTION;
					} else if (x > (width - (side * 2))) {
						action = CustomComboBoxEvent.REPLACE_ACTION;
					} else {
						action = CustomComboBoxEvent.SELECT_ACTION;
					}
				}

				x = 0;

				CustomComboBoxEvent event = new CustomComboBoxEvent(instance, list.getSelectedValue(), action);
				for (CustomComboBoxListener currentListener: listenerList) {
					System.out.println("currentListener");
					currentListener.customComboBoxChanged(event);
				}
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel("");
		panel.add(label, BorderLayout.CENTER);

		if (value != null) {
			String text = value.toString();
			label.setText(text);

			if (isSelected) {
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new GridLayout(1, 2));
				int side = getSide(list);
				Dimension buttonDim = new Dimension(side * 2, side);
				buttonPanel.setPreferredSize(buttonDim);
				Insets buttonInset = new Insets(0, 0, 0, 0);

				if (text.equals(CustomComboBox.ADD_TEXT)) {
					JButton addButton = new JButton("A");
					addButton.setMargin(buttonInset);
					buttonPanel.add(new JLabel());
					buttonPanel.add(addButton);
				} else {
					JButton replaceButton = new JButton("R");
					JButton deleteButton = new JButton("D");
					replaceButton.setMargin(buttonInset);
					deleteButton.setMargin(buttonInset);
					buttonPanel.add(replaceButton);
					buttonPanel.add(deleteButton);
				}
				panel.add(buttonPanel, BorderLayout.EAST);
			}
		}

		return panel;
	}


	private int getSide (JList list) {
		return (list.getHeight() / list.getModel().getSize());
	}


	@Override
	public void addCustomComboBoxListener(
			CustomComboBoxListener customComboBoxListener) {
		listenerList.add(customComboBoxListener);
	}

	@Override
	public CustomComboBoxListener[] getCustomComboBoxListeners() {
		CustomComboBoxListener[] customComboBoxListeners = new CustomComboBoxListener[listenerList.size()];
		return listenerList.toArray(customComboBoxListeners);
	}

	@Override
	public void removeCustomComboBoxListener(
			CustomComboBoxListener customComboBoxListener) {
		listenerList.remove(customComboBoxListener);
	}

}
