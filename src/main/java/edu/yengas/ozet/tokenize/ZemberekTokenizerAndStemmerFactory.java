package edu.yengas.ozet.tokenize;

import edu.yengas.ozet.models.CorpusLanguage;
import zemberek.morphology.analysis.tr.TurkishMorphology;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZemberekTokenizerAndStemmerFactory implements TokenizerAndStemmerFactory {
    private static TurkishMorphology morphology = null;

    private static TurkishMorphology getTurkishMorphology() throws IOException {
        // TODO: this seems hackish, convert to using properties file or something else.
        Logger logger = Logger.getLogger("zemberek-logger");
        if(logger != null) logger.setLevel(Level.OFF);

        if(morphology != null) return morphology;
        return morphology = TurkishMorphology.createWithDefaults();
    }

    @Override
    public StemmerAndTokenizerForLanguage getTokenizerForLanguage(CorpusLanguage language) {
        switch(language){
            case Turkish: {
                TurkishMorphology morphology = null;

                try {
                    morphology = getTurkishMorphology();
                }catch(Exception e){ e.printStackTrace(); return null; }

                return new StemmerAndTokenizerForLanguage(
                        new ZemberekTurkishSentenceTokenizer(),
                        new ZemberekTurkishStemmer(morphology),
                        CorpusLanguage.Turkish
                );
            }
            default:
                return null;
        }
    }
}
