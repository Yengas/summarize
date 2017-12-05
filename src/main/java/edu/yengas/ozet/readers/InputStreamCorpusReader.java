package edu.yengas.ozet.readers;

import akka.NotUsed;
import akka.japi.function.Creator;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.StreamConverters;
import edu.yengas.ozet.models.Corpus;

import java.io.InputStream;

public class InputStreamCorpusReader implements CorpusReader {

    private Creator<InputStream> inputStreamCreator;

    public InputStreamCorpusReader(Creator<InputStream> inputStreamCreator){
        this.inputStreamCreator = inputStreamCreator;
    }

    @Override
    public Source<Corpus, NotUsed> createReader() {
        return StreamConverters.fromInputStream(this.inputStreamCreator)
                .map(bytes -> bytes.utf8String())
                .grouped(Integer.MAX_VALUE)
                .map(lines -> new Corpus(String.join("", lines)))
                .watchTermination((x, y) -> NotUsed.getInstance());
    }
}
