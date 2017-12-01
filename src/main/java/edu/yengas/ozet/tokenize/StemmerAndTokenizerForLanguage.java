package edu.yengas.ozet.tokenize;

import edu.yengas.ozet.models.CorpusLanguage;

public class StemmerAndTokenizerForLanguage {
    public final Tokenizer tokenizer;
    public final Stemmer stemmer;
    public final CorpusLanguage language;

    public StemmerAndTokenizerForLanguage(Tokenizer tokenizer, Stemmer stemmer, CorpusLanguage language){
        this.tokenizer = tokenizer;
        this.stemmer = stemmer;
        this.language = language;
    }

}
