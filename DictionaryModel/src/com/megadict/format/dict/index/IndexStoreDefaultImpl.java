package com.megadict.format.dict.index;

import java.util.List;

public interface IndexStoreDefaultImpl {

    public Index getIndexOf(String headword);

    public boolean containsWord(String word);

    public List<String> getSimilarWord(String headword, int preferredNumber);

}