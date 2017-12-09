package edu.yengas.ozet.writers;

import akka.NotUsed;
import akka.stream.javadsl.Sink;
import edu.yengas.ozet.models.SentenceWithRoots;
import edu.yengas.ozet.models.Summary;

import java.util.List;

public interface SummaryWriter {
    public Sink<Summary, NotUsed> createWriter();
}
