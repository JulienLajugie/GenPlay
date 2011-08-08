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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;
import edu.yu.einstein.genplay.gui.launcher.Launcher;

/**
 * This class provides a panel including combo boxes to choose an assembly.
 * @author Nicolas Fourel
 */
class AssemblyPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -5768796908632202321L;

	private final static 	String 		ICON_PATH = "edu/yu/einstein/genplay/resource/tools.png"; 	// path of the tools icon
	private 	 			ImageIcon	icon; 

	private static final int COMBO_WIDTH = 200;	// Combo box width value
	private static final int COMBO_HEIGTH = 20;	// Combo box height value

	private static final String CLADE_DEFAULT_VALUE = "mammal";	// Default clade value
	private static final String GENOME_DEFAULT_VALUE = "human";	// Default genome value

	private JLabel 		jlClade;			// Clade label
	private JLabel 		jlGenome;			// Genome label
	private JLabel 		jlAssembly;			// Assembly label
	private JComboBox 	jcClade;			// Clade combo box
	private JComboBox 	jcGenome;			// Genome combo box
	private JComboBox 	jcAssembly;			// Assembly combo box
	private String 		selectedClade;		// Selected Clade
	private String 		selectedGenome;		// Selected Genome
	private String 		selectedAssembly;	// Selected Assembly

	private ChromosomeChooser 					chromosomeChooser;	// Chromosome chooser object
	private JButton 							jbChromosome;		// Button to create a chromosome chooser object
	private Map<Integer, Map<Integer, Object>> 	data;				// Data used for the chromosome selection


	/**
	 * Constructor of {@link AssemblyPanel}
	 */
	protected AssemblyPanel () {
		//Size Panel
		setSize(ProjectScreenManager.getAssemblyDim());
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		//Labels
		jlClade = new JLabel("Clade:");
		jlGenome = new JLabel("Genome:");
		jlAssembly = new JLabel("Assembly:");

		//Combo boxes
		jcClade = new JComboBox();
		jcGenome = new JComboBox();
		jcAssembly = new JComboBox();

		//Size Combo boxes
		Dimension comboDim = new Dimension(COMBO_WIDTH, COMBO_HEIGTH);
		jcClade.setPreferredSize(comboDim);
		jcGenome.setPreferredSize(comboDim);
		jcAssembly.setPreferredSize(comboDim);

		//Boxes color
		jcClade.setBackground(ProjectScreenManager.getAssemblyColor());
		jcGenome.setBackground(ProjectScreenManager.getAssemblyColor());
		jcAssembly.setBackground(ProjectScreenManager.getAssemblyColor());

		//Chromosome selection button
		icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource(ICON_PATH)));
		jbChromosome = new JButton(icon);
		jbChromosome.setPreferredSize(new Dimension(20, 20));
		jbChromosome.setMargin(new Insets(0, 0, 0, 0));

		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		//Insets
		Insets labelInsets = new Insets (10, 28, 10, 30);
		Insets boxInsets = new Insets (0, 0, 0, 0);
		Insets buttonInsets = new Insets (0, 5, 0, 0);

		//jlClade
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weighty = 0;
		add(jlClade, gbc);

		//jcClade
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = boxInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jcClade, gbc);

		//jlGenome
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlGenome, gbc);

		//jcGenome
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = boxInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jcGenome, gbc);

		//jlAssembly
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlAssembly, gbc);

		//jcAssembly
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = boxInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jcAssembly, gbc);

		//jbChromosome
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = buttonInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jbChromosome, gbc);

		//Background
		setBackground(ProjectScreenManager.getAssemblyColor());

		//init boxes and data
		initClade ();
		initGenome ();
		initAssembly ();
		selectedAssembly = jcAssembly.getSelectedItem().toString();
		initData ();

		//Listeners
		jcClade.addActionListener(this);
		jcGenome.addActionListener(this);
		jbChromosome.addActionListener(this);
	}


	/**
	 * Initialization of the clade combo box.
	 */
	private void initClade () {
		List<String> clades = new ArrayList<String>(Launcher.getCladeList().keySet());
		Collections.sort(clades);
		for (String name: clades){
			jcClade.addItem(Launcher.getCladeList().get(name).getName());
		}
		jcClade.setSelectedItem(CLADE_DEFAULT_VALUE);
		selectedClade = jcClade.getSelectedItem().toString();
	}


	/**
	 * Initialization of the genome combo box.
	 */
	private void initGenome () {
		jcGenome.removeAllItems();
		List<String> genomes = new ArrayList<String>(Launcher.getCladeList().get(selectedClade).getGenomeList().keySet());
		Collections.sort(genomes);
		for (String name: genomes){
			jcGenome.addItem(Launcher.getCladeList().get(selectedClade).getGenomeList().get(name).getName());
		}
		jcGenome.setSelectedItem(GENOME_DEFAULT_VALUE);
		selectedGenome = jcGenome.getSelectedItem().toString();
	}


	/**
	 * Initialization of the assembly combo box
	 */
	private void initAssembly () {
		jcAssembly.removeAllItems();
		List<String> assemblies = new ArrayList<String>(Launcher.getCladeList().get(selectedClade).getGenomeList().get(selectedGenome).getAssemblyList().keySet());
		Collections.sort(assemblies);
		for (int i = (assemblies.size()-1); i >= 0; i--) {
			jcAssembly.addItem(Launcher.getCladeList().get(selectedClade).getGenomeList().get(selectedGenome).getAssemblyList().get(assemblies.get(i)).getDisplayName());
		}
		jcAssembly.setSelectedIndex(0);
	}


	/**
	 * This listener updates combo boxes when an action is performed.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == jcClade) {
			if (selectedClade == null || !selectedClade.equals(((JComboBox)arg0.getSource()).getSelectedItem().toString())) {
				selectedClade = ((JComboBox)arg0.getSource()).getSelectedItem().toString();
				initGenome();
			}
		} else if (arg0.getSource() == jcGenome) {
			if (jcGenome.getSelectedItem() != null) {
				if (selectedGenome == null || !selectedGenome.equals(((JComboBox)arg0.getSource()).getSelectedItem().toString())) {
					selectedGenome = ((JComboBox)arg0.getSource()).getSelectedItem().toString();
					initAssembly();
				}
			}
		} else if (arg0.getSource() == jbChromosome) {
			if (chromosomeChooser == null || !chromosomeChooser.isVisible()){
				if (!selectedAssembly.equals(jcAssembly.getSelectedItem().toString())) {
					initData ();
				}
				String title = "Chromosome chooser - " + selectedGenome + " - " + jcAssembly.getSelectedItem().toString();
				chromosomeChooser = new ChromosomeChooser(this, title, data);
				selectedAssembly = jcAssembly.getSelectedItem().toString();
				chromosomeChooser.setVisible(true);
			}
		}
	}


	/**
	 * This method initiates the data.
	 * Data need to be initiates for the first run.
	 */
	private void initData () {
		data = new HashMap<Integer, Map<Integer,Object>>();
		Map<Integer,Object> line;
		int row = 0;

		Assembly assembly = Launcher.getCladeList().get(selectedClade).getGenomeList().get(selectedGenome).getAssemblyList().get(findAssembly());
		List<String> chromosomeNames = new ArrayList<String>(assembly.getChromosomeList().keySet());
		Collections.sort(chromosomeNames, new ChromosomeComparator());
		for (String s: chromosomeNames) {
			line = new HashMap<Integer, Object>();
			line.put(0, row + 1);
			line.put(1, assembly.getChromosomeList().get(s).getName());
			line.put(2, assembly.getChromosomeList().get(s).getLength());
			line.put(3, true);
			data.put(row, line);
			row++;
		}
	}


	/**
	 * This method gets the label from the assembly box and convert it in order to get the original name.
	 * @return	the name of the selected assembly.
	 */
	protected String findAssembly () {
		Date date_tmp = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.US);
		String assembly_tmp = getSelectedAssembly();
		try {
			date_tmp = sdf.parse(assembly_tmp.substring(0, 8));
			sdf.applyPattern("yyyy MM");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String assembly = sdf.format(date_tmp);
		return assembly.concat(" ").concat(assembly_tmp.substring(10, assembly_tmp.length()-1));
	}


	/**
	 * @return the selectedClade
	 */
	protected String getSelectedClade() {
		return selectedClade;
	}


	/**
	 * @return the selectedGenome
	 */
	protected String getSelectedGenome() {
		return selectedGenome;
	}


	/**
	 * @return the selectedAssembly
	 */
	protected String getSelectedAssembly() {
		return jcAssembly.getSelectedItem().toString();
	}


	/**
	 * @param data the data to set
	 */
	protected void setData(Map<Integer, Map<Integer, Object>> data) {
		this.data = data;
	}


	/**
	 * @return the selected chromosome list
	 */
	protected Map<String, Chromosome> getNewChromosomeList() {
		if (!selectedAssembly.equals(jcAssembly.getSelectedItem().toString())) {
			initData ();
		}
		Map<String, Chromosome> chromosomeList = new HashMap<String, Chromosome>();
		for (Map<Integer, Object> row: data.values()) {
			if ((Boolean)row.get(3)) {
				chromosomeList.put(row.get(1).toString(), new Chromosome(row.get(1).toString(), Integer.parseInt(row.get(2).toString())));
			}
		}
		return chromosomeList;
	}


	/**
	 * Destruction of the chromosome chooser object
	 */
	protected void destruct() {
		this.chromosomeChooser = null;
	}

}