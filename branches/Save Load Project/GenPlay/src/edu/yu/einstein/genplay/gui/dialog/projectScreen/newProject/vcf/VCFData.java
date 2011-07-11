package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import edu.yu.einstein.genplay.core.enums.VCFType;


class VCFData {

	private VCFLoader 			loader;
	private List<List<Object>> 	data;			// table data
	private VCFList[] 			dialogList;
	private String[] 			columnNames;	// Column names
	private Hashtable<Integer, TableCellEditor> tableCellEditor;


	protected VCFData (VCFLoader manager) {
		this.loader = manager;
		data = new ArrayList<List<Object>>();
		tableCellEditor = new Hashtable<Integer, TableCellEditor>();
		initColumnNames();
		initDialogsList();
	}


	private void initColumnNames () {
		this.columnNames = new String[5];
		this.columnNames[0] = "Group";
		this.columnNames[1] = "Genome";
		this.columnNames[2] = "Type";
		this.columnNames[3] = "File";
		this.columnNames[4] = "Raw name(s)";
	}


	protected void initDialogsList() {
		if(dialogList == null) {
			dialogList = new VCFList[5];
			for (int i = 0; i < 5; i++) {
				dialogList[i] = new VCFList(getColumnName(i), getFirstList(i));
			}
			dialogList[3].setFile(true);
		}
	}


	/**
	 * @param data the data to set
	 */
	protected void setData(List<List<Object>> data) {
		//this.data = data;
		this.data = new ArrayList<List<Object>>();
		for (List<Object> list: data) {
			this.data.add(list);
		}

		initDialogsList();
		for(int i = 0; i < dialogList.length; i++) {
			for (List<Object> elements: data) {
				dialogList[i].addElement(elements.get(i).toString());
			}
		}
		VCFLoader.getInstance().setAllCellEditor();
	}


	/**
	 * @return the data
	 */
	protected List<List<Object>> getData() {
		return data;
	}


	protected void addRow () {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < 5 ; i++) {
			list.add(getNewComboBox(i).getSelectedItem().toString());
		}
		data.add(list);
	}


	protected void removeRows (int[] rows) {
		for (int i: rows) {
			data.remove(i);
		}
		List<List<Object>> newList = new ArrayList<List<Object>>();
		for (List<Object> line: data) {
			newList.add(line);
		}
		data = newList;
	}


	protected JComboBox getNewComboBox (int col) {
		JComboBox box = new JComboBox();
		box.setName("" + col);
		List<String> list = dialogList[col].getElementsList();
		for (String s: list) {
			box.addItem(s);
		}

		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox box = (JComboBox)arg0.getSource();
				String value = box.getSelectedItem().toString();
				if (box.getName().equals("3")) {
					for (int i = 0; i < data.size(); i++) {
						if (value.equals(getValueAt(i, 3))) {
							loader.updateRawNames(value, i, null);
							break;
						}
					}
				}
			}
		});

		return box;
	}


	private List<String> getFirstList (int row) {
		List<String> list = new ArrayList<String>();
		switch (row) {
		case 0:
			list.add(VCFLoader.GROUP_LIST);
			break;
		case 1:
			list.add(VCFLoader.GENOME_LIST);
			break;
		case 2:
			list.add(VCFLoader.TYPE_LIST);
			list.add(VCFType.INDELS.toString());
			list.add(VCFType.SNPS.toString());
			list.add(VCFType.SV.toString());
			break;
		case 3:
			list.add(VCFLoader.FILE_LIST);
			break;
		case 4:
			list.add(VCFLoader.RAW_NAMES_LIST);
			break;
		default:
			break;
		}
		return list;
	}


	protected int getColumnCount() {
		return columnNames.length;
	}


	protected String[] getColumnNames() {
		return columnNames;
	}


	protected String getColumnName(int col) {
		return columnNames[col];
	}


	protected int getRowCount() {
		return data.size();
	}


	protected Object getValueAt(int row, int col) {
		if (row >= 0 && row < getRowCount()) {
			return data.get(row).get(col);
		} else {
			return null;
		}
	}


	protected void setValueAt(Object value, int row, int col) {
		if (row < getRowCount()) {
			data.get(row).set(col, value);
		} else {
			loader.fixRowBug(row, col);
		}
	}


	protected List<String> getColumnList (int col) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < getRowCount(); i++) {
			list.add(getValueAt(i, col).toString());
		}
		return list;
	}


	protected void addEditorForRow(int row, TableCellEditor e ) {
		tableCellEditor.put(new Integer(row), e);
	}


	protected void removeEditorForRow(int[] rows) {
		for (int row: rows) {
			removeEditorForRow(row);
		}
	}


	protected void removeEditorForRow(int row) {
		if (tableCellEditor.get(row) != null) {
			tableCellEditor.remove(new Integer(row));
		}
	}


	protected TableCellEditor getEditor(int row, int col) {
		if (tableCellEditor.get(row) != null) {
			return (TableCellEditor)tableCellEditor.get(row);
		}
		return null;
	}


	protected void displayList(int col) {
		dialogList[col].display();
	}


	protected int getNumberElementList (int col) {
		List<String> list = getColumnList(col);
		int size;
		List<String> listCleared = new ArrayList<String>();
		for (String s: list) {
			if (!listCleared.contains(s)) {
				listCleared.add(s);
			}
		}
		list = listCleared;
		size = list.size();
		if (list.contains(VCFLoader.GROUP_LIST) ||
				list.contains(VCFLoader.GENOME_LIST) ||
				list.contains(VCFLoader.FILE_LIST)) {
			size--;
		}
		return size;
	}


	/*@SuppressWarnings("unused") // For development
	protected void showData () {
		for (int row = 0; row < data.size(); row++) {
			String line = row + " ::: ";
			for (int col = 0; col < 5; col++) {
				line = line + col + ": " + getValueAt(row, col) + "; ";
			}
			System.out.println(line);
		}
	}*/


	/*@SuppressWarnings("unused") // For development
	protected void showElements () {
		for(int i = 0; i < dialogList.length; i++) {
			System.out.println(i + ": ");
			dialogList[i].showElements();
		}
	}*/


}