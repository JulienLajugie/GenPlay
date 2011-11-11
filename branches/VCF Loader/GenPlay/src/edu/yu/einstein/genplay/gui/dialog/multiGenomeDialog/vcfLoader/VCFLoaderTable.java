/**
 * 
 */
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



/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLoaderTable extends JTable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 9060290582452147288L;

	private CustomComboBox groupBox;	// the box for Group column
	private CustomComboBox genomeBox;	// the box for Genome column
	private CustomComboBox fileBox;		// the box for VCF file column
	private JComboBox SNPBox;				// the box for VCF type column


	/**
	 * Constructor of {@link VCFLoaderTable}
	 * @param model
	 */
	public VCFLoaderTable (VCFLoaderModel model) {
		super(model);

		TableColumn column;

		// Group combo box
		groupBox = new CustomComboBox();
		column = this.getColumnModel().getColumn(VCFData.GROUP_INDEX);
		column.setCellEditor(new DefaultCellEditor(groupBox));

		// Genome combo box
		genomeBox = new CustomComboBox();
		column = this.getColumnModel().getColumn(VCFData.GENOME_INDEX);
		column.setCellEditor(new DefaultCellEditor(genomeBox));

		// File combo box
		fileBox = new CustomComboBox();
		//fileBox.setFile(true);
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
		switch (column) {
		/*case VCFData.GROUP_INDEX:
			return new DefaultCellEditor(groupBox);
		case VCFData.GENOME_INDEX:
			return new DefaultCellEditor(testBox);*/
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
		/*case VCFData.FILE_INDEX:
			return new DefaultCellEditor(fileBox);
		case VCFData.TYPE_INDEX:
			return new DefaultCellEditor(SNPBox);*/
		default:
			return super.getCellEditor(row, column);
		}
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
			fileBox.addElement(vcfData.getPath());
		}
		if (data.size() > 0) {
			groupBox.resetCombo();
			genomeBox.resetCombo();
			fileBox.resetCombo();
		}
	}

}
