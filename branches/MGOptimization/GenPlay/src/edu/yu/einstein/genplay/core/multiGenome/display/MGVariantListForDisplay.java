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
package edu.yu.einstein.genplay.core.multiGenome.display;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.comparator.VariantComparator;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * This class manages a list of variants.
 * It knows its allele and for which chromosome the variants are about.
 * It also knows the type of variant it contains (insertions, deletions, SNPs).
 * 
 * This method contains methods for handling the variants.
 * The method getFullVariantInformation retrieves all the information about a variant from the related VCF file.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGVariantListForDisplay implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3317488112661108128L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private MGAlleleForDisplay 		alleleForDisplay;					// its allele
	private Chromosome				chromosome;							// its chromosome
	private VariantType 			type;								// type of the variants
	private List<VariantInterface> 	variantList;						// list of variants


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(alleleForDisplay);
		out.writeObject(chromosome);
		out.writeObject(type);
		out.writeObject(variantList);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		alleleForDisplay = (MGAlleleForDisplay) in.readObject();
		chromosome = (Chromosome) in.readObject();
		type = (VariantType) in.readObject();
		variantList = (List<VariantInterface>) in.readObject();
	}


	/**
	 * Constructor of {@link MGVariantListForDisplay}
	 * @param alleleForDisplay 	the allele for display object
	 * @param chromosome 		the chromosome where this list is
	 * @param type				type of variant
	 */
	public MGVariantListForDisplay (MGAlleleForDisplay alleleForDisplay, Chromosome chromosome, VariantType type) {
		this.alleleForDisplay = alleleForDisplay;
		this.chromosome = chromosome;
		this.type = type;
		this.variantList = new ArrayList<VariantInterface>();
	}


	/**
	 * @return the genome
	 */
	public MGAlleleForDisplay getAlleleForDisplay() {
		return alleleForDisplay;
	}


	/**
	 * @return the chromosome
	 */
	public Chromosome getChromosome() {
		return chromosome;
	}


	/**
	 * @return the type
	 */
	public VariantType getType() {
		return type;
	}


	/**
	 * @return the variantList
	 */
	public List<VariantInterface> getVariantList() {
		return variantList;
	}


	/**
	 * Empty the variant list
	 */
	public void clearVariantList () {
		this.variantList = new ArrayList<VariantInterface>();
	}


	/**
	 * Sorts the variant list
	 */
	public void sort () {
		Collections.sort(this.variantList, new VariantComparator());
	}


	/**
	 * Show the information of the {@link MGAlleleForDisplay}
	 */
	public void show () {
		String info = "";
		info += "Genome: " + alleleForDisplay.getGenomeInformation().getName() + "\n";
		info += "Allele: " + alleleForDisplay.getAlleleType() + "\n";
		info += "Chromosome: " + chromosome.getName() + " " + chromosome.getLength() +"\n";
		info += "Variant type: " + type + "\n";
		info += "Variant list size: " + variantList.size() + "\n";
		info += "Readers: ";
		List<VCFFile> vcfFileList = ProjectManager.getInstance().getMultiGenomeProject().getVCFFiles(alleleForDisplay.getGenomeInformation().getName(), this.type);
		for (VCFFile vcfFile: vcfFileList) {
			info += " " + vcfFile.getFile().getName();
		}
		//info += "\n";
		System.out.println("MGVariantListForDisplay.show()");
		System.out.println(info);

		/*int cpt = 0;
		for (VariantInterface variant: variantList) {
			if (cpt < 10) {
				variant.show();
				cpt++;
			}
		}*/
	}

}
