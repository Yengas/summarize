package edu.yengas.ozet;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import edu.yengas.ozet.identify.Identifier;
import edu.yengas.ozet.identify.ZemberekLanguageIdentifier;
import edu.yengas.ozet.models.CorpusWithLanguage;
import edu.yengas.ozet.readers.CorpusReader;
import edu.yengas.ozet.readers.FileCorpusReader;
import zemberek.langid.Language;
import zemberek.langid.LanguageIdentifier;

import java.nio.file.Paths;

public class Main {

    public static Language identify(LanguageIdentifier id, String content, int max){
        return Language.getByName(id.identify(content, max));
    }

    public static void main(String[] args) throws Exception{
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);

        Flow<CorpusWithLanguage, CorpusWithLanguage, NotUsed> printer = Flow.fromFunction((param) -> {
            System.out.println(param.corpus.content + "--" + param.language.toString());
            return param;
        });

        // Language identification classes, maybe removed in case no auto detection of languages.
        LanguageIdentifier identifierModel = LanguageIdentifier.fromInternalModels();
        Identifier identifier = new ZemberekLanguageIdentifier(identifierModel);

        // Read the configuration,
        // Parse the configuration into classes
        // Create the pipeline with the read configurations.

        CorpusReader reader = new FileCorpusReader(Paths.get("/home/yengas/Workspace/KYCUBYO/MakaleOzet/src/main/resources/corpus.txt"));

        reader.createReader()
                .via(Streams.createCorpusLanguageDetectionStream(identifier))
                .via(printer)
                .to(Sink.ignore())
                .run(materializer);


        // Pipeline:
        // Read the corpus
        // Summarize
        // Output
    }
}
