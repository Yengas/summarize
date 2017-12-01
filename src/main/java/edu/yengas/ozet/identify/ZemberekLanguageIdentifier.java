package edu.yengas.ozet.identify;

import edu.yengas.ozet.models.CorpusLanguage;
import zemberek.langid.Language;
import zemberek.langid.LanguageIdentifier;

public class ZemberekLanguageIdentifier implements Identifier {
    private static final int SAMPLE_COUNT = 50;

    private LanguageIdentifier identifier;

    public ZemberekLanguageIdentifier(LanguageIdentifier identifier){
        this.identifier = identifier;
    }

    @Override
    public CorpusLanguage identify(String content) {
        Language identifiedLanguage = Language.getByName(identifier.identify(content, SAMPLE_COUNT));

        if(identifiedLanguage.equals(Language.TR))
            return CorpusLanguage.Turkish;
        else if(identifiedLanguage.equals(Language.EN))
            return CorpusLanguage.English;

        return CorpusLanguage.Unknown;
    }
}
