package edu.yengas.ozet.summarizers;

import java.util.List;

/**
 * Mead algorithm with centroid based summarization.
 * Implements the tf * idf algorithm by considering each sentence as a document.
 * Reference implementation: http://clair.si.umich.edu/~radev/papers/centroid.pdf
 */
public class MeadSummarization implements Summarization {
    @Override
    public List<Integer> summarizeSentences(List<List<String>> sentences, int count) {
        return new MeadSummarizationAlgorithm().updateModel(sentences).summarizeSentences(sentences, count);
    }
}
