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
package edu.yu.einstein.genplay.core.DAS;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomicDataList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;



/**
 * Provides tools to connect and retrieve data from a DAS server
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASConnector {

	private final String 			serverAddress;		// address of a DAS Server
	private final ProjectChromosome projectChromosome; 	// Instance of the Chromosome Manager
	private String 					genomeName;			// for multi-genome project only.  Name of the genome on which the data were mapped
	private AlleleType 				alleleType;			// for multi-genome project only.  Type of allele for synchronization


	/**
	 * Creates an instance of {@link DASConnector}
	 * @param serverAddress address of a DAS server
	 */
	public DASConnector(String serverAddress) {
		// get rid of spaces if there is some
		serverAddress = serverAddress.trim();
		// add a "/" at the end of the address if there is none
		if (!serverAddress.substring(serverAddress.length() - 1).equals("/")) {
			serverAddress += "/";
		}
		this.serverAddress = serverAddress;
		projectChromosome = ProjectManager.getInstance().getProjectChromosome();
	}


	/**
	 * Retrieves a list of Data Sources from the DAS server
	 * @return a List of {@link DataSource}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public List<DataSource> getDataSourceList() throws IOException, ParserConfigurationException, SAXException {
		URL dsnURL = new URL(serverAddress + "dsn");
		URLConnection connection = dsnURL.openConnection();
		connection.setUseCaches(true);
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		DataSourceHandler dsh = new DataSourceHandler();
		connection.getInputStream();
		parser.parse(connection.getInputStream(), dsh);
		return dsh.getDataSourceList();
	}


	/**
	 * Retrieves a list of DAS types for a specified data Source
	 * @param dataSource a {@link DataSource}
	 * @return a List of {@link DataSource}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public List<DASType> getDASTypeList(DataSource dataSource) throws IOException, ParserConfigurationException, SAXException {
		URL dasTypesURL = new URL(serverAddress + dataSource.getID() + "/types");
		URLConnection connection = dasTypesURL.openConnection();
		connection.setUseCaches(true);
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		DASTypeHandler dth = new DASTypeHandler();
		parser.parse(connection.getInputStream(), dth);
		return dth.getDasTypeList();
	}


	/**
	 * Retrieves a list of DAS entry points for a specified data Source
	 * @param dataSource a {@link DataSource}
	 * @return a List of {@link EntryPoint}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public List<EntryPoint> getEntryPointList(DataSource dataSource) throws IOException, ParserConfigurationException, SAXException {
		URL entryPointURL = new URL(serverAddress + dataSource.getID() + "/entry_points");
		//System.out.println("Entry Point URL: " + entryPointURL);
		URLConnection connection = entryPointURL.openConnection();
		connection.setUseCaches(true);
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		EntryPointHandler eph = new EntryPointHandler();
		parser.parse(connection.getInputStream(), eph);
		return eph.getEntryPointList();
	}


	/**
	 * Retrieves a list of genes from a specified Data Source and a specified DAS Type
	 * @param dataSource a {@link DataSource}
	 * @param dasType a {@link DASType}
	 * @return a {@link GeneList}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public GeneList getGeneList(DataSource dataSource, DASType dasType) throws IOException, ParserConfigurationException, SAXException, InterruptedException, ExecutionException {
		//		if ((dasType.getPreferredFormat() != null) && (dasType.getPreferredFormat().equals(".link.psl;.bps;.psl;"))) {
		//			return getGeneListFromPSL(cm, dataSource, dasType);
		//		}
		List<EntryPoint> entryPointList = getEntryPointList(dataSource);
		List<List<Gene>> resultList = new ArrayList<List<Gene>>();
		for (Chromosome currentChromo: projectChromosome) {
			EntryPoint currentEntryPoint = findEntryPoint(entryPointList, currentChromo);
			// if we found a chromosome retrieve the data and
			// we create a genelist for this chromosome
			if (currentEntryPoint != null) {
				URL queryUrl = generateQuery(dataSource, currentEntryPoint, dasType);
				URLConnection connection = queryUrl.openConnection();
				connection.setUseCaches(true);
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setValidating(true);
				SAXParser parser = parserFactory.newSAXParser();
				GeneHandler gh = new GeneHandler(currentChromo);
				// if the current project is a muti genome project we set the
				// name of the genome that was used for the mapping of the data
				if (ProjectManager.getInstance().isMultiGenomeProject()) {
					gh.setGenomeName(genomeName);
					gh.setAlleleType(alleleType);
				}
				parser.parse(connection.getInputStream(), gh);
				List<Gene> currentGeneList = gh.getGeneList();
				resultList.add(currentGeneList);
			} else {
				resultList.add(null);
			}
		}
		boolean areExonsScored = false;
		for (List<Gene> currentList: resultList) {
			if (currentList != null) {
				// check if the exons are scored
				int i = 0;
				while (!areExonsScored && (i < currentList.size())) {
					int j = 0;
					double[] exonScores = currentList.get(i).getExonScores();
					while (!areExonsScored && (j < exonScores.length)) {
						if (exonScores[j] != 0) {
							areExonsScored = true;
						}
						j++;
					}
					i++;
				}
			}
		}
		// if the exons are not scored we set the exonScore field of every gene to null
		if (!areExonsScored) {
			for (List<Gene> currentList: resultList) {
				if (currentList != null) {
					for (Gene currentGene: currentList) {
						currentGene.setExonScores(null);
					}
				}
			}
		}
		return new SimpleGeneList(resultList, null, null);
	}


	/**
	 * Retrieves a list of genes from a specified Data Source and a specified DAS Type and a specified Data Range
	 * @param dataSource a {@link DataSource}
	 * @param dasType a {@link DASType}
	 * @param genomeWindow a {@link SimpleGenomeWindow}
	 * @return a {@link GeneList}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public GeneList getGeneList(DataSource dataSource, DASType dasType, SimpleGenomeWindow genomeWindow) throws IOException, ParserConfigurationException, SAXException, InterruptedException, ExecutionException
	{
		List<EntryPoint> entryPointList = getEntryPointList(dataSource);
		//System.out.println("1st Entry Point " + entryPointList.get(0));
		List<List<Gene>> resultList = new ArrayList<List<Gene>>();
		Chromosome currentChromo = genomeWindow.getChromosome();
		//System.out.println("Chormozome = " + currentChromo.getName());
		EntryPoint currentEntryPoint = findEntryPoint(entryPointList, currentChromo);
		if (currentEntryPoint != null)
		{
			URL queryUrl = generateQuery(dataSource, currentEntryPoint, dasType, genomeWindow);
			//System.out.println("URL: " + queryUrl);
			URLConnection connection = queryUrl.openConnection();
			connection.setUseCaches(true);
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(true);
			SAXParser parser = parserFactory.newSAXParser();
			GeneHandler gh = new GeneHandler(currentChromo);
			// if the current project is a muti genome project we set the
			// name of the genome that was used for the mapping of the data
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				gh.setGenomeName(genomeName);
				gh.setAlleleType(alleleType);
			}
			parser.parse(connection.getInputStream(), gh);
			List<Gene> currentGeneList = gh.getGeneList();
			resultList.add(currentGeneList);
		} else {
			resultList.add(null);
		}
		boolean areExonsScored = false;
		for (List<Gene> currentList: resultList) {
			if (currentList != null) {
				// check if the exons are scored
				int i = 0;
				while (!areExonsScored && (i < currentList.size())) {
					int j = 0;
					double[] exonScores = currentList.get(i).getExonScores();
					while (!areExonsScored && (j < exonScores.length)) {
						if (exonScores[j] != 0) {
							areExonsScored = true;
						}
						j++;
					}
					i++;
				}
			}
		}
		// if the exons are not scored we set the exonScore field of every gene to null
		if (!areExonsScored) {
			for (List<Gene> currentList: resultList) {
				if (currentList != null) {
					for (Gene currentGene: currentList) {
						currentGene.setExonScores(null);
					}
				}
			}
		}
		return new SimpleGeneList(resultList, null, null);
	}


	//	private GeneList getGeneListFromPSL(ChromosomeManager cm, DataSource dataSource, DASType dasType) throws IOException, ParserConfigurationException, SAXException {
	//		List<EntryPoint> entryPointList = getEntryPointList(dataSource);
	//		File tempFile = File.createTempFile("GenPlay", null);
	//		FileWriter fw = new FileWriter(tempFile);
	//		for (Chromosome currentChromo: cm) {
	//			EntryPoint currentEntryPoint = findEntryPoint(entryPointList, currentChromo);
	//			// if we found a chromosome retrieve the data and
	//			// we create a genelist for this chromosome
	//			if (currentEntryPoint != null) {
	//				URL queryUrl = generateQuery(dataSource, currentEntryPoint, dasType);
	//				URLConnection connection = queryUrl.openConnection();
	//				connection.setUseCaches(true);
	//				System.out.println(queryUrl.toString());
	//				connection.connect();
	//				for (List<String> currentList: connection.getHeaderFields().values()) {
	//					for (String currString: currentList) {
	//						System.out.println(currString);
	//					}
	//				}
	//				System.out.println(connection.getHeaderFields().values());
	//				//File f = new File(queryUrl.toString() + "/features");
	//				//InputStream is = connection.getInputStream();
	//				BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
	//				//FileReader fr = new FileReader(f);
	//				//InputStream is = new FileInputStream(f);
	//				byte[] test = new byte[1];
	//				int readInt = is.read(test);
	//				System.out.println(readInt);
	//				while (readInt != -1)	{
	//					System.out.println((char)readInt);
	//					//fw.write(test);
	//					readInt = is.read(test);
	//				}
	//				is.close();
	//			}
	//		}
	//		fw.close();
	//		System.out.println(tempFile.getAbsolutePath());
	//		PSLExtractor pslExtractor = new PSLExtractor(tempFile, null, cm);
	//		return pslExtractor.toGeneList();
	//	}


	/**
	 * Retrieves a list of ScoredChromosomeWindow from a specified Data Source and a specified DAS Type
	 * @param dataSource a {@link DataSource}
	 * @param dasType a {@link DASType}
	 * @return a {@link GenomicDataArrayList}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public ScoredChromosomeWindowList getSCWList(DataSource dataSource, DASType dasType) throws IOException, ParserConfigurationException, SAXException, InterruptedException, ExecutionException {
		List<EntryPoint> entryPointList = getEntryPointList(dataSource);
		ArrayList<List<ScoredChromosomeWindow>> resultList = new ArrayList<List<ScoredChromosomeWindow>>();
		for (Chromosome currentChromo: projectChromosome) {
			EntryPoint currentEntryPoint = findEntryPoint(entryPointList, currentChromo);
			// if we found a chromosome retrieve the data and
			// we create a genelist for this chromosome
			//System.out.println("Current Entry Point: " + currentEntryPoint.toString());
			if (currentEntryPoint != null) {
				URL queryUrl = generateQuery(dataSource, currentEntryPoint, dasType);
				//System.out.println("QueryURL: " + queryUrl);
				URLConnection connection = queryUrl.openConnection();
				connection.setUseCaches(true);
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setValidating(true);
				SAXParser parser = parserFactory.newSAXParser();
				SCWHandler scwh = new SCWHandler(currentChromo);
				// if the current project is a muti genome project we set the
				// name of the genome that was used for the mapping of the data
				if (ProjectManager.getInstance().isMultiGenomeProject()) {
					scwh.setGenomeName(genomeName);
					scwh.setAlleleType(alleleType);
				}
				parser.parse(connection.getInputStream(), scwh);
				List<ScoredChromosomeWindow> currentSCWList = scwh.getScoreChromosomeWindowList();
				resultList.add(currentSCWList);
			}
			resultList.add(null);
		}
		// Check if the list is scored
		boolean isScored = false;
		for (List<ScoredChromosomeWindow> currentList: resultList) {
			if (currentList != null) {
				Collections.sort(currentList);
				int i = 0;
				while (!isScored && (i < currentList.size())) {
					if (currentList.get(i).getScore() != 0) {
						isScored = true;
					}
					i++;
				}
			}
		}
		// if the list is not scored we set all the scores to 1
		if (!isScored) {
			for (List<ScoredChromosomeWindow> currentList: resultList) {
				if (currentList != null) {
					for (ScoredChromosomeWindow currentWindow: currentList) {
						currentWindow.setScore(1);
					}
				}
			}
		}
		return new SimpleSCWList(resultList);
	}

	/**
	 * Retrieves a list of ScoredChromosomeWindow from a specified Data Source and a specified DAS Type and a specified Data Range
	 * @param dataSource a {@link DataSource}
	 * @param dasType a {@link DASType}
	 * @param genomeWindow a {@link SimpleGenomeWindow}
	 * @return a {@link GenomicDataArrayList}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */

	public ScoredChromosomeWindowList getSCWList(DataSource dataSource, DASType dasType, SimpleGenomeWindow genomeWindow) throws IOException, ParserConfigurationException, SAXException, InterruptedException, ExecutionException {
		List<EntryPoint> entryPointList = getEntryPointList(dataSource);
		ArrayList<List<ScoredChromosomeWindow>> resultList = new ArrayList<List<ScoredChromosomeWindow>>();
		Chromosome currentChromo = genomeWindow.getChromosome();
		//System.out.println("Chormozome = " + currentChromo.getName());
		EntryPoint currentEntryPoint = findEntryPoint(entryPointList, currentChromo);
		// if we found a chromosome retrieve the data and
		// we create a genelist for this chromosome
		//System.out.println("Current Entry Point: " + currentEntryPoint.toString());
		if (currentEntryPoint != null) {
			URL queryUrl = generateQuery(dataSource, currentEntryPoint, dasType, genomeWindow);
			//System.out.println("QueryURL: " + queryUrl);
			URLConnection connection = queryUrl.openConnection();
			connection.setUseCaches(true);
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(true);
			SAXParser parser = parserFactory.newSAXParser();
			SCWHandler scwh = new SCWHandler(currentChromo);
			// if the current project is a muti genome project we set the
			// name of the genome that was used for the mapping of the data
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				scwh.setGenomeName(genomeName);
				scwh.setAlleleType(alleleType);
			}
			parser.parse(connection.getInputStream(), scwh);
			List<ScoredChromosomeWindow> currentSCWList = scwh.getScoreChromosomeWindowList();
			resultList.add(currentSCWList);
		} else {
			resultList.add(null);
		}
		// Check if the list is scored
		boolean isScored = false;
		for (List<ScoredChromosomeWindow> currentList: resultList) {
			if (currentList != null) {
				Collections.sort(currentList);
				int i = 0;
				while (!isScored && (i < currentList.size())) {
					if (currentList.get(i).getScore() != 0) {
						isScored = true;
					}
					i++;
				}
			}
		}
		// if the list is not scored we set all the scores to 1
		if (!isScored) {
			for (List<ScoredChromosomeWindow> currentList: resultList) {
				if (currentList != null) {
					for (ScoredChromosomeWindow currentWindow: currentList) {
						currentWindow.setScore(1);
					}
				}
			}
		}
		return new SimpleSCWList(resultList);
	}


	/**
	 * Searches if there is an entry point associated to the specify chromosome in the list of entries
	 * @param chr a {@link Chromosome}
	 * @return the name of the entry. Null if none
	 */
	private EntryPoint findEntryPoint(List<EntryPoint> entryPointList, Chromosome chr) {
		boolean found = false;
		int i = 0;
		// we search for an entry point corresponding to the current chromosome
		while ((i < entryPointList.size()) && (!found)) {
			//System.out.println(entryPointList.get(i).getID());
			if (chr.hasSameNameAs(entryPointList.get(i).getID())) {
				found = true;
			} else {
				i++;
			}
		}
		if (found) {
			// if the stop position of the entry point can't be greater than the one of the chromosome
			EntryPoint resultEntry = entryPointList.get(i);
			if (resultEntry.getStop() > chr.getLength()) {
				resultEntry.setStop(chr.getLength());
			}
			return resultEntry;
		} else {
			return null;
		}
	}

	/**
	 * Generates a query for all the data for a specified data source, entry point and das type
	 * @param dataSource a {@link DataSource}
	 * @param entryPoint an {@link EntryPoint}
	 * @param dasType a {@link DASType}
	 * @return a {@link URL} containing the query
	 * @throws MalformedURLException
	 */
	private URL generateQuery(DataSource dataSource, EntryPoint entryPoint, DASType dasType) throws MalformedURLException {
		String URLStr = new String(serverAddress);
		URLStr += dataSource.getID();
		URLStr += "/features?segment=";
		URLStr += entryPoint.getID();
		URLStr += ":";
		URLStr += entryPoint.getStart();
		URLStr += ",";
		URLStr += entryPoint.getStop();
		URLStr += ";type=";
		URLStr += dasType.getID();
		return new URL(URLStr);
	}

	/**
	 * Generates a query for all the data for a specified data source, entry point, das type and data range
	 * @param dataSource a {@link DataSource}
	 * @param entryPoint an {@link EntryPoint}
	 * @param dasType a {@link DASType}
	 * @param genomeWindow a {@link SimpleGenomeWindow}
	 * @return a {@link URL} containing the query
	 * @throws MalformedURLException
	 */
	private URL generateQuery(DataSource dataSource, EntryPoint entryPoint, DASType dasType, SimpleGenomeWindow genomeWindow) throws MalformedURLException {
		String URLStr = new String(serverAddress);
		URLStr += dataSource.getID();
		URLStr += "/features?segment=";
		URLStr += entryPoint.getID();
		URLStr += ":";
		if(genomeWindow.getStart() < entryPoint.getStart()) {
			genomeWindow.setStart(entryPoint.getStart());
		}
		URLStr += genomeWindow.getStart();
		URLStr += ",";
		if(genomeWindow.getStop() > entryPoint.getStop()) {
			genomeWindow.setStop(entryPoint.getStop());
		}
		URLStr += genomeWindow.getStop();
		URLStr += ";type=";
		URLStr += dasType.getID();
		//		System.out.println("New Start: " + genomeWindow.getStart());
		//		System.out.println("New Stop: " + genomeWindow.getStop());
		return new URL(URLStr);
	}


	/**
	 * @param genomeName for multi-genome project only.  Name of the genome on which the data were mapped
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * @return the name of the genome on which the data were mapped.  For multi-genome project only
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @return the alleleType
	 */
	public AlleleType getAlleleType() {
		return alleleType;
	}


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}


	//	public static void main(String[] args) {
	//		try {
	//			long startTime = System.currentTimeMillis();
	//			DASConnector dasc = new DASConnector("http://genome.ucsc.edu/cgi-bin/das/");
	//			//DASConnector dasc = new DASConnector("http://www.ensembl.org/das/");
	//			List<DataSource> dsList = dasc.getDataSourceList();
	//			DataSource dataSource = dsList.get(0);
	//			System.out.println(dataSource.getID());
	//			List<DASType> dasTypeList = dasc.getDASTypeList(dataSource);
	//			DASType dasType = dasTypeList.get(39);
	//			System.out.println(dasType.getID());
	//			//ScoredChromosomeWindowListInterface scwList = dasc.getSCWList(ChromosomeManager.getInstance(), dataSource, dasType);
	//			GeneList geneList = dasc.getGeneList(ChromosomeManager.getInstance(), dataSource, dasType);
	//			GeneListAsBedWriter glabw = new GeneListAsBedWriter(ChromosomeManager.getInstance(), new File("testDAS.bed"), geneList, "test");
	//			glabw.write();
	//			int length = (int)((System.currentTimeMillis() - startTime) / 1000l);
	//			System.out.println(length);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}
}
