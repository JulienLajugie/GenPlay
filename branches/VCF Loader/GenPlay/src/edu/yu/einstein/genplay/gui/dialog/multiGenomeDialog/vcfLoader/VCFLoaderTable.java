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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.io.File;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomComboBox;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomFileComboBox;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomStringComboBox;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxListener;
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;



/**
 * This class is the table for {@link VCFLoaderDialog}
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLoaderTable extends JTable implements CustomComboBoxListener {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 9060290582452147288L;

	private CustomStringComboBox 	groupBox;	// the box for Group column
	private CustomStringComboBox 	genomeBox;	// the box for Genome column
	private CustomFileComboBox 		fileBox;	// the box for VCF file column
	private JComboBox SNPBox;					// the box for VCF type column

	private int lastRow = 0;		// saves the row index of the last event on getCellEditor
	private int lastCol = 0;		// saves the column index of the last event on getCellEditor
	

	/**
	 * Constructor of {@link VCFLoaderTable}
	 * @param model
	 */
	public VCFLoaderTable (VCFLoaderModel model) {
		super(model);

		TableColumn column;

		// Group combo box
		groupBox = new CustomStringComboBox();
		groupBox.getRenderer().addCustomComboBoxListener(this);
		column = this.getColumnModel().getColumn(VCFData.GROUP_INDEX);
		column.setCellEditor(new DefaultCellEditor(groupBox));

		// Genome combo box
		genomeBox = new CustomStringComboBox();
		genomeBox.getRenderer().addCustomComboBoxListener(this);
		column = this.getColumnModel().getColumn(VCFData.GENOME_INDEX);
		column.setCellEditor(new DefaultCellEditor(genomeBox));

		// File combo box
		fileBox = new CustomFileComboBox();
		fileBox.getRenderer().addCustomComboBoxListener(this);
		VCFFilter[] filter = {new VCFFilter()};
		fileBox.setFilters(filter);
		column = this.getColumnModel().getColumn(VCFData.FILE_INDEX);
		column.setCellEditor(new DefaultCellEditor(fileBox));

		// SNPs combo box
		SNPBox = new JComboBox();
		SNPBox.addItem(VCFType.INDELS);
		SNPBox.addItem(VCFType.SNPS);
		SNPBox.addItem(VCFType.SV);
		column = this.getColumnModel().getColumn(VCFData.TYPE_INDEX);
		column.setCellEditor(new DefaultCellEditor(SNPBox));
	}


	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		lastRow = row;
		lastCol = column;
		switch (column) {
		case VCFData.RAW_INDEX:
			String path = (String) getValueAt(row, VCFData.FILE_INDEX);
			try {
				File file = new File(path);
				VCFReader reader = new VCFReader(file);
				List<String> rawGenomeNames = reader.getRawGenomesNames();
				JComboBox combo = new JComboBox(rawGenomeNames.toArray());
				return new DefaultCellEditor(combo);
			} catch (Exception e) {
				System.out.println("not a valid file");
				return super.getCellEditor(row, column);
			}
		default:
			return super.getCellEditor(row, column);
		}
	}
	
	
	@Override
	public Object getValueAt(int row, int col) {
		Object value = getModel().getValueAt(row, col);
		switch (col) {
		case VCFData.GROUP_INDEX:
			if (value.toString().equals("")) {
				value = CustomComboBox.ADD_TEXT;
			}
			break;
		case VCFData.GENOME_INDEX:
			if (value.toString().equals("")) {
				value = CustomComboBox.ADD_TEXT;
			}
			break;
		case VCFData.RAW_INDEX:
			if (value.toString().equals("")) {
				value = "...";
			}
			break;
		case VCFData.FILE_INDEX:
			if (value == null) {
				value = CustomComboBox.ADD_TEXT;
			}
			break;
		case VCFData.TYPE_INDEX:
			if (value == null) {
				value = "...";
			}
			break;
		default:
			value = new Object();
		}
		return value;
	}
	
	
	/**
	 * Adds an empty row
	 */
	public void addEmptyRow() {
		((VCFLoaderModel) getModel()).addEmptyRow();
	}
	

	/**
	 * @return the data
	 */
	public List<VCFData> getData() {
		return ((VCFLoaderModel)getModel()).getData();
	}


	/**
	 * @param data the data to set
	 */
	public void setData(List<VCFData> data) {
		((VCFLoaderModel)getModel()).setData(data);
		for (VCFData vcfData: data) {
			groupBox.addElement(vcfData.getGroup());
			genomeBox.addElement(vcfData.getGenome());
			fileBox.addElement(vcfData.getFile());
		}
		if (data.size() > 0) {
			groupBox.resetCombo();
			genomeBox.resetCombo();
			fileBox.resetCombo();
		}
	}


	@Override
	public void customComboBoxChanged(CustomComboBoxEvent evt) {
		setValueAt(evt.getElement(), lastRow, lastCol);
	}

}