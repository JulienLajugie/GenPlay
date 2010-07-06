/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import yu.einstein.gdp2.core.manager.ChromosomeManager;


/**
 * A dialog window showing the correlations for each chromosome between two tracks 
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
			if ((!isSelected) && (row % 2 == 1)) {
				setBackground(Color.LIGHT_GRAY);
			} else {
				setBackground(Color.WHITE);
			}
			if (column == 0) {
				setHorizontalAlignment(JLabel.LEFT);
			} else {
				setHorizontalAlignment(JLabel.RIGHT);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}


	private static final long serialVersionUID = 5952700526094523963L;	// generated ID
	private static final String text1 = "Correlation Between ";			// text before the first track name in the text pane 
	private static final String text2 = "\n And ";						// text after the first track name in the text pane
	private static JScrollPane 	jsp;									// scroll pane containing the JTable
	private static JTable 		jt;										// JTable showing the result of the correlation for each chromosome plus the genome wide result
	private static JButton 		jbOk;									// button OK
	private static JTextPane	jtaTrackNames; 							// text pane with the name of the tracks 

	
	/**
	 * Privates constructor. Creates an instance of {@link CorrelationReportDialog}
	 * @param parent parent Component in which the dialog is shown
	 * @param correlations array containing the correlations for each chromosome and the correlation genome wide
	 * @param name1 name of the first track
	 * @param name2 name of the second track
	 */
	private CorrelationReportDialog(Component parent, Double[] correlations, String name1, String name2) {
		super();
		DecimalFormat df = new DecimalFormat("#.##");
		Object[][] tableData = new Object[correlations.length][2];
		ChromosomeManager cm = ChromosomeManager.getInstance();
		// we fill the correlation for each chromosome
		for (int i = 0; i < cm.size(); i++) {
			tableData[i][0] = cm.get(i);
			if (correlations[i] == null) {
				tableData[i][1] = "-";
			} else {
				tableData[i][1] = df.format(correlations[i]);
			}
		}
		// we fill the total correlation
		int lastIndex = tableData.length - 1;
		tableData[lastIndex][0] = "Total";
		tableData[lastIndex][1] = df.format(correlations[lastIndex]);
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
		// create the text area
		jtaTrackNames = new JTextPane();
		// retrieve the document
		StyledDocument document = jtaTrackNames.getStyledDocument();
		// add a centered justification to the default style
		MutableAttributeSet attributes = new SimpleAttributeSet(); 		
		StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
		document.setParagraphAttributes(0, 0, attributes, false);		
		// set the text of the document
		jtaTrackNames.setText(text1 + name1 + text2 + name2);
		// change the style of the track names
		StyleConstants.setForeground(attributes, Color.BLUE);
		//StyleConstants.setItalic(attributes, true);
		document.setCharacterAttributes(text1.length(), name1.length(), attributes, false);
		document.setCharacterAttributes(text1.length() + name1.length() + text2.length() , name2.length(), attributes, false);
		// set the text area non editable and change the color of the background
		jtaTrackNames.setEditable(false);
		jtaTrackNames.setBackground(getContentPane().getBackground());
		
		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1;
		c.weighty = 1;		
		c.anchor = GridBagConstraints.CENTER;
		add(jtaTrackNames, c);

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
		setModal(true);
		getRootPane().setDefaultButton(jbOk);
		setTitle("Correlation Report");
	}


	/**
	 * Show a {@link CorrelationReportDialog} containing the specified data
	 * @param parent parents component in which the dialog is shown
	 * @param correlations array containing the correlation for each chromosome plus a row with the total correlation
	 * @param name1 name of the first track
	 * @param name2 name of the second track
	 */
	public static void showDialog(Component parent, Double[] correlations, String name1, String name2) {
		new CorrelationReportDialog(parent, correlations, name1, name2).setVisible(true);
	}
}
