package com.rewardify.util;

import jakarta.annotation.PostConstruct;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class NLPUtils {

    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;

    private POSTaggerME posTagger;


    @PostConstruct
    public void init() throws Exception {
        try (
                InputStream sentenceModelStream = getClass().getResourceAsStream("/models/en-sent.bin");
                InputStream tokenModelStream = getClass().getResourceAsStream("/models/en-token.bin");
                InputStream posModelStream = getClass().getResourceAsStream("/models/en-pos.bin")
        ) {
            sentenceDetector = new SentenceDetectorME(new SentenceModel(sentenceModelStream));
            tokenizer = new TokenizerME(new TokenizerModel(tokenModelStream));
            posTagger = new POSTaggerME(new POSModel(posModelStream));
        }
    }

    public String[] getSentences(String text) {
        return sentenceDetector.sentDetect(text);
    }

    public String[] getTokens(String sentence) {
        return tokenizer.tokenize(sentence);
    }

    public String[] getPOS(String[] tokens) {
        return posTagger.tag(tokens);
    }
}
