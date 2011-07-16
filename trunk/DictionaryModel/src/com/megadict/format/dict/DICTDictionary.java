package com.megadict.format.dict;

import java.io.File;
import java.util.*;

import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexStore;
import com.megadict.format.dict.reader.BufferedDictReader;
import com.megadict.format.dict.reader.DictFileReader;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class DICTDictionary implements Dictionary {

    public DICTDictionary(String indexFilePath, String dictFilePath) {
        this(new File(indexFilePath), new File(dictFilePath));        
    }
    
    public DICTDictionary(File indexFile, File dictFile) {
        this.indexFile = indexFile;
        this.dictFile = dictFile;
        initialize();
    }
    
    private void initialize() {
        checkFileExistence();
        buildIndex();
        prepareDefinitions();
        loadDictionaryMetadata();
    }
    
    private void checkFileExistence() {
        if (!(indexFile.exists())) {
            throw new ResourceMissingException("Index file does not exist.");
        }
        
        if( !(dictFile.exists())) {
            throw new ResourceMissingException("Dict file does not exist.");
        }
    }

    private void buildIndex() {
        supportedWords = new IndexStore(indexFile);
    }
    
    private void prepareDefinitions() {
        DictFileReader defaultDictFileReader = new BufferedDictReader(dictFile);
        definitionFinder = new DefinitionFinder(defaultDictFileReader);
    }

    private void loadDictionaryMetadata() {
        loadDictionaryName();
    }

    private void loadDictionaryName() {
        Index nameEntry = 
            supportedWords.getIndexOf(MetaDataEntry.SHORT_NAME.tagName());
        String name = definitionFinder.getDefinitionAt(nameEntry);        
        this.name = cleanedUpName(name);
    }
    
    private static String cleanedUpName(String rawName) {
        String noNewLineCharactersName = rawName.replace("\n", "");
        StringBuilder builder = new StringBuilder(noNewLineCharactersName);
        builder.delete(0, NAME_REDUNDANT_STRING.length());
        return builder.toString();
    }

    @Override
    public List<String> recommendWord(String word) {
        // TODO: Implement to return the adjacency words. Maybe
        // interpolation search algorithm.
        return Collections.emptyList();
    }

    @Override
    public Definition lookUp(String word) {
        boolean validated = validateWord(word);

        if (validated && supportedWords.containsWord(word)) {
            return loadDefinition(supportedWords.getIndexOf(word));
        } else {
            return Definition.NOT_FOUND;
        }
    }

    private static boolean validateWord(String word) {
        return StringChecker.check(word);
    }

    private Definition loadDefinition(Index index) {
        if (definitionCache.containsKey(index)) {
            return definitionCache.get(index);
        } else {
            String definitionContent = definitionFinder
                    .getDefinitionAt(index);
            Definition def = new Definition(index.getWord(), definitionContent,
                    this.name);
            cacheDefinition(def);
            return def;
        }
    }

    private void cacheDefinition(Definition def) {
        definitionCache.put(def.getWord(), def);
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = String.format(TO_STRING_PATTERN, name, indexFile, dictFile);
        }
        return toStringCache;
    }

    private final File indexFile;
    private final File dictFile;
    private String name;
    private DefinitionFinder definitionFinder;
    private Map<String, Definition> definitionCache = new HashMap<String, Definition>();
    private IndexStore supportedWords;
    
    private static final String NAME_REDUNDANT_STRING = "@00-database-short- FVDP "; 
    private static final String TO_STRING_PATTERN = "DICTDictionary[name: %s; indexFile: %s; dictFile: %s]";
    private String toStringCache;
}