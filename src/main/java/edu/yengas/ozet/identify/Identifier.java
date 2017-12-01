package edu.yengas.ozet.identify;


import edu.yengas.ozet.models.CorpusLanguage;

public interface Identifier {
    CorpusLanguage identify(String content);
}
