package edu.yengas.ozet.models;

import java.util.List;

public class Summary {
    public final List<SentenceWithRoots> sentences;

    public Summary(List<SentenceWithRoots> sentences){
        this.sentences = sentences;
    }
}
