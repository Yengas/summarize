package edu.yengas.ozet.models;

import java.util.List;

public class SentenceWithRoots {
    public final String sentence;
    public final List<String> roots;

    public SentenceWithRoots(String sentence, List<String> roots){
        this.sentence = sentence;
        this.roots = roots;

    }
}
