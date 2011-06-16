package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

class VCFTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8124585566610818031L;

	
	private VCFData		data;			// Table data	


	protected VCFTableModel (VCFData data) {
		this.data = data;
	}

	
	@Override
	public int getColumnCount() {
		return data.getColumnCount();
	}


	@Override
	public int getRowCount() {
		return data.getRowCount();
	}

	
	@Override
	public String getColumnName(int col) {
		return data.getColumnName(col);
	}


	@Override
	public Object getValueAt(int row, int col) {
		return data.getValueAt(row, col);
	}
	
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		data.setValueAt(value, row, col);
	}


	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	
	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	
	protected TableCellEditor getCellEditor(int row, int col) {
		return data.getEditor(row);
	}
	
}