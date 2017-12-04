package edu.yengas.ozet.summarizers;

import java.util.List;

public interface Summarization {
    /**
     * Makes a selection of sentences given a list of sentences, represented as a list of words.
     * @param sentences the list of sentences to pick a summarization for.
     * @param percentage the percentage of sentences to pick.
     * * @return the sel@paramection of sentences as they appear in the given list.
     */
    public List<Integer> summarizeSentences(List<List<String>> sentences, double percentage);
}
