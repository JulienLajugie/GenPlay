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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.nucleotideListView.TwoBitListView;

import java.io.IOException;
import java.io.RandomAccessFile;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link TwoBitListView} objects.
 * @author Julien Lajugie
 */
public class TwoBitListViewBuilder implements Stoppable {

	/** True if the file extraction needs to be stopped */
	private boolean needToBeStopped = false;

	/** 2bit random access file */
	private final RandomAccessFile raf;

	/** Path to the 2bit file (used for the serialization) */
	private final String filePath;

	/** The offset of the sequence data relative to the start of the file */
	private final int offset;

	/** Genome name for a multi genome project */
	private final String genomeName;

	/** Allele type for a multi genome project */
	private final AlleleType alleleType;

	/** Chromosome of the current list */
	private final Chromosome chromosome;

	/** True if the order of the bytes of multi-bytes entities need to be reversed  */
	private final boolean reverseBytes;


	/**
	 * Creates an instance of {@link TwoBitListViewBuilder}
	 * @param raf 2bit random access file
	 * @param filePath the path to the 2bit file
	 * @param offset the offset of the sequence data relative to the start of the file
	 * @param genomeName the genome name for a multi genome project
	 * @param alleleType the allele type for a multi genome project
	 * @param chromosome the chromosome of the current list
	 * @param reverseBytes true if the order of the bytes of multi-bytes entities need to be reversed
	 */
	public TwoBitListViewBuilder(
			RandomAccessFile raf,
			String filePath,
			int offset,
			String genomeName,
			AlleleType alleleType,
			Chromosome chromosome,
			boolean reverseBytes
			) {
		this.raf = raf;
		this.filePath = filePath;
		this.offset = offset;
		this.genomeName = genomeName;
		this.alleleType = alleleType;
		this.chromosome = chromosome;
		this.reverseBytes = reverseBytes;
	}


	/**
	 * Extract the information about a sequence from a 2bit sequence file and generate a ListView for a chromosome
	 * @return a {@link TwoBitListView}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ListView<Nucleotide> getListView() throws IOException, InterruptedException {
		int dnaSize;
		int[] nBlockStarts;
		int[] nBlockSizes;
		raf.seek(offset);
		if (reverseBytes) {
			dnaSize = Integer.reverseBytes(raf.readInt());
		} else {
			dnaSize = raf.readInt();
		}
		int nBlockCount = 0;
		if (reverseBytes) {
			nBlockCount = Integer.reverseBytes(raf.readInt());
		} else {
			nBlockCount = raf.readInt();
		}
		nBlockStarts = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockStarts[i] = Integer.reverseBytes(raf.readInt());
			} else {
				nBlockStarts[i] = raf.readInt();
			}
		}
		nBlockSizes = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockSizes[i] = Integer.reverseBytes(raf.readInt());
			} else {
				nBlockSizes[i] = raf.readInt();
			}
		}

		int maskBlockCount = 0;
		if (reverseBytes) {
			maskBlockCount = Integer.reverseBytes(raf.readInt());
		} else {
			maskBlockCount = raf.readInt();
		}
		int headerSize = 8 * (nBlockCount + maskBlockCount + 2);
		ListView<Nucleotide> lv = new TwoBitListView(filePath, headerSize, offset, dnaSize, nBlockStarts, nBlockSizes, genomeName, alleleType, chromosome, raf);
		return lv;
	}

	@Override
	public void stop() {
		needToBeStopped = true;
	}
}
