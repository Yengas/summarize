package edu.yengas.ozet.readers;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import edu.yengas.ozet.models.Corpus;

public interface CorpusReader {
    public Source<Corpus, NotUsed> createReader();
}
