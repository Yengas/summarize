package edu.yengas.ozet.readers;


import akka.NotUsed;
import akka.dispatch.Futures;
import akka.stream.javadsl.Source;
import edu.yengas.ozet.models.Corpus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import scala.concurrent.ExecutionContext;

public class HTTPCorpusReader implements CorpusReader {
    private static final String USER_AGENT = "Java/Summarize";

    public static class Options{
        private final String url;
        private String query;
        private String agent;

        public Options(String url){
            this(url, "body");
        }

        public Options(String url, String body){
            this(url, body, USER_AGENT);
        }

        public Options(String url, String query, String agent){
            this.url = url;
            this.query = query;
            this.agent = agent;
        }

        public String getURL(){
            return this.url;
        }

        public String getQuery(){
            return this.query;
        }

        public String getUserAgent(){
            return this.agent;
        }
    }

    private HTTPCorpusReader.Options options;
    private ExecutionContext executor;

    public HTTPCorpusReader(HTTPCorpusReader.Options options, ExecutionContext executor){
        this.options = options;
        this.executor = executor;
    }

    @Override
    public Source<Corpus, NotUsed> createReader() {
        return Source.fromFuture(Futures.future(() -> {
            Document document = Jsoup.connect(options.getURL()).header("User-Agent", options.getUserAgent()).get();
            return new Corpus(document.select(options.getQuery()).text());
        }, executor));
    }

}
