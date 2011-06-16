package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

class VCFTable extends JTable {

	private static final long serialVersionUID = -287266865363088084L;

	private VCFTableModel 	model;


	protected VCFTable (VCFTableModel model) {
		super(model);
		this.model = model;
	}


	protected void updateTable () {
		model.fireTableDataChanged();
	}

	
	public TableCellEditor getCellEditor(int row, int col) {
		if (col == 4) {
			TableCellEditor editor = model.getCellEditor(row, col);
			if (editor != null) {
				return editor;
			}
		}
		return super.getCellEditor(row, col);
	}
	
	
	public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);

        Object o = model.getValueAt(rowIndex, realColumnIndex);
        if (o != null) {
        	tip = o.toString();
        } else {
        	tip = super.getToolTipText(e);
        }
        
        return tip;
    }

}
