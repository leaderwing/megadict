package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.model.DictionaryInformation;

public class ExternalReader {
	// Init logger for debugging.
	final static Logger LOGGER = Logger.getLogger("ExternalReader");
	static { LOGGER.addHandler(new ConsoleHandler()); }

	private final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
	public static final String NO_DICTIONARY = "There is no dictionary.";
	public static final String INDEX_FILE_NOT_FOUND = "Index file not found.";
	public static final String DATA_FILE_NOT_FOUND = "Data file not found.";

	public List<DictionaryInformation> getInfos() {
		return infos;
	}

	public ExternalReader(final File externalFile) {
		init(externalFile);
	}

	private void init(final File externalFile) {

		final File files[] = externalFile.listFiles();
		if (files == null) throw new IllegalArgumentException("External storage is not a valid directory.");
		for (final File file : files) {
			if (file.isDirectory()) {
				try {
					final DictionaryInformation info = createDictionaryInformation(file);
					infos.add(info);
				} catch (final IndexFileNotFoundException e) {
					LOGGER.warning(e.getMessage());
				} catch (final DataFileNotFoundException e) {
					LOGGER.warning(e.getMessage());
				}
			}
		}
	}

	private DictionaryInformation createDictionaryInformation(final File parentFilePath) throws IndexFileNotFoundException, DataFileNotFoundException {
		return new DictionaryInformation(parentFilePath);
	}
}
