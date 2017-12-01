package edu.yengas.ozet.tokenize;

import zemberek.morphology.analysis.tr.TurkishMorphology;

public class ZemberekTurkishStemmer implements Stemmer {
    private TurkishMorphology morphology;

    public ZemberekTurkishStemmer(TurkishMorphology morphology){
        this.morphology = morphology;
    }

    @Override
    public String rootWord(String word) {
        return morphology.analyze(word).get(0).getRoot();
    }
}
