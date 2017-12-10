package edu.yengas.ozet.writers;

import akka.NotUsed;
import akka.stream.javadsl.Sink;
import edu.yengas.ozet.models.Summary;
import scala.collection.JavaConverters;
import scala.collection.immutable.Map;
import scala.util.parsing.json.JSONArray;
import scala.util.parsing.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;

public class JsonSummaryWriter implements SummaryWriter {
    private BufferedWriter writer;

    public JsonSummaryWriter(OutputStream outputStream){
        writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    @Override
    public Sink<Summary, NotUsed> createWriter() {
        return Sink.<Summary>foreach((summary) -> {
            List<Object> sentences = summary.sentences.stream().map(swr -> swr.sentence).collect(Collectors.toList());

            writer.write(
                    new JSONObject(
                            new Map.Map1<String, Object>(
                                    "summary",
                                    new JSONArray(JavaConverters.asScalaBuffer(sentences).toList())
                            )
                    ).toString()
            );
            writer.newLine();
            writer.flush();
        }).mapMaterializedValue((a) -> NotUsed.getInstance());
    }
}
