package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
//import java.awt.Point;
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


	/*private void initLocation () {
		Point point = ProjectScreenManager.getInstance().getLocation();
		int x = (int) (point.getX() + ((VCFLoader.getDialogWidth() - VCFLoader.getDialogListWidth()) / 4));
		int y = (int) (point.getY() + (VCFLoader.getDialogHeight() / 4));
		dialog.setLocation(x, y);
	}*/


	private void closeDialog () {
		approved = APPROVE_OPTION;
		dialog.setVisible(false);
		VCFLoader.getInstance().setAllCellEditor();
	}


	private JList getList () {
		initListModel();

		JList list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		return list;
	}


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


	protected void addElement (String s) {
		if (!elements.contains(s)) {
			elements.add(s);
			initListModel();
			listModel.addElement(s);
		}
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

	protected void showElements () {
		String s = "";
		for (String element: elements) {
			s = s + " " + element;
		}
		System.out.println(s);
	}

}
