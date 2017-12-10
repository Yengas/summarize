package edu.yengas.ozet;

import akka.NotUsed;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import edu.yengas.ozet.identify.Identifier;
import edu.yengas.ozet.models.Corpus;
import edu.yengas.ozet.models.CorpusWithLanguage;
import edu.yengas.ozet.models.SentenceWithRoots;
import edu.yengas.ozet.models.Summary;
import edu.yengas.ozet.summarizers.Summarization;
import edu.yengas.ozet.tokenize.StemmerAndTokenizerForLanguage;
import edu.yengas.ozet.tokenize.TokenizerAndStemmerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Streams {
    public static Flow<Corpus, CorpusWithLanguage, NotUsed> createCorpusLanguageDetectionStream(Identifier identifier){
        return Flow.fromFunction((corpus) -> new CorpusWithLanguage(corpus, identifier.identify(corpus.content)));
    }

    private static class CorpusWithLanguageSTL{
        public final CorpusWithLanguage cwl;
        public final StemmerAndTokenizerForLanguage stl;

        public CorpusWithLanguageSTL(CorpusWithLanguage cwl, StemmerAndTokenizerForLanguage stl){
            this.cwl = cwl;
            this.stl = stl;
        }
    }

    /**
     * Given a corpus with the stemmer and tokenizer attached to it, creates a source of sentence with root words stream.
     * @param cwlStl
     * @return
     */
    public static Source<List<SentenceWithRoots>, NotUsed> createSentenceWithRootsParser(CorpusWithLanguageSTL cwlStl){
        return Source.from(cwlStl.stl.tokenizer.tokenizeSentencesFromDocument(cwlStl.cwl.corpus.content))
                .via(Flow.fromFunction((sentence) ->
                                new SentenceWithRoots(
                                        sentence,
                                        cwlStl.stl.tokenizer.tokenizeWordsFromSentence(sentence)
                                                .stream()
                                                .map(cwlStl.stl.stemmer::rootWord)
                                                .collect(Collectors.toList())
                                )
                )).grouped(Integer.MAX_VALUE);
    }

    /**
     * Given a stemmer and tokenizer factory, returns a stream that attaches stemmer and tokenizer to given corpuses.
     * @param tsFactory
     * @return
     */
    public static Flow<CorpusWithLanguage, CorpusWithLanguageSTL, NotUsed> corpusStlAdderStream(TokenizerAndStemmerFactory tsFactory){
        return Flow.fromFunction((cwl) -> new CorpusWithLanguageSTL(cwl, tsFactory.getTokenizerForLanguage(cwl.language)));
    }

    /**
     * Given a stemmer and tokenizer factory, returns a stream that returns list of sentences and the root words in them.
     * @param tsFactory
     * @return
     */
    public static Flow<CorpusWithLanguage, List<SentenceWithRoots>, NotUsed> corpusSentenceWithRootsParser(TokenizerAndStemmerFactory tsFactory){
        return corpusStlAdderStream(tsFactory).flatMapConcat(Streams::createSentenceWithRootsParser);
    }

    /**
     * Creates a stream that summarizes the given sentences with root words.
     * @param summarization the summarization algorithm instance to be used.
     * @return
     */
    public static Flow<List<SentenceWithRoots>, Summary, NotUsed> summarizeStream(Summarization summarization, int count){
        return Flow.fromFunction(sentencesWithRoots -> {
            List<List<String>> asSentenceList = sentencesWithRoots.stream().map(swr -> swr.roots).collect(Collectors.toList());

            return new Summary(summarization.summarizeSentences(
                    asSentenceList,
                    count
            ).stream().map(sentencesWithRoots::get).collect(Collectors.toList()));
        });
    }
}
