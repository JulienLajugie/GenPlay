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
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantComparator;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGVariantListForDisplay implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3317488112661108128L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private MGAlleleForDisplay 		alleleForDisplay;
	private Chromosome				chromosome;
	private VariantType 			type;
	private List<VariantInterface> 	variantList;


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
		//this.vcfReaderList = ProjectManager.getInstance().getMultiGenome().getReaders(alleleForDisplay.getGenomeInformation().getName(), this.type);
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
	 * @param variant a variant
	 * @return all information about the variant (from the vcf)
	 */
	public MGPosition getFullVariantInformation (VariantInterface variant) {
		if (!(variant instanceof MixVariant)) {
			List<VCFReader> vcfReaderList = ProjectManager.getInstance().getMultiGenome().getReaders(alleleForDisplay.getGenomeInformation().getName(), this.type);
			List<String> columns = vcfReaderList.get(0).getFixedColumn();
			columns.add(FormattedMultiGenomeName.getRawName(alleleForDisplay.getGenomeInformation().getName()));
			int start = variant.getReferenceGenomePosition();
			List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
			VCFReader requiredReader = null;
			for (VCFReader reader: vcfReaderList) {
				List<Map<String, Object>> resultsTmp = null;
				try {
					resultsTmp = reader.query(chromosome.getName(), start - 1, start + 1, columns);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (resultsTmp.size() > 0) {
					results.add(resultsTmp.get(0));
					requiredReader = reader;
				}
			}

			int size = results.size();
			switch (size) {
			case 1:
				MGPosition position = new MGPosition(variant, results.get(0), requiredReader);
				return position;
			case 0:
				System.err.println("MGVariantListForDisplay.getFullVariantInformation: No variant found");
				break;
			default:
				System.err.println("MGVariantListForDisplay.getFullVariantInformation: Many variant found: " + size);
				break;
			}
		} else {
			MGPosition position = new MGPosition(variant, null, null);
			return position;
		}
		return null;
	}


	/**
	 * Show the information of the {@link MGAlleleForDisplay}
	 */
	public void show () {
		System.out.println("Variant type: " + type);
		String readerInfo = "Readers:";
		List<VCFReader> vcfReaderList = ProjectManager.getInstance().getMultiGenome().getReaders(alleleForDisplay.getGenomeInformation().getName(), this.type);
		for (VCFReader reader: vcfReaderList) {
			readerInfo += " " + reader.getFile().getName();
		}
		System.out.println(readerInfo);
		System.out.println("Variant list size: " + variantList.size());
		int cpt = 0;
		for (VariantInterface variant: variantList) {
			if (cpt < 10) {
				variant.show();
				//cpt++;
			}
		}
	}

}
