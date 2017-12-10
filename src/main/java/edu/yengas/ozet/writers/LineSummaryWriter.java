package edu.yengas.ozet.writers;

import akka.NotUsed;
import akka.stream.javadsl.Sink;
import edu.yengas.ozet.models.SentenceWithRoots;
import edu.yengas.ozet.models.Summary;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class LineSummaryWriter implements SummaryWriter{
    private BufferedWriter bufferedWriter;

    public LineSummaryWriter(OutputStream outputStream){
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    @Override
    public Sink<Summary, NotUsed> createWriter() {
        return Sink.<Summary>foreach(summary -> {
            for(SentenceWithRoots sentence : summary.sentences){
                bufferedWriter.write(sentence.sentence);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }).mapMaterializedValue((a) -> NotUsed.getInstance());
    }
}
