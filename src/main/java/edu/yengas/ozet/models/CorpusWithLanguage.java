package edu.yengas.ozet.models;

public class CorpusWithLanguage {
    public final Corpus corpus;
    public final CorpusLanguage language;

    public CorpusWithLanguage(Corpus corpus, CorpusLanguage language){
        this.corpus = corpus;
        this.language = language;
    }
}
