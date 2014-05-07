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
package edu.yu.einstein.genplay.core.IO.genomeListLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.dataStructure.genome.Clade;
import edu.yu.einstein.genplay.dataStructure.genome.Genome;

/**
 * This class makes the clade list from xml files
 * @author Nicolas Fourel
 */
public class AssemblyListLoader {

	private final static String XML_ASSEMBLIES_ROOT_PATH = "edu/yu/einstein/genplay/resource/assemblies/";
	private final List<String> 			xmlAssembliesPath;
	private final Map<String, Clade> 	cladeList;
	private GenomeListLoader 			genomeParser;


	/**
	 * Constructor of {@link AssemblyListLoader}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public AssemblyListLoader () throws ParserConfigurationException, SAXException, IOException {
		xmlAssembliesPath = new ArrayList<String>();
		cladeList = new HashMap<String, Clade>();
		initGenomePath ();
		computeList ();
	}


	/**
	 * Add a clade to the clade list
	 * If the clade is already existing, this method will try to add the genome.
	 * @param clade
	 */
	protected void addClade (Clade clade) {
		if (!cladeList.containsKey(clade.getName())){
			cladeList.put(clade.getName(), clade);
		} else {
			for (Genome genome: clade.getGenomeList().values()){
				cladeList.get(clade.getName()).addGenome(genome);
			}
		}
	}


	/**
	 * This method read all XML files defined to build a list of clade.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void computeList () throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();

		for (String currentGenomePath: xmlAssembliesPath) {
			InputStream xml = getClass().getClassLoader().getResourceAsStream(currentGenomePath);
			genomeParser = new GenomeListLoader();
			parser.parse(xml, genomeParser);
			genomeParser.computeClade();
			addClade(genomeParser.getClade());
			xml.close();
		}
	}


	/**
	 * @return the clade list
	 */
	public Map<String, Clade> getCladeList() {
		return cladeList;
	}


	/**
	 * XML path files are set here.
	 */
	private void initGenomePath () {
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("deuterostome_c. intestinalis_2002_12_JGI_1.0_ci1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("deuterostome_c. intestinalis_2005_03_JGI_2.1_ci2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("deuterostome_lancelet_2006_03_JGI_1.0_braFlo1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_a. gambiae_2003_02_IAGEC_MOZ2_anoGam1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_a. mellifera_2004_07_Baylor_1.2_apiMel1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_a. mellifera_2005_01_Baylor_2.0_apiMel2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. erecta_2005_08_Agencourt_prelim_droEre1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. melanogaster_2003_01_BDGP_R3_dm1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. melanogaster_2004_04_BDGP_R4_dm2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. melanogaster_2006_04_BDGP_R5_dm3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. mojavensis_2005_08_Agencourt_prelim_droMoj2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. pseudoobscura_2003_08_Baylor_freeze1_dp2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. pseudoobscura_2004_11_FlyBase_1.03_dp3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. simulans_2005_04_WUGSC_mosaic_1.0_droSim1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. yakuba_2004_04_WUGSC_1.0_droYak1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("insect_d. yakuba_2005_11_WUGSC_7.1_droYak2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_chimp_2003_11_CGSC_1.1_panTro1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_chimp_2006_03_CGSC_2.1_panTro2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_dog_2004_07_Broad_canFarm1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_dog_2005_05_Broad_canFarm2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_elephant_2009_07_Broad_loxAfr3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_guinea pig_2008_02_Broad_cavPor3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_horse_2007_01_Broad_equCab1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_horse_2007_09_Broad_equCab2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2003_07_hg16.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2003_07_NCBI34.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2004_05_hg17.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2004_05_NCBI35.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2006_03_hg18.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2006_03_NCBI36.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2009_02_GRCh37.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2009_02_hg19.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2013_12_GRCh38.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_human_2013_12_hg38.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_mouse_2005_08_NCBI35_mm7.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_mouse_2006_02_NCBI36_mm8.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_mouse_2007_07_NCBI37_mm9.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_mouse_2011_12_GRCm38_mm10.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_opossum_2006_01_Broad_monDom4.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_opossum_2006_10_Broad_monDom5.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_orangutan_2007_07_WUGSC_2.0.2_ponAbe2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_pig_2009_11_SGSC_Sscrofa9.2_susScr2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_rabbit_2009_04_Broad_oryCun2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_rat_2003_06_Baylor_3.1_rn3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_rat_2004_11_Baylor_3.4_rn4.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_rat_2012_03_RGSC_5.0_rn5.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("mammal_rhesus_2006_01_MGSC_Merged_1.0_rheMac2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. brenneri_2007_01_WUGSC_4.0_caePb1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. brenneri_2008_02_WUGSC_6.0.1_caePb2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. briggsae_2002_07_WormBase_cb25.agp8_cb1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. briggsae_2007_01_WUGSC_1.0_cb3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. elegans_2004_03_WS120_ce2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. elegans_2007_01_WS170_ce4.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. elegans_2008_05_WS190_ce6.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. japonica_2008_03_WUGSC_3.0.2_caeJap1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. remanei_2006_03_WUGSC_1.0_caeRem2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_c. remanei_2007_05_WUGSC_15.0.1_caeRem3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("nematode_p. pacificus_2007_02_WUGSC_5.0_priPac1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("other_s. cerevisiae_2003_10_SGD_sacCer1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("other_s. cerevisiae_2008_07_SGD_sacCer2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("other_sea hare_2008_09_Broad_2.0_aplCal1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("plant_a. thaliana_2010_11_TAIR10.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("S. pombe_2002_02(Nature_paper).xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("S. pombe_2011_05(GFF).xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_chicken_2004_02_WUGSC_1.0_galGal2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_chicken_2006_05_WUGSC_2.1_galGal3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_fugu_2002_08_JGI_3.0_fr1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_fugu_2004_10_JGI_4.0_fr2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_lizard_2007_02_Broad_anoCar1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_medaka_2005_10_NIG_UT_MEDAKA1_oryLat2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_stickleback_2006_02_Broad_gasAcu1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_tetraodon_2004_02_Genoscope_7_tetNig1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_tetraodon_2007_03_Genoscope_8.0_tetNig2.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_zebra finch_2008_07_WUGSC_3.2.4_taeGut1.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_zebrafish_2005_04_Zv5_danRer3.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_zebrafish_2006_03_Zv6_danRer4.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_zebrafish_2007_07_Zv7_danRer5.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("vertebrate_zebrafish_2010_07_Zv9_danRer7.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus1(9629378)_2010_08.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus2(9629267)_2010_04.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus3(9625875)_2011_03.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus4(139424470)_2010_03.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus4type1(82503188)_2010_03.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus5(155573622)_2012_08.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus6A(224020395)_2010_04.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus6B(9633069)_2010_08.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus7(51874225)_2010_04.xml"));
		xmlAssembliesPath.add(XML_ASSEMBLIES_ROOT_PATH.concat("virus_herpesvirus_herpesvirus8(139472801)_2010_04.xml"));
	}
}
