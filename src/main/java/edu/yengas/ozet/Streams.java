package edu.yengas.ozet;

import akka.NotUsed;
import akka.stream.javadsl.Flow;
import edu.yengas.ozet.identify.Identifier;
import edu.yengas.ozet.models.CorpusWithLanguage;
import edu.yengas.ozet.models.Corpus;

public class Streams {
    public static Flow<Corpus, CorpusWithLanguage, NotUsed> createCorpusLanguageDetectionStream(Identifier identifier){
        return Flow.fromFunction((corpus) -> new CorpusWithLanguage(corpus, identifier.identify(corpus.content)));
    }
}
