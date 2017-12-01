package edu.yengas.ozet.tokenize;

import edu.yengas.ozet.models.CorpusLanguage;

public interface TokenizerAndStemmerFactory {
    public StemmerAndTokenizerForLanguage getTokenizerForLanguage(CorpusLanguage language);
}
