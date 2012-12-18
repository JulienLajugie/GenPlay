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
package edu.yu.einstein.genplay.core.multiGenome.data.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.comparator.VariantComparator;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGChromosomeContent;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGFileContentManager;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGLineContent;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.DeletionVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.InsertionVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.NoCallVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.operation.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayListBuilder {


	private final String referenceGenomeName;
	private final String metaGenomeName;

	private List<List<Variant>> variants;
	private String genomeName;
	private List<VariantType> types;
	private Chromosome chromosome;
	private final MGFileContentManager contentManager;

	private MGChromosomeContent currentContent;


	/**
	 * Constructor of {@link VariantDisplayListBuilder}
	 */
	public VariantDisplayListBuilder () {
		contentManager = ProjectManager.getInstance().getMultiGenomeProject().getFileContentManager();
		referenceGenomeName = ProjectManager.getInstance().getAssembly().getDisplayName();
		metaGenomeName = FormattedMultiGenomeName.META_GENOME_NAME;
	}


	/**
	 * @param genomeName the name of a genome
	 * @param types a list of {@link VariantType}
	 * @return the list of {@link Variant} for the current chromosome
	 */
	public List<List<Variant>> getList (String genomeName, List<VariantType> types) {
		return getList(genomeName, types, ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome());
	}


	/**
	 * @param genomeName the name of a genome
	 * @param types a list of {@link VariantType}
	 * @param chromosome a chromosome
	 * @return the list of {@link Variant}
	 */
	public List<List<Variant>> getList (String genomeName, List<VariantType> types, Chromosome chromosome) {
		variants = new ArrayList<List<Variant>>();
		this.genomeName = genomeName;
		this.types = types;
		this.chromosome = chromosome;


		List<VCFFile> fileList = getValidFileList();
		for (VCFFile file: fileList) {
			currentContent = contentManager.getContent(file, chromosome);
			List<List<Variant>> list = getVariantList();
			addFromListToList(variants, list);
		}
		for (List<Variant> list: variants) {
			Collections.sort(list, new VariantComparator());
		}

		return variants;
	}



	private List<VCFFile> getValidFileList () {
		List<VCFFile> validFiles = new ArrayList<VCFFile>();
		List<VCFFile> fileList = contentManager.getFileList();
		for (VCFFile file: fileList) {
			boolean hasData = false;
			for (VariantType type: types) {
				if (file.canManage(genomeName, type)) {
					hasData = true;
				}
			}
			if (hasData) {
				validFiles.add(file);
			}
		}

		return validFiles;
	}





	private List<List<Variant>> getVariantList () {
		List<List<Variant>> result = getEmptyVariantList(currentContent);

		int lineNumber = currentContent.getSize();
		MGLineContent line = new MGLineContent();
		for (int i = 0; i < lineNumber; i++) {
			line = currentContent.getPosition(line, i);
			byte[] genotype = line.getGenotypes().get(genomeName);
			if (defineVariantType(line)) {
				if (isHomozygoteReference(genotype)) {
					// TODO: insert two references using longest length of the required variations
				} else {
					byte[] correctedGenotype = getAdjustedGenotype(line);
					if (!isHomozygoteReference(correctedGenotype)) {			// the corrected genotype must contain at least one index that is not reference related
						List<List<Variant>> list = new ArrayList<List<Variant>>();
						for (int j = 0; j < genotype.length; j++) {
							Variant variant = null;
							byte alternativeIndex = genotype[j];
							if (alternativeIndex == MGSynchronizer.NO_CALL) {
								int start = ShiftCompute.getPosition(referenceGenomeName, null, line.getReferenceGenomePosition(), chromosome, metaGenomeName);
								variant  = new NoCallVariant(currentContent, i, start);
							} else if (alternativeIndex >= 0) {
								variant = currentContent.getVariants().getVariant(alternativeIndex, i);
							}
							addToList(list, j, variant);
						}
						list = processCurrentVariants(list);

						addFromListToList(result, list);
					}
				}
			}
		}
		return result;
	}



	private void addToList (List<List<Variant>> list, int genotypeIndex, Variant variant) {
		if (variant != null) {
			// Add missing lists
			int add = (genotypeIndex - list.size()) + 1;
			for (int i = 0; i < add; i++) {
				list.add(new ArrayList<Variant>());
			}

			// Create the adjusted reference variant
			Variant reference = new ReferenceVariant(currentContent, variant.getReferencePositionIndex(), variant.getStart(), variant.getStop(), getReferenceType(variant));

			// Add the variant and the reference(s)
			for (int i = 0; i < list.size(); i++) {
				if (i == genotypeIndex) {
					list.get(i).add(variant);
				} else {
					list.get(i).add(reference);
				}
			}
		}
	}


	private void addFromListToList (List<List<Variant>> list01, List<List<Variant>> list02) {
		// Add missing lists
		int add = list02.size() - list01.size();
		for (int i = 0; i < add; i++) {
			list01.add(new ArrayList<Variant>());
		}
		for (int i = 0; i < list02.size(); i++) {
			for (Variant variant: list02.get(i)) {
				list01.get(i).add(variant);
			}
		}
	}


	private VariantType getReferenceType (Variant variant) {
		if (variant instanceof InsertionVariant) {
			return VariantType.REFERENCE_INSERTION;
		} else if (variant instanceof DeletionVariant) {
			return VariantType.REFERENCE_DELETION;
		} else if (variant instanceof SNPVariant) {
			return VariantType.REFERENCE_SNP;
		} else if (variant instanceof NoCallVariant) {
			return VariantType.REFERENCE_NO_CALL;
		}
		return null;
	}


	private List<List<Variant>> getEmptyVariantList (MGChromosomeContent chromosomeContent) {
		List<List<Variant>> list = new ArrayList<List<Variant>>();
		list.add(new ArrayList<Variant>());
		return list;
	}



	/**
	 * Create a new genotype based on the native one.
	 * All alternative indexes not required in the original genotype are replaced by reference indexes in the new genotype.
	 * Examples:
	 *  - A genotype REF/INS where insertions are requested will be corrected as REF/INS.
	 *  - A genotype REF/INS where deletions are requested will be corrected as REF/REF, meaning there is nothing to process here.
	 *  - A genotype REF/INS/DEL where insertions are requested will be corrected as REF/INS/REF.
	 * @param line the {@link MGLineContent}
	 * @return the corrected genotype
	 */
	private byte[] getAdjustedGenotype (MGLineContent line) {
		byte[] genotype = line.getGenotypes().get(genomeName);
		byte[] newGenotype = new byte[genotype.length];
		int[] alternatives = line.getAlternatives();
		for (int i = 0; i < genotype.length; i++) {

			if (genotype[i] == MGSynchronizer.NO_CALL) {
				if (types.contains(VariantType.NO_CALL)) {
					newGenotype[i] = MGSynchronizer.NO_CALL;
				}
			} else if (genotype[i] == MGSynchronizer.REFERENCE) {
				newGenotype[i] = MGSynchronizer.REFERENCE;
			} else {
				boolean insert = false;
				int alternative = alternatives[genotype[i]];
				if (alternative > 0) {
					if (types.contains(VariantType.INSERTION)) {
						insert = true;
					}
				} else if (alternative < 0) {
					if (types.contains(VariantType.DELETION)) {
						insert = true;
					}
				} else {
					if (types.contains(VariantType.SNPS)) {
						insert = true;
					}
				}
				if (insert) {
					newGenotype[i] = genotype[i];
				} else {
					newGenotype[i] = MGSynchronizer.REFERENCE;
				}
			}
		}
		return newGenotype;
	}


	private boolean isHomozygote (byte[] genotype) {
		int length = genotype.length;

		// A genotype of one/zero element is considered as homozygote
		if (length < 2) {
			return true;
		}

		// Try to see if one allele has a different alternative than the next one
		for (int i = 0; i < length; i++) {
			int nextIndex = i + 1;
			if (nextIndex < length) {
				if (genotype[i] != genotype[nextIndex]) {
					return false;
				}
			}
		}
		return true;
	}


	private boolean isHomozygoteReference (byte[] genotype) {
		if (isHomozygote(genotype)) {
			return (genotype[0] == MGSynchronizer.REFERENCE);
		}
		return false;
	}


	private boolean defineVariantType (MGLineContent line) {
		int[] alternatives = line.getAlternatives();
		for (int alternative: alternatives) {
			if (alternative > 0) {
				if (types.contains(VariantType.INSERTION)) {
					return true;
				}
			} else if (alternative < 0) {
				if (types.contains(VariantType.DELETION)) {
					return true;
				}
			} else {
				if (types.contains(VariantType.SNPS)) {
					return true;
				}
			}
		}
		return false;
	}


	private List<List<Variant>> processCurrentVariants (List<List<Variant>> list) {
		List<List<Variant>> newList = new ArrayList<List<Variant>>();
		for (List<Variant> current: list) {
			List<Variant> newCurrent = new ArrayList<Variant>();
			if (current.size() == 1) {
				newCurrent.add(current.get(0));
			} else if (current.size() > 1) {
				Collections.sort(current, new VariantComparator());
				int currentIndex = 1;
				Variant previousVariant = current.get(0);
				Variant currentVariant = current.get(currentIndex);
				newCurrent.add(previousVariant);
				while (currentVariant != null) {

					if (currentVariant instanceof ReferenceVariant) {
						if (!(previousVariant instanceof ReferenceVariant)) {
							currentVariant.setStart(previousVariant.getStop());
							if (currentVariant.getLength() > 0) {
								newCurrent.add(currentVariant);
							}
						}
					} else {
						newCurrent.add(currentVariant);
					}

					currentIndex++;
					if (currentIndex < current.size()) {
						previousVariant = current.get(currentIndex - 1);
						currentVariant = current.get(currentIndex);
					} else {
						previousVariant = null;
						currentVariant = null;
					}
				}
			}
			newList.add(newCurrent);
		}
		return newList;
	}
}
