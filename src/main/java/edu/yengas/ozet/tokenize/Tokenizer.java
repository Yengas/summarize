package edu.yengas.ozet.tokenize;

import java.util.List;

public interface Tokenizer {
    public List<String> tokenizeSentencesFromDocument(String document);
    public List<String> tokenizeSentencesFromParagraph(String paragraph);
    public List<String> tokenizeWordsFromSentence(String sentence);
}
