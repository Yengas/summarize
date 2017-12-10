package edu.yengas.ozet.summarizers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Mead algorithm with centroid based summarization.
 * Implements the tf * idf algorithm by considering each sentence as a document.
 * Reference implementation: http://clair.si.umich.edu/~radev/papers/centroid.pdf
 */
public class MeadSummarizationAlgorithm {

    class Frequency implements Cloneable{
        int termFrequency = 0;
        // This refers to how many sentences we have seen the word in so far.
        int documentFrequency = 0;

        public Frequency(){ }

        public Frequency(int termFrequency, int documentFrequency){
            this.termFrequency = termFrequency;
            this.documentFrequency = documentFrequency;
        }

        /**
         * Returns on average how many times this word is used in a single document.
         * @param documentCount
         * @return
         */
        public double averageFrequency(int documentCount){
            return termFrequency / (double) documentCount;
        }

        /**
         * Returns the centroid value for the given frequencies in the given corpus.
         * @param documentCount the total number of sentences in the corpus.
         * @return
         */
        public double centroidValue(int documentCount){
            return averageFrequency(documentCount) * Math.log10(documentCount / ((double) documentFrequency));
        }

        @Override
        public Frequency clone(){
            return new Frequency(termFrequency, documentFrequency);
        }

    }

    class Model{
        int documentCount;
        SortedSet<String> words;
        Map<String, Frequency> frequencies;

        public double getCentroidValueForWord(String word){
            if(!frequencies.containsKey(word)) return 0;
            return frequencies.get(word).centroidValue(documentCount);
        }
    }

    private Model model = null;

    public MeadSummarizationAlgorithm(){
        this.model = emptyModel();
    }

    public MeadSummarizationAlgorithm(MeadSummarizationAlgorithm.Model model){
        this.model = model;
    }

    private Model emptyModel(){
        Model m = new Model();
        m.documentCount = 0;
        m.words = new TreeSet<String>();
        m.frequencies = Collections.emptyMap();
        return m;
    }

    /**
     * Updates the model with a new document. This method needs to be called before asking for summarizations.
     * @param sentences
     * @return
     */
    public MeadSummarizationAlgorithm updateModel(List<List<String>> sentences){
        Model m = emptyModel();
        Set<String> documentWords = sentences.stream().flatMap(List::stream).collect(Collectors.toSet());

        // We think of each individual sentence as a document.
        m.documentCount = this.model.documentCount + sentences.size();

        m.words.addAll(this.model.words);
        m.words.addAll(documentWords);

        m.frequencies = new HashMap<String, Frequency>();

        // Add word that are defined in the old model, but not in the new.
        for(String word : this.model.frequencies.keySet()){
            if(!m.frequencies.containsKey(word)) {
                Frequency freq = this.model.frequencies.get(word).clone();

                m.frequencies.put(word, freq);
            }
        }

        for(List<String> sentence : sentences){
            HashSet<String> set = new HashSet<String>();

            for(String word : sentence){
                Frequency freq = m.frequencies.getOrDefault(word, new Frequency(0, 0));

                freq.termFrequency += 1;
                if(set.add(word))
                    freq.documentFrequency += 1;
                m.frequencies.put(word, freq);
            }
        }

        return new MeadSummarizationAlgorithm(m);
    }

    /**
     * Get the top words in the document according to the current model state.
     * @param percentage
     * @return
     */
    public Set<String> topWords(double percentage){
        int size = (int) Math.min(Math.max(1, model.words.size() * percentage / 100), model.words.size());

        return model.frequencies.keySet()
                .stream()
                .sorted(
                        Comparator.comparing(model::getCentroidValueForWord).reversed()
                                .thenComparing(Comparator.reverseOrder())
                )
                .limit(size)
                .collect(Collectors.toSet());
    }

    /**
     * Calculate the centroid value for the given sentence with the topwords and the current model state.
     * @param sentence
     * @param topWords
     * @return
     */
    public double calculateSentenceCentroid(List<String> sentence, Set<String> topWords){
        double value = 0;

        // For each word, make sure we only include it once.
        for(String word : new HashSet<String>(sentence)){
            if(topWords.contains(word))
                value += model.getCentroidValueForWord(word);
        }

        return value;
    }

    /**
     * Returns the positional value of the sentence, taking into account the size of the document.
     * Makes sure the sentences which are closer to the start are ranked higher
     * @return
     */
    public double positionalValue(int position, int numSentences, double maxCentroidValue){
        return ((numSentences - position) / (double)numSentences) * maxCentroidValue;
    }

    private class IndexWithValue implements Comparable<IndexWithValue>{
        public final int id;
        public final double value;

        public IndexWithValue(int id, double value){
            this.id = id;
            this.value = value;
        }

        /**
         * Reverse sort by the value
         * @return
         */
        @Override
        public int compareTo(IndexWithValue other) {
            return Double.compare(other.value, this.value);
        }
    }

    /**
     * Returns the overlap value for the given sentence with the first sentence.
     * @param firstSentenceVector the term occurrences of the first sentence.
     * @param sentence the sentence to check against first sentence.
     * @return
     */
    private static int getOverlapsOfSentence(Map<String, Integer> firstSentenceVector, List<String> sentence){
        int overlaps = 0;

        for(String word : sentence)
            overlaps += firstSentenceVector.getOrDefault(word, 0);

        return overlaps;
    }


    /**
     * Creates a term occurrence map for the given sentence.
     * @param sentence
     * @return
     */
    private static Map<String, Integer> createWordOccurrenceMap(List<String> sentence){
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        for(String word : sentence){
            result.put(word, result.getOrDefault(word, 0) + 1);
        }

        return result;
    }

    public List<Integer> summarizeSentences(List<List<String>> sentences, int countToSelect) {
        // Top 10 percent of the words in the document
        Set<String> topTenPercent = topWords(10);
        List<Double> centroidValues = sentences.stream().map(s -> calculateSentenceCentroid(s, topTenPercent)).collect(Collectors.toList());
        double centroidMaxValue = Collections.max(centroidValues);
        Map<String, Integer> firstSentenceVector = createWordOccurrenceMap(sentences.get(0));

        return IntStream.range(0, sentences.size()).boxed()
                .map(idx -> {
                    // Get centroid value for the sentence, according to the words in the sentence and topWords
                    double cv = centroidValues.get(idx);
                    // Get positional value, relative to the sentence position and cmax value
                    double pv = positionalValue(idx, sentences.size(), centroidMaxValue);
                    // Get overlaps value with the first sentence of the document
                    double so = getOverlapsOfSentence(firstSentenceVector, sentences.get(idx));

                    return new IndexWithValue(idx, cv + pv + so);
                })
                .sorted()
                .limit(countToSelect)
                .map(obj -> obj.id)
                .sorted()
                .collect(Collectors.toList());
    }
}
