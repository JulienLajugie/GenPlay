/**
 * 
 */
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.core.enums.VCFType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLoaderModel extends AbstractTableModel {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 3478197435828366331L;


	private String[] columnNames;	// column names
	private List<VCFData> data;		// list of data


	/**
	 * Constructor of {@link VCFLoaderModel}
	 */
	public VCFLoaderModel () {
		initializeColumnNames();
		data = new ArrayList<VCFData>();
	}


	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}


	@Override
	public int getRowCount() {
		return data.size();
	}


	@Override
	public boolean isCellEditable(int row, int col)	{
		if (col == VCFData.RAW_INDEX) {
			if (getValueAt(row, VCFData.FILE_INDEX).equals("")) {
				return false;
			}
		}
		return true;
	}


	@Override
	public Object getValueAt(int row, int col) {
		VCFData vCFData = data.get(row);
		String value;
		switch (col) {
		case VCFData.GROUP_INDEX:
			value = vCFData.getGroup();
			/*if (value.equals("")) {
				value = MyEditableComboBox.ADD_TEXT;
			}*/
			return value;
		case VCFData.GENOME_INDEX:
			value = vCFData.getGenome();
			/*if (value.equals("")) {
				value = MyEditableComboBox.ADD_TEXT;
			}*/
			return value;
		case VCFData.RAW_INDEX:
			return vCFData.getRaw();
		case VCFData.FILE_INDEX:
			value = vCFData.getPath();
			/*if (value.equals("")) {
				value = MyEditableComboBox.ADD_TEXT;
			}*/
			return value;
		case VCFData.TYPE_INDEX:
			return vCFData.getType();
		default:
			return new Object();
		}
	}


	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < getRowCount()) {
			VCFData vCFData = data.get(row);
			switch (col) {
			case VCFData.GROUP_INDEX:
				vCFData.setGroup(value.toString());
				break;
			case VCFData.GENOME_INDEX:
				vCFData.setGenome(value.toString());
				break;
			case VCFData.RAW_INDEX:
				vCFData.setRaw(value.toString());
				break;
			case VCFData.FILE_INDEX:
				vCFData.setPath(value.toString());
				break;
			case VCFData.TYPE_INDEX:
				vCFData.setType((VCFType)value);
				break;
			default:
				System.out.println("Invalid column index");
			}
		} else {
			System.out.println("Invalid row index");
		}
		fireTableCellUpdated(row, col);
	}


	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case VCFData.GROUP_INDEX:
			return String.class;
		case VCFData.GENOME_INDEX:
			return String.class;
		case VCFData.RAW_INDEX:
			return String.class;
		case VCFData.FILE_INDEX:
			return String.class;
		case VCFData.TYPE_INDEX:
			return VCFType.class;
		default:
			return Object.class;
		}
	}


	/**
	 * Adds an empty row
	 */
	protected void addEmptyRow() {
		data.add(new VCFData());
		fireTableRowsInserted(
				data.size() - 1,
				data.size() - 1);
		
	}
	
	
	/**
	 * Deletes a row
	 * @param row the row number
	 */
	public void deleteRow(int row) {
		data.remove(row);
		fireTableRowsDeleted(row, row);
	}


	/**
	 * @return the data
	 */
	public List<VCFData> getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(List<VCFData> data) {
		this.data = data;
		for (int row = 0; row <data.size(); row++) {
			fireTableCellUpdated(row, VCFData.GROUP_INDEX);
			fireTableCellUpdated(row, VCFData.GENOME_INDEX);
			fireTableCellUpdated(row, VCFData.RAW_INDEX);
			fireTableCellUpdated(row, VCFData.FILE_INDEX);
			fireTableCellUpdated(row, VCFData.TYPE_INDEX);
		}
	}


	/**
	 * Initializes the column names
	 */
	private void initializeColumnNames () {
		columnNames = new String[5];
		columnNames[0] = VCFData.GROUP_NAME;
		columnNames[1] = VCFData.GENOME_NAME;
		columnNames[2] = VCFData.RAW_NAME;
		columnNames[3] = VCFData.FILE_NAME;
		columnNames[4] = VCFData.TYPE_NAME;
	}

}
