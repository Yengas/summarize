package edu.yengas.ozet.readers;

import akka.NotUsed;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import edu.yengas.ozet.models.Corpus;

import java.nio.file.Path;

public class FileCorpusReader implements CorpusReader {
    private Path path;

    public FileCorpusReader(Path path){
        this.path = path;
    }

    public Source<Corpus, NotUsed> createReader(){
        return FileIO.fromPath(path)
                .map(bytes -> bytes.utf8String())
                .grouped(Integer.MAX_VALUE)
                .map(lines -> new Corpus(String.join("", lines)))
                .watchTermination((x, y) -> NotUsed.getInstance());
    }
}
