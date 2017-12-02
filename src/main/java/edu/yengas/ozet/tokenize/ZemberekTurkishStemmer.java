package edu.yengas.ozet.tokenize;

import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ZemberekTurkishStemmer implements Stemmer {
    private TurkishMorphology morphology;

    public ZemberekTurkishStemmer(TurkishMorphology morphology){
        this.morphology = morphology;
    }

    @Override
    public String rootWord(String word) {
        // Get the minumum of the roots, and return it.
        return Collections.min(
                morphology.analyze(word),
                Comparator.comparingInt(r -> r.getRoot().length())
        ).getRoot();
    }
}
