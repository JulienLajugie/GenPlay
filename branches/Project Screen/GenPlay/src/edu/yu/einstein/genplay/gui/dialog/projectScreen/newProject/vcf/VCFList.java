package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
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
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;


class VCFList {

	private JDialog 			dialog;
	private String				title;
	private List<String> 		elements;
	private JList 				list;
	private DefaultListModel 	listModel;
	private boolean 			isFile;


	protected VCFList (String title, List<String> elements) {
		this.title = title;
		this.elements = elements;
	}


	protected void display () {
		if (dialog == null) {
			dialog = getDialog();
		} else {
			dialog.setVisible(true);
			dialog.toFront();
		}
	}


	private JDialog getDialog () {
		//Dialog
		dialog = new JDialog();
		dialog.setVisible(false);
		dialog.setTitle(title + " list manager");
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);
		//dialog.setLocationRelativeTo(null);
		initLocation();
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
	
	
	private void initLocation () {
		Point point = ProjectScreenManager.getInstance().getLocation();
		int x = (int) (point.getX() + ((VCFLoader.getDialogWidth() - VCFLoader.getDialogListWidth()) / 4));
		int y = (int) (point.getY() + (VCFLoader.getDialogHeight() / 4));
		dialog.setLocation(x, y);
	}


	private void closeDialog () {
		dialog.setVisible(false);
		VCFLoader.getInstance().setAllCellEditor();
	}
	
	
	protected void hide () {
		dialog.setVisible(false);
	}
	

	private JList getList () {
		listModel = new DefaultListModel();
		for (String s: elements) {
			if (!s.equals(VCFLoader.GROUP_LIST) &&
				!s.equals(VCFLoader.GENOME_LIST) &&
				!s.equals(VCFLoader.FILE_LIST)) {
				listModel.addElement(s);
			}
		}
		JList list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		return list;
	}


	private void addElement (String s) {
		//String addElement = null;
		/*if (elements.contains(VCFManager.GROUP_LIST)) {
			addElement = VCFManager.GROUP_LIST;
		} else if (elements.contains(VCFManager.GENOME_LIST)) {
			addElement = VCFManager.GENOME_LIST;
		} else if (elements.contains(VCFManager.FILE_LIST)) {
			addElement = VCFManager.FILE_LIST;
		}
		if (addElement != null) {
			elements.remove(addElement);
		}*/
		elements.add(s);
		listModel.addElement(s);
		//elements.add(addElement);
		//listModel.addElement(addElement);
	}


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


	protected List<String> getElementsList() {
		return elements;
	}

}
