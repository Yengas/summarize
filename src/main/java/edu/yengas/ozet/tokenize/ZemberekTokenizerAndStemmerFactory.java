package edu.yengas.ozet.tokenize;

import edu.yengas.ozet.models.CorpusLanguage;
import zemberek.morphology.analysis.tr.TurkishMorphology;

import java.io.IOException;

public class ZemberekTokenizerAndStemmerFactory implements TokenizerAndStemmerFactory {
    private static TurkishMorphology morphology = null;

    private static TurkishMorphology getTurkishMorphology() throws IOException {
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
