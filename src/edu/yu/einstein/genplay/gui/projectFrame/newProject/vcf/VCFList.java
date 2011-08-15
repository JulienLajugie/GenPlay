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
package edu.yu.einstein.genplay.gui.projectFrame.newProject.vcf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;

/**
 * This class manages dialog list editor.
 * Some columns can be edited using a dialog object displayed by this class.
 * @author Nicolas Fourel
 * @version 0.1
 */
class VCFList {

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;
	private int	approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not


	private JDialog 			dialog;		// the dialog list editor instance
	private String				title;		// the dialog title
	private List<String> 		elements;	// the elements of the list (to display)
	private JList 				list;		// the list component
	private DefaultListModel 	listModel;	// the list model component
	private boolean 			isFile;		// is true if elements are file (different behavior)


	/**
	 * Constructor of {@link VCFList}
	 * @param title		the dialog title
	 * @param elements	the list of elements
	 */
	protected VCFList (String title, List<String> elements) {
		this.title = title;
		this.elements = elements;
	}


	/**
	 * Displays the dialog
	 * @return 0 if the user approved the changes / 1 otherwise
	 */
	protected int display () {
		if (dialog == null) {
			dialog = getDialog();
		}
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(VCFLoader.getInstance());
		dialog.toFront();
		return approved;
	}


	/**
	 * Creates the dialog list editor
	 * @return the dialog list editor
	 */
	private JDialog getDialog () {
		//Dialog
		dialog = new JDialog();
		dialog.setVisible(false);
		dialog.setTitle(title + " list editor");
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);
		dialog.setLocationRelativeTo(VCFLoader.getInstance());
		//initLocation();
		Dimension dim = new Dimension(VCFLoader.getDialogListWidth(), VCFLoader.getDialogListHeight());
		dialog.setSize(dim);
		dialog.setMinimumSize(dim);
		dialog.setPreferredSize(dim);
		BorderLayout bl = new BorderLayout();
		dialog.setLayout(bl);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				closeDialog();
			}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});

		//List
		list = getList();
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(VCFLoader.getDialogListWidth(), VCFLoader.getDialogListHeight()));

		//Button dimension
		Dimension dimButton = new Dimension(VCFLoader.getButtonSide(), VCFLoader.getButtonSide());

		//Add button
		JButton add = new JButton("+");
		add.setSize(dimButton);
		add.setMinimumSize(dimButton);
		add.setPreferredSize(dimButton);
		add.setToolTipText(VCFLoader.ADD_ELEMENT);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String inputValue = "";
				if (isFile) {
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new File(ConfigurationManager.getInstance().getDefaultDirectory()));
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setFileFilter(new VCFFilter());
					int returnVal = chooser.showOpenDialog(dialog);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						inputValue = chooser.getSelectedFile().getPath();
					}
				} else {
					inputValue = JOptionPane.showInputDialog(dialog, "Add a new " + title);
				}
				if (inputValue != null && !inputValue.equals("")) {
					addElement(inputValue);

				}
			}
		});

		//Del button
		JButton del = new JButton("-");
		del.setSize(dimButton);
		del.setMinimumSize(dimButton);
		del.setPreferredSize(dimButton);
		del.setToolTipText(VCFLoader.REMOVE_ELEMENT);
		del.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object o[] = list.getSelectedValues();
				for (Object e: o) {
					removeElement((String) e);
				}
			}
		});

		//Button panel
		JPanel buttonPanel = new JPanel();
		GridLayout gl = new GridLayout(1, 2);
		buttonPanel.setLayout(gl);
		buttonPanel.add(add);
		buttonPanel.add(del);

		dialog.add(listScroller, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		return dialog;
	}


	/**
	 * Closes the dialog list editor
	 */
	private void closeDialog () {
		approved = APPROVE_OPTION;
		dialog.setVisible(false);
		VCFLoader.getInstance().setAllCellEditor();
	}


	/**
	 * Creates the list component
	 * @return the list component
	 */
	private JList getList () {
		initListModel();

		JList list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		return list;
	}


	/**
	 * Initializes the list model component
	 */
	private void initListModel () {
		if(listModel == null) {
			listModel = new DefaultListModel();
			for (String s: elements) {
				if (!s.equals(VCFLoader.GROUP_LIST) &&
						!s.equals(VCFLoader.GENOME_LIST) &&
						!s.equals(VCFLoader.FILE_LIST)) {
					listModel.addElement(s);
				}
			}
		}
	}


	/**
	 * Adds an element to the list
	 * @param s
	 */
	protected void addElement (String s) {
		if (!elements.contains(s)) {
			elements.add(s);
			initListModel();
			listModel.addElement(s);
		}
	}


	/**
	 * Removes an element from the list
	 * @param s element to remove
	 */
	private void removeElement (String s) {
		elements.remove(s);
		listModel.removeElement(s);
	}


	/**
	 * @param isFile the isFile to set
	 */
	protected void setFile(boolean isFile) {
		this.isFile = isFile;
	}


	/**
	 * @return all elements from the list
	 */
	protected List<String> getElementsList() {
		return elements;
	}

	
	/**
	 * Shows elemetns in the console
	 */
	protected void showElements () {
		String s = "";
		for (String element: elements) {
			s = s + " " + element;
		}
		System.out.println(s);
	}

}
