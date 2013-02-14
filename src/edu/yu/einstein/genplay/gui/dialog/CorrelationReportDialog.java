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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.colors.Colors;



/**
 * A dialog window showing the correlations for each chromosome between two layers
 * @author Julien Lajugie
 * @version 0.1
 */
public class CorrelationReportDialog extends JDialog {

	/**
	 * An extension of the {@link DefaultTableCellRenderer} class that changes the background color and the alignment of the cells
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class CorrelationJTableRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -7966573391861597792L;	// generated ID

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if ((!isSelected) && ((row % 2) == 1)) {
				component.setBackground(Colors.LIGHT_GREY);
			} else {
				component.setBackground(Colors.WHITE);
			}
			if (column == 0) {
				((JLabel) component).setHorizontalAlignment(SwingConstants.LEFT);
			} else {
				((JLabel) component).setHorizontalAlignment(SwingConstants.RIGHT);
			}
			setMaxMinStyle(table, value, row, column, component);
			setTotalStyle(table, value, row, column, component);
			return component;
		}


		/**
		 * Sets the style of the "Total" line
		 * @param table the JTable
		 * @param value the value to assign to the cell at [row, column]
		 * @param row the row of the cell to render
		 * @param column the column of the cell to render
		 * @param component the component whose style needs to be changed
		 */
		private void setTotalStyle(JTable table, Object value, int row, int column, Component component) {
			String valueStr = null;
			if (column == 0) {
				valueStr = value.toString();
			} else {
				valueStr = table.getValueAt(row, 0).toString();
			}
			if ((valueStr != null) && (valueStr.equalsIgnoreCase("total"))) {
				component.setFont(getFont().deriveFont(Font.BOLD + Font.ITALIC));
			}
		}


		/**
		 * Sets the style of rows with the minimum and the maximum correlation
		 * @param table the JTable
		 * @param value the value to assign to the cell at [row, column]
		 * @param row the row of the cell to render
		 * @param column the column of the cell to render
		 * @param component the component whose style needs to be changed
		 */
		private void setMaxMinStyle(JTable table, Object value, int row, int column, Component component) {
			boolean greatest = true;
			boolean smallest = true;
			double valueAsDouble = 0;
			// try to retrieve the value of the current row
			try {
				if (column == 0) {
					valueAsDouble = Double.parseDouble((String) table.getValueAt(row, 1));
				} else {
					valueAsDouble = Double.parseDouble((String) value);
				}
			} catch (NumberFormatException e) {
				// if we can't parse the value it might be because it's a line having "-" as a value
				component.setForeground(Color.BLACK);
				return;
			}
			// check if it's the greatest or the smallest value
			for (int i = 0; i < table.getRowCount(); i++) {
				try {
					double currentAsDouble = Double.parseDouble((String) table.getValueAt(i, 1));
					if (valueAsDouble < currentAsDouble) {
						greatest = false;
					}
					if (valueAsDouble > currentAsDouble) {
						smallest = false;
					}
				} catch (NumberFormatException e) {
					// do nothing if we can't parse
					// it must be a line with "-" as a value
				}
			}
			if ((greatest) && (!smallest)) {
				// case were we have the greatest value
				component.setForeground(Color.RED);
				component.setFont(getFont().deriveFont(Font.BOLD));
			} else if ((smallest) && (!greatest)) {
				// case where we have the smallest one
				component.setForeground(Color.BLUE);
				component.setFont(getFont().deriveFont(Font.BOLD));
			} else {
				// when it's neither the minimum nor the maximum
				component.setForeground(Color.BLACK);
			}
		}
	};


	/**
	 * An extension of the {@link JTable} class that sets the renderer and makes the cells not editable
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class CorrelationJTable extends JTable {

		private static final long serialVersionUID = 1023098920948600203L;	// generated ID

		/**
		 * Creates an instance of {@link CorrelationJTable}.
		 * Sets the renderer as a {@link CorrelationJTableRenderer}
		 * @param rowData
		 * @param columnNames
		 */
		public CorrelationJTable(Object[][] rowData, Object[] columnNames) {
			super(rowData, columnNames);
			CorrelationJTableRenderer renderer = new CorrelationJTableRenderer();
			setDefaultRenderer(Object.class, renderer);
			setRowSorter(new CorrelationJTableSorter<TableModel>(rowData, getModel()));
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}


	/**
	 * Redefines the sorting process for the column containing the chromosome
	 * @author Julien Lajugie
	 * @version 0.1
	 * @param <T>
	 */
	private class CorrelationJTableSorter<T extends TableModel> extends TableRowSorter<T > {

		private final Object[][] data;	// data inside the JTable in the original order

		/**
		 * Redefines the sorting process for the column containing the chromosome.
		 * The order of the chromosome is define by the order used when the JTable is created
		 * @param tableData data inside the JTable in the original order
		 * @param model model of the JTable
		 */
		public CorrelationJTableSorter(Object[][] tableData, T model) {
			super(model);
			this.data = tableData;

			Comparator<Object> comparator = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					int index1 = 0;
					boolean found = false;
					while ((!found) && (index1 < data.length)) {
						if (data[index1][0].equals(o1)) {
							found = true;
						} else {
							index1++;
						}
					}
					int index2 = 0;
					found = false;
					while ((!found) && (index2 < data.length)) {
						if (data[index2][0].equals(o2)) {
							found = true;
						} else {
							index2++;
						}
					}
					return new Integer(index1).compareTo(index2);
				}
			};
			setComparator(0, comparator);
		}
	}


	private static final long serialVersionUID = 5952700526094523963L;	// generated ID
	private static final String text1 = "Correlation Between ";			// text before the first layer name in the text pane
	private static final String text2 = "\n And ";						// text after the first layer name in the text pane
	private static JScrollPane 	jsp;									// scroll pane containing the JTable
	private static JTable 		jt;										// JTable showing the result of the correlation for each chromosome plus the genome wide result
	private static JButton 		jbOk;									// button OK
	private static JTextPane	jtaLayerNames; 							// text pane with the name of the layers

	/**
	 * Privates constructor. Creates an instance of {@link CorrelationReportDialog}
	 * @param parent parent Component in which the dialog is shown
	 * @param correlations array containing the correlations for each chromosome and the correlation genome wide
	 * @param layer1 1st layer
	 * @param layer2 2nd layer
	 */
	private CorrelationReportDialog(Component parent, Double[] correlations, Layer<?> layer1, Layer<?> layer2) {
		super();
		Object[][] tableData = new Object[correlations.length][2];
		ProjectChromosome cm = ProjectManager.getInstance().getProjectChromosome();
		// we fill the correlation for each chromosome
		for (int i = 0; i < cm.size(); i++) {
			tableData[i][0] = cm.get(i);
			if (correlations[i] == null) {
				tableData[i][1] = "-";
			} else {
				tableData[i][1] = NumberFormat.getInstance().format(correlations[i]);
			}
		}
		// we fill the total correlation
		int lastIndex = tableData.length - 1;
		tableData[lastIndex][0] = "Total";
		tableData[lastIndex][1] = NumberFormat.getInstance().format(correlations[lastIndex]);
		// we fill the column names
		Object[] columnNames = {"Chromosome", "Correlation"};
		// create the jtable
		jt = new CorrelationJTable(tableData, columnNames);
		// create the JScrollPane
		jsp = new JScrollPane(jt);
		// create the OK button
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		// retrieve layer properties
		String name1 = layer1.getName();
		String name2 = layer2.getName();
		Color color1 = Colors.BLACK;
		if (layer1 instanceof ColoredLayer) {
			color1 = ((ColoredLayer) layer1).getColor();
		}
		Color color2 = Colors.BLACK;
		if (layer2 instanceof ColoredLayer) {
			color2 = ((ColoredLayer) layer2).getColor();
		}
		// create the text area
		jtaLayerNames = new JTextPane();
		// retrieve the document
		StyledDocument document = jtaLayerNames.getStyledDocument();
		// add a centered justification to the default style
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
		document.setParagraphAttributes(0, 0, attributes, false);
		// set the text of the document
		jtaLayerNames.setText(text1 + name1 + text2 + name2);
		// change the style of the layer names
		StyleConstants.setForeground(attributes, color1);
		document.setCharacterAttributes(text1.length(), name1.length(), attributes, false);
		StyleConstants.setForeground(attributes, color2);
		document.setCharacterAttributes(text1.length() + name1.length() + text2.length() , name2.length(), attributes, false);
		// set the text area non editable and change the color of the background
		jtaLayerNames.setEditable(false);
		jtaLayerNames.setBackground(getContentPane().getBackground());

		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		add(jtaLayerNames, c);

		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		add(jsp, c);

		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		add(jbOk, c);

		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		getRootPane().setDefaultButton(jbOk);
		setTitle("Correlation Report");
		setIconImage(Images.getApplicationImage());
	}


	/**
	 * Show a {@link CorrelationReportDialog} containing the specified data
	 * @param parent parents component in which the dialog is shown
	 * @param correlations array containing the correlation for each chromosome plus a row with the total correlation
	 * @param layer1 1st layer
	 * @param layer2 2nd layer
	 */
	public static void showDialog(Component parent, Double[] correlations, Layer<?> layer1, Layer<?> layer2) {
		new CorrelationReportDialog(parent, correlations, layer1, layer2).setVisible(true);
	}
}
