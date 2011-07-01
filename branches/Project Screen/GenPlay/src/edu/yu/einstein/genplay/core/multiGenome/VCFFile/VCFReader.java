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
package edu.yu.einstein.genplay.core.multiGenome.VCFFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.yu.einstein.genplay.core.multiGenome.tabixAPI.Iterator;
import edu.yu.einstein.genplay.core.multiGenome.tabixAPI.TabixReader;


/**
 * This class handles VCF files.
 * It indexes information to perform fast queries.
 * It also get VCF header information.
 * @author Nicolas
 */
public class VCFReader {

	private final 	File 					vcf;		// Path of the VCF file
	private 		TabixReader 			vcfParser;	// Tabix object for the VCF file (Tabix Java API)
	private 		Map<String, String> 	headerInfo;	// Header main information
	private 		Map<String, Map<Integer, Map<String, String>>> 	headerAttributes;	// Header attributes information
	private			List<String>			columnNames;	// All column header names
	private			List<String>			fixedColumn;	// Fixed header names included in the VCF file
	private			List<String>			genomeNames;	// Dynamic header names included in the VCF file (raw genome names)


	/**
	 * Constructor of {@link VCFReader}
	 * @param vcfPath	the path of the VCF file
	 * @throws IOException
	 */
	public VCFReader (File vcf) throws IOException {
		this.vcf = vcf;
		initFixedColumnList();
		indexVCFFile();
		processHeader();
	}


	/**
	 * Initializes column header list.
	 */
	private void initFixedColumnList () {
		fixedColumn = new ArrayList<String>();
		fixedColumn.add("CHROM");
		fixedColumn.add("POS");
		fixedColumn.add("ID");
		fixedColumn.add("REF");
		fixedColumn.add("ALT");
		fixedColumn.add("QUAL");
		fixedColumn.add("FILTER");
		fixedColumn.add("INFO");
		fixedColumn.add("FORMAT");
	}


	/**
	 * This method indexs the VCF file using the Tabix Java API.
	 * @throws IOException
	 */
	private void indexVCFFile () throws IOException {
		if (!isVCFIndexed ()) {
			this.vcfParser = new TabixReader(vcf.getPath());
		}
	}


	/**
	 * This method checks if the VCF has been indexed.
	 * @return
	 */
	private boolean isVCFIndexed () {
		if (this.vcfParser != null) {
			return true;
		}
		return false;
	}


	/**
	 * This method reads and saves the vcf header information
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void processHeader () throws FileNotFoundException, IOException {
		boolean valid = true;
		headerInfo = new HashMap<String, String>();
		headerAttributes = new HashMap<String, Map<Integer, Map<String, String>>>();
		Integer row = 0;
		while (valid) {
			String line = vcfParser.readLine();
			if (line.length() > 0) {
				if (line.substring(0, 2).equals("##")) {
					int equalChar = line.indexOf("=");
					String type = line.substring(2, equalChar);
					if (type.equals("INFO") || type.equals("ALT") || type.equals("FILTER") || type.equals("FORMAT")) {
						Map<String, String> info = parseVCFHeaderInfo(line.substring(equalChar + 2, line.length() - 1));

						if (headerAttributes.containsKey(type)) {
							headerAttributes.get(type).put(headerAttributes.get(type).size(), info);
						} else {
							Map<Integer, Map<String, String>> attribute = new HashMap<Integer, Map<String, String>>();
							attribute.put(0, info);
							headerAttributes.put(type, attribute);
						}
						row++;
					} else {
						headerInfo.put(type, line.substring(equalChar + 1, line.length() - 1));
					}
				} else {
					valid = false;
					if (line.substring(0, 1).equals("#")) {
						columnNames = new ArrayList<String>();
						for (String name: line.substring(1, line.length()).split("[\t]")) {
							columnNames.add(name);
						}
					}
				}
			}
		}
	}


	/**
	 * This method parses the content of attributes header information.
	 * @param line
	 * @return
	 */
	private Map<String, String> parseVCFHeaderInfo (String line) {
		Map<String, String> info = new HashMap<String, String>();
		String details[] = line.split(",");
		String detail[];
		for (String s: details) {
			detail = s.split("=");
			if (detail.length == 2) {
				if (detail[0].equals("Description")) {
					int start = line.indexOf("\"") + 1;
					int stop = line.indexOf("\"", start);
					String element = line.substring(start, stop);
					info.put(detail[0], element);
				} else {
					info.put(detail[0], detail[1]);
				}
			}
		}
		return info;
	}


	/**
	 * Shows the main header information
	 */
	public void showHeaderInfo () {
		System.out.println("===== Header information");
		for (String key: headerInfo.keySet()) {
			System.out.println(key + ": " + headerInfo.get(key));
		}
	}


	/**
	 * Shows the attributes header information
	 */
	public void showHeaderAttributes () {
		System.out.println("===== Header attributes information");
		String line;
		for (String type: headerAttributes.keySet()) {
			Map<Integer, Map<String, String>> typeValues = headerAttributes.get(type);
			for (Integer number: typeValues.keySet()) {
				Map<String, String> rows = typeValues.get(number);
				line = type + " " + number + ": ";
				for (String attributes: rows.keySet()) {
					line = line + attributes + " = " + rows.get(attributes) + "; ";
				}
				System.out.println(line);
			}
		}
	}


	/**
	 * Shows the column names
	 */
	public void showColumnNames () {
		System.out.println("===== Column names");
		String line = "";
		for (String name: columnNames) {
			line = line + name + "\t";
		}
		System.out.println(line);
	}


	/**
	 * This method performs queries from a file to the indexed VCF file.
	 * @param queriesFile	file containing queries information.
	 * @return a list of query results
	 * @throws IOException
	 */
	public List<List<Map<String,Object>>> queriesFromFile (String queriesFile) throws IOException {
		return queriesFromFile(queriesFile, columnNames);
	}


	/**
	 * This method performs queries from a file to the indexed VCF file.
	 * @param queriesFile	file containing queries information.
	 * @param fields		filter to select specific fields
	 * @return a list of query results
	 * @throws IOException
	 */
	public List<List<Map<String,Object>>> queriesFromFile (String queriesFile, List<String> fields) throws IOException {
		BufferedReader in = 	new BufferedReader(
				new InputStreamReader(
						new FileInputStream (
								new File(queriesFile)
						)
				)
		);
		String line;
		List<List<Map<String, Object>>> result = new ArrayList<List<Map<String,Object>>>();
		while( (line = in.readLine()) != null){
			String infoString[] = line.split("[\t]");
			int infoInt[] = new int[infoString.length];
			for (int i = 0; i < infoInt.length; i++) {
				infoInt[i] = Integer.parseInt(infoString[i]);
			}
			result.add(query(infoString[0], infoInt[1], infoInt[2], fields));
		}
		return result;
	}


	/**
	 * Performs a query on the indexed VCF file.
	 * @param chr		chromosome
	 * @param start		start position
	 * @param stop		stop position
	 * @return query 	results list
	 * @throws IOException
	 */
	public List<Map<String, Object>> query (String chr, int start, int stop) throws IOException {
		return query(chr, start, stop, columnNames);
	}


	/**
	 * Performs a query on the indexed VCF file.
	 * @param chr		chromosome
	 * @param start		start position
	 * @param stop		stop position
	 * @param fields	filter to select specific fields
	 * @return query 	results list
	 * @throws IOException
	 */
	public List<Map<String, Object>> query (String chr, int start, int stop, List<String> fields) throws IOException {
		Iterator iter = vcfParser.query(chr + ":" + start + "-" + stop);
		String line;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		while (iter != null && (line = iter.next()) != null){
			String info[] = line.split("[\t]");
			Map<String, Object> row = new HashMap<String, Object>();
			for (String columnName: columnNames) {
				if (fields.indexOf(columnName) != -1) {
					row.put(columnName, info[columnNames.indexOf(columnName)]);
				}
			}
			result.add(row);
		}
		return result;
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


	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}


	/**
	 * @return the columnNames
	 */
	public List<String> getRawGenomesNames() {
		if (genomeNames == null) {
			List<String> list = new ArrayList<String>();
			for (String s: columnNames) {
				if (!fixedColumn.contains(s)) {
					list.add(s);
				}
			}
			Collections.sort(list);
			genomeNames = list;
		}
		return genomeNames;
	}

}