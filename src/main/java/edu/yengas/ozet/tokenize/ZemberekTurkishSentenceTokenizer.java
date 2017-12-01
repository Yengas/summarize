package edu.yengas.ozet.tokenize;

import org.antlr.v4.runtime.Token;
import zemberek.tokenization.TurkishSentenceExtractor;
import zemberek.tokenization.TurkishTokenizer;

import java.util.List;
import java.util.stream.Collectors;

public class ZemberekTurkishSentenceTokenizer implements Tokenizer {
    private static final TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
    private static final TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;

    @Override
    public List<String> tokenizeSentencesFromDocument(String document) {
        return extractor.fromDocument(document);
    }

    @Override
    public List<String> tokenizeSentencesFromParagraph(String paragraph) {
        return extractor.fromParagraph(paragraph);
    }

    @Override
    public List<String> tokenizeWordsFromSentence(String sentence) {
        return tokenizer.tokenize(sentence)
                .stream()
                .filter(token -> token.getType() == 15)
                .map(Token::getText).collect(Collectors.toList());
    }

}
