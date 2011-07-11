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
package edu.yu.einstein.genplay.core.genome;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains genome information
 * @author Nicolas Fourel
 */
public class Genome implements Serializable {

	private static final long 		serialVersionUID = 6313753019744936641L;	// generated ID
	private Map<String, Assembly>	assemblyList;	// List of assembly
	private String					name; 			// Name of the Genome
	
	
	/**
	 * Constructor of {@link Genome}
	 * @param name	name of the genome
	 */
	protected Genome (String name) {
		assemblyList = new HashMap<String, Assembly>();
		this.name = name;
	}
	
	
	/**
	 * Add an assembly to the genome if the assembly is not already existing.
	 * @param assembly	genome to add
	 */
	protected void addAssembly (Assembly assembly) {
		if (!assemblyList.containsKey(assembly.getIndexName())){
			assemblyList.put(assembly.getIndexName(), assembly);
		}
	}
	
	
	/**
	 * @return the genome name
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * @return the assembly list
	 */
	public Map<String, Assembly> getAssemblyList() {
		return assemblyList;
	}


	/**
	 * @param assemblyList the new assembly list
	 */
	protected void setAssemblyList(Map<String, Assembly> assemblyList) {
		this.assemblyList = assemblyList;
	}
	
	
}