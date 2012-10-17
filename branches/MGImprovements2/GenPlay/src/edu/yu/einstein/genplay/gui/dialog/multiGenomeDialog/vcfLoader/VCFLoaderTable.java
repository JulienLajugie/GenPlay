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

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomComboBox;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomFileComboBoxMG;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomStringComboBox;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxListener;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.VCFGZFilter;



/**
 * This class is the table for {@link VCFLoaderDialog}
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLoaderTable extends JTable implements CustomComboBoxListener, ActionListener {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 9060290582452147288L;

	private CustomStringComboBox 	groupBox;		// the box for Group column
	private CustomStringComboBox 	genomeBox;		// the box for Genome column
	private CustomFileComboBoxMG 	fileBox;		// the box for VCF file column
	private final List<JComboBox>	rawComboList;

	private int lastRow = 0;		// saves the row index of the last event on getCellEditor
	private int lastCol = 0;		// saves the column index of the last event on getCellEditor

	private final Map<String, List<String>> temporaryFileAndGenomeNameMap;
	private final List<VCFFile> vcfFileList;



	/**
	 * Constructor of {@link VCFLoaderTable}
	 * @param model
	 */
	public VCFLoaderTable (VCFLoaderModel model) {
		super(model);
		temporaryFileAndGenomeNameMap = new HashMap<String, List<String>>();
		vcfFileList = new ArrayList<VCFFile>();
		rawComboList = new ArrayList<JComboBox>();
		initializesBoxes();
	}


	/**
	 * initializes the different combo boxes used in the table
	 */
	private void initializesBoxes () {
		TableColumn column;

		// Group combo box
		groupBox = new CustomStringComboBox();
		groupBox.getRenderer().addCustomComboBoxListener(this);
		column = this.getColumnModel().getColumn(VCFData.GROUP_INDEX);
		column.setCellEditor(new DefaultCellEditor(groupBox));

		// Genome combo box
		genomeBox = new CustomStringComboBox();
		genomeBox.getRenderer().addCustomComboBoxListener(this);
		column = this.getColumnModel().getColumn(VCFData.NICKNAME_INDEX);
		column.setCellEditor(new DefaultCellEditor(genomeBox));

		// File combo box
		fileBox = new CustomFileComboBoxMG(this);
		fileBox.getRenderer().addCustomComboBoxListener(this);
		ExtendedFileFilter[] filter = {new VCFFilter(), new VCFGZFilter()};
		fileBox.setFilters(filter);
		column = this.getColumnModel().getColumn(VCFData.FILE_INDEX);
		column.setCellEditor(new DefaultCellEditor(fileBox));
	}


	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		lastRow = row;
		lastCol = column;
		switch (column) {
		case VCFData.RAW_INDEX:
			try {
				JComboBox combo;
				if (lastRow < rawComboList.size()) {
					combo = rawComboList.get(lastRow);
				} else {
					File file = (File) getValueAt(row, VCFData.FILE_INDEX);
					VCFFile vcfFile = getVCFFile(file);
					List<String> rawGenomeNames = vcfFile.getHeader().getRawGenomesNames();
					combo = new RawNameComboBox(rawGenomeNames.toArray());
					combo.addActionListener(this);
					rawComboList.add(combo);
				}
				return new DefaultCellEditor(combo);
			} catch (Exception e) {
				System.out.println("not a valid file : " + getValueAt(row, VCFData.FILE_INDEX));
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
		case VCFData.NICKNAME_INDEX:
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
		default:
			value = new Object();
		}
		return value;
	}


	/**
	 * Adds an empty row
	 */
	public void addEmptyRow() {
		VCFLoaderModel model = (VCFLoaderModel) getModel();
		model.addEmptyRow();
		String defaultGroupValue = MGDisplaySettings.getInstance().getVariousSettings().getDefaultGroupText();
		if (model.getData().size() == 1) {
			groupBox.addElement(defaultGroupValue);
			groupBox.resetCombo();
		}
		model.setValueAt(defaultGroupValue, model.getData().size() - 1, VCFData.GROUP_INDEX);
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
		List<VCFData> newData = new ArrayList<VCFData>();
		for (int i = 0; i < data.size(); i++) {
			VCFData rowData = new VCFData();
			rowData.setGroup(data.get(i).getGroup());
			rowData.setNickname(data.get(i).getNickname());
			rowData.setRaw(data.get(i).getRaw());
			rowData.setFile(data.get(i).getFile());
			newData.add(rowData);
		}
		((VCFLoaderModel)getModel()).setData(newData);

		initializesBoxes();
		for (VCFData vcfData: data) {
			groupBox.addElement(vcfData.getGroup());
			genomeBox.addElement(vcfData.getNickname());
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
		Object value = evt.getElement();
		setValueAt(value, lastRow, lastCol);
		updateSize(value, lastCol);

		if ((lastCol == VCFData.FILE_INDEX) && !value.toString().equals(CustomComboBox.ADD_TEXT)) {
			String filePath = value.toString();
			List<String> rawGenomeNames = null;
			if (temporaryFileAndGenomeNameMap.containsKey(filePath)) {
				rawGenomeNames = temporaryFileAndGenomeNameMap.get(filePath);
			} else {
				File file = new File(value.toString());
				VCFFile vcfFile = getVCFFile(file);
				rawGenomeNames = vcfFile.getHeader().getRawGenomesNames();
				temporaryFileAndGenomeNameMap.put(filePath, rawGenomeNames);
			}
			if (rawGenomeNames != null) {
				setValueAt(rawGenomeNames.get(0), lastRow, VCFData.RAW_INDEX);
				updateRawName(lastRow);
			}
		}
	}


	/**
	 * Sets the width of a column according to the new value
	 * @param value		new value
	 * @param column	column index
	 */
	private void updateSize (Object value, int column) {
		FontMetrics fm = getFontMetrics(getFont());
		int width = fm.stringWidth(value.toString());
		int colWidth = getColumnModel().getColumn(column).getWidth();
		if (colWidth < width) {
			getColumnModel().getColumn(column).setPreferredWidth(width);
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof RawNameComboBox) {
			RawNameComboBox box = (RawNameComboBox) e.getSource();
			if (box.isClicked()) {
				updateRawName(lastRow);
			}
		}
	}


	/**
	 * Updates the nickname box in a row according to the selected raw name of the same row
	 * @param row	a row
	 */
	private void updateRawName (int row) {
		String rawName = (String) getValueAt(row, VCFData.RAW_INDEX);;
		if ((rawName != null) && !rawName.isEmpty()) {
			genomeBox.addElement(rawName);
			genomeBox.resetCombo();
			((VCFLoaderModel) getModel()).setValueAt(rawName, row, VCFData.NICKNAME_INDEX);
		}
	}


	/**
	 * @param file
	 * @return the existing VCF file (if exists) or a new VCF File
	 */
	public VCFFile getVCFFile (File file) {
		for (VCFFile vcfFile: vcfFileList) {
			if (vcfFile.getFile().getAbsoluteFile().equals(file.getAbsoluteFile())) {
				return vcfFile;
			}
		}
		VCFFile vcfFile = null;
		try {
			vcfFile = new VCFFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		vcfFileList.add(vcfFile);
		return vcfFile;
	}

}