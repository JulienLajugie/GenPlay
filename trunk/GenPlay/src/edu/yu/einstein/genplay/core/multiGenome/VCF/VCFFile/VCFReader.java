/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jannot.tabix.Iterator;
import net.sf.jannot.tabix.TabixReader;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFReader {

	private transient	TabixReader 			vcfParser;		// Tabix object for the VCF file (Tabix Java API)
	private	List<String>						columnNames;		// All column header names


	/**
	 * Constructor of {@link VCFReader}
	 */
	protected VCFReader () {
		vcfParser = null;
	}


	/**
	 * Indexes the file creating using the Tabix API
	 * @param file
	 * @throws IOException
	 */
	protected void indexVCFFile (File file) throws IOException {
		this.vcfParser = new TabixReader(file.getPath());
	}


	/**
	 * Performs a query on the indexed VCF file for a whole chromosome.
	 * @param chromosome	the chromosome
	 * @return	the results list
	 * @throws IOException
	 */
	public List<String> query (Chromosome chromosome) throws IOException {
		return query(chromosome.getName(), 0, chromosome.getLength());
	}


	/**
	 * Performs a query on the indexed VCF file.
	 * @param chr		chromosome
	 * @param start		start position
	 * @param stop		stop position
	 * @return query 	results list
	 * @throws IOException
	 */
	public List<String> query (String chr, int start, int stop) throws IOException {
		Iterator iter = vcfParser.query(chr + ":" + start + "-" + stop);
		List<String> result = new ArrayList<String>();
		String line;
		while ((iter != null) && ((line = iter.next()) != null)){
			result.add(line);
		}
		return result;
	}


	/**
	 * Performs a query on the first chromosome of the indexed VCF file and return the 10 first results.
	 * @return query 	results list
	 * @throws IOException
	 */
	public List<Map<String, Object>> shortQuery () throws IOException {
		Iterator iter = vcfParser.shortQuery(0);
		List<String> fields = new ArrayList<String>();
		fields.add(VCFColumnName.REF.toString());
		fields.add(VCFColumnName.ALT.toString());
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int cpt = 0;
		String line;
		while ((iter != null) && ((line = iter.next()) != null) && (cpt < 10)){
			String[] info = Utils.splitWithTab(line);
			Map<String, Object> row = new HashMap<String, Object>();
			for (String columnName: columnNames) {
				if (fields.indexOf(columnName) != -1) {
					row.put(columnName, info[columnNames.indexOf(columnName)]);
				}
			}
			result.add(row);
			cpt++;
		}
		return result;
	}


	/**
	 * @return the vcfParser
	 */
	public TabixReader getVCFParser() {
		return vcfParser;
	}


	/**
	 * @param columnNames the columnNames to set
	 */
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}


	/**
	 * Shows the result of a query.
	 * A query can have more than one result.
	 * @param result the list of result
	 */
	public static void showQueryResults (List<Map<String, Object>> result) {
		System.out.println("===== Query results");
		String line;
		if (result.size() == 0) {
			System.out.println("no result");
		} else {
			for (int i = 0; i < result.size(); i++) {
				line = "Result " + (i + 1) + ": ";
				for (String name: result.get(i).keySet()) {
					line = line + name + " = " + result.get(i).get(name) + ", ";
				}
				System.out.println(line);
			}
		}
	}


	/**
	 * Shows the result of several query.
	 * @param result the list of result
	 */
	public static void showQueriesFileResults (List<List<Map<String, Object>>> result) {
		System.out.println("===== Queries file results");
		for (List<Map<String, Object>> queryResults: result) {
			showQueryResults(queryResults);
		}
	}
}
