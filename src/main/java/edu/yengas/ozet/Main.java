package edu.yengas.ozet;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.*;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import edu.yengas.ozet.identify.Identifier;
import edu.yengas.ozet.identify.ZemberekLanguageIdentifier;
import edu.yengas.ozet.models.SentenceWithRoots;
import edu.yengas.ozet.readers.CorpusReader;
import edu.yengas.ozet.readers.FileCorpusReader;
import edu.yengas.ozet.readers.InputStreamCorpusReader;
import edu.yengas.ozet.summarizers.MeadSummarization;
import edu.yengas.ozet.summarizers.MeadSummarization2;
import edu.yengas.ozet.summarizers.Summarization;
import edu.yengas.ozet.tokenize.TokenizerAndStemmerFactory;
import edu.yengas.ozet.tokenize.ZemberekTokenizerAndStemmerFactory;
import zemberek.langid.Language;
import zemberek.langid.LanguageIdentifier;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static Language identify(LanguageIdentifier id, String content, int max){
        return Language.getByName(id.identify(content, max));
    }


    public static void main(String[] args) throws Exception{
        final ActorSystem system = ActorSystem.create("QuickStart");
        final ActorMaterializerSettings settings = ActorMaterializerSettings.create(system)
                .withSupervisionStrategy(new Function<Throwable, Supervision.Directive>() {
                    @Override
                    public Supervision.Directive apply(Throwable param) throws Exception {
                        param.printStackTrace();
                        return Supervision.stop();
                    }
                });
        final Materializer materializer = ActorMaterializer.create(settings, system);

        Flow<List<SentenceWithRoots>, List<SentenceWithRoots>, NotUsed> printer = Flow.fromFunction((param) -> {
            System.out.println("Total sentence count for corpus: " + param.size());

            for(SentenceWithRoots swr : param){
                System.out.println(swr.sentence);
                for(String root : swr.roots) System.out.print("\"" + root + "\"" + ", ");
                System.out.println("\n---------");
            }

            return param;
        });

        // Language identification classes, maybe removed in case no auto detection of languages.
        LanguageIdentifier identifierModel = LanguageIdentifier.fromInternalModels();
        Identifier identifier = new ZemberekLanguageIdentifier(identifierModel);
        // Create TokenizerAndStemmerFactory instance to be used
        TokenizerAndStemmerFactory tsFactory = new ZemberekTokenizerAndStemmerFactory();
        // Create summarization instance to be used
        Summarization summarization = new MeadSummarization();

        // Read the configuration,
        // Parse the configuration into classes
        // Create the pipeline with the read configurations.
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        CorpusReader reader = new InputStreamCorpusReader(() -> loader.getResourceAsStream("corpus.txt"));


        reader.createReader()
                .via(Streams.createCorpusLanguageDetectionStream(identifier))
                .via(Streams.corpusSentenceWithRootsParser(tsFactory))
                .via(Streams.summarizeStream(summarization, 10))
                .via(printer)
                .to(Sink.ignore())
                .run(materializer);


        // Pipeline:
        // Read the corpus
        // Summarize
        // Output
    }
}
