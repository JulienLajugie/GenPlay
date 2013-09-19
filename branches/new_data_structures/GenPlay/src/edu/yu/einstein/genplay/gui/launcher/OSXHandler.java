package edu.yu.einstein.genplay.gui.launcher;

import java.io.File;

import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.OpenFilesHandler;

class OSXHandler implements OpenFilesHandler {

	private File fileToOpen = null;

	public File getFileToOpen() {
		return fileToOpen;
	}

	@Override
	public void openFiles(OpenFilesEvent openFilesEvent) {
		fileToOpen = openFilesEvent.getFiles().get(0);
	}
}
