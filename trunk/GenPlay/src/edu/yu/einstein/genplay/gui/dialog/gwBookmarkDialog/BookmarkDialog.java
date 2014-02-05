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
package edu.yu.einstein.genplay.gui.dialog.gwBookmarkDialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;

import edu.yu.einstein.genplay.dataStructure.gwBookmark.GWBookmark;
import edu.yu.einstein.genplay.util.Images;

public class BookmarkDialog extends JDialog {

	private static final long serialVersionUID = -1547629052972041565L;

	public static void showDialog(Component parentComponent, List<GWBookmark> bookmarkList) {
		new BookmarkDialog(parentComponent, bookmarkList);
	}
	private final JTable jtBookmarks;
	private final JButton jbClose;

	private final JButton jbGoTo;
	//private final List<GWBookmark> bookmarkList;

	private BookmarkDialog(Component parentComponent, List<GWBookmark> bookmarkList) {
		//this.bookmarkList = bookmarkList;
		jtBookmarks = new JTable(new BookmarkTableModel(bookmarkList));
		jbClose = new JButton("Close");
		jbGoTo = new JButton("Go!");
		initComponents(parentComponent);
	}

	private void initComponents(Component parentComponent) {
		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		add(jtBookmarks, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		add(jbGoTo, c);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		add(jbClose, c);

		getRootPane().setDefaultButton(jbGoTo);
		setIconImage(Images.getApplicationImage());
		setTitle("Bookmarks");
		setLocationRelativeTo(parentComponent);
		setModal(true);
		pack();
		setResizable(false);
		setVisible(true);
	}
}
