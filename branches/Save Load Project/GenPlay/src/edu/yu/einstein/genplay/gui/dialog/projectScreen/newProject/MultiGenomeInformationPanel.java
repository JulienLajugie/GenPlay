package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;


public class MultiGenomeInformationPanel extends JPanel {

	private static final long serialVersionUID = 6394382682521718513L;
	
	//private static final Dimension LABEL_DIM = new Dimension((ProjectScreenManager.getVCFDim().width / 2), 20);
	private static final Dimension LABEL_DIM = new Dimension(100, 25);
	private static final Dimension VALUE_DIM = new Dimension(20, 20);

	private JLabel groupLabel;
	private JLabel genomeLabel;
	private JLabel vcfLabel;
	private static JLabel groupValue;
	private static JLabel genomeValue;
	private static JLabel vcfValue;
	
	
	
	protected  MultiGenomeInformationPanel () {
		
		Dimension paneDim = new Dimension(ProjectScreenManager.getVCFDim().width, 70);
		setSize(paneDim);
		setMinimumSize(paneDim);
		setMaximumSize(paneDim);
		setPreferredSize(paneDim);
		
		setBackground(ProjectScreenManager.getVCFColor());
		
		// Label
		groupLabel = new JLabel("Group :");
		genomeLabel = new JLabel("Genome :");
		vcfLabel = new JLabel("VCF :");
		setLabelSize(groupLabel, LABEL_DIM);
		setLabelSize(genomeLabel, LABEL_DIM);
		setLabelSize(vcfLabel, LABEL_DIM);
		
		// Value
		groupValue = new JLabel("0");
		genomeValue = new JLabel("0");
		vcfValue = new JLabel("0");
		setLabelSize(groupValue, VALUE_DIM);
		setLabelSize(genomeValue, VALUE_DIM);
		setLabelSize(vcfValue, VALUE_DIM);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		Insets labelInsets = new Insets(0, 0, 3, 0);
		Insets valueInsets = new Insets(0, 0, 3, 0);
		setLayout(gbl);
		
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = labelInsets;
		add(groupLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = valueInsets;
		add(groupValue, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = labelInsets;
		add(genomeLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = valueInsets;
		add(genomeValue, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = labelInsets;
		add(vcfLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = valueInsets;
		add(vcfValue, gbc);
		
	}
	
	
	private void setLabelSize (JLabel label, Dimension dim) {
		label.setSize(dim);
		label.setMinimumSize(dim);
		label.setMaximumSize(dim);
		label.setPreferredSize(dim);
	}
	
	
	public static void setInformation (int group, int genome, int vcf) {
		groupValue.setText("" + group);
		genomeValue.setText("" + genome);
		vcfValue.setText("" + vcf);
	}
	
}
