package com.rewardify.service;

import com.rewardify.exceeption.ReviewValidationException;
import com.rewardify.util.NLPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ReviewValidatorService {

    @Autowired
    private NLPUtils nlpUtils;

    private final List<String> suspiciousPhrases = List.of(
            "nice product", "very good", "best product", "awesome", "worth it", "amazing", "must buy"
    );


    private final Map<String, String> RULES = Map.of(
            "REVIEW_MAX_LENGTH", "500",
            "REVIEW_MIN_SENTENCES", "2",
            "REVIEW_MIN_TOKENS_PER_SENTENCE", "5",
            "REVIEW_MAX_TOKENS_PER_SENTENCE", "50",
            "MIN_INFORMATIVE_WORD_RATIO", "0.3",
            "MIN_UNIQUENESS_RATIO", "0.6"
    );

    public void validateReview(String review) {

        if (StringUtils.isEmpty(review)) {
           throw new ReviewValidationException("Review cannot be empty");
        }
        if (review.length() > Integer.parseInt(RULES.get("REVIEW_MAX_LENGTH"))) {
            throw new ReviewValidationException("Review exceeds maximum length of 500 characters.");
        }

        String[] sentences = nlpUtils.getSentences(review);
        List<String> allTokens = new ArrayList<>();
        if (sentences.length < Integer.parseInt(RULES.get("REVIEW_MIN_SENTENCES"))) {
            throw new ReviewValidationException("Review must contain at least two sentences.");
        }
        for (String sentence : sentences) {
            String[] tokens = nlpUtils.getTokens(sentence);
            if (tokens.length < Integer.parseInt(RULES.get("REVIEW_MIN_TOKENS_PER_SENTENCE")) ||
                    tokens.length > Integer.parseInt(RULES.get("REVIEW_MAX_TOKENS_PER_SENTENCE")) ) {
                throw new ReviewValidationException("Each sentence must have between 5 and 50 tokens.");
            }
            allTokens.addAll(Arrays.asList(tokens));
        }

        String[] posTags = nlpUtils.getPOS(allTokens.toArray(new String[0]));

        boolean isBotGenerated = isBotGenerated(review, allTokens.toArray(new String[0]),  posTags);
        if (isBotGenerated) {
            throw new ReviewValidationException("Review is likely bot-generated.");
        }
    }

    public boolean isBotGenerated(String review, String[] tokens, String[] tags) {
        return containsSuspiciousPhrases(review)
                || isRepetitive(tokens)
                || isUninformative(tags);
    }
    // 1. Suspicious Phrase Check
    public boolean containsSuspiciousPhrases(String review) {
        String lower = review.toLowerCase();
        return suspiciousPhrases.stream().anyMatch(lower::contains);
    }
    // 2. Repetition Check
    public boolean isRepetitive(String[] tokens) {
        Set<String> unique = new HashSet<>(Arrays.asList(tokens));
        double uniquenessRatio = (double) unique.size() / tokens.length;
        return uniquenessRatio < Double.parseDouble(RULES.get("MIN_UNIQUENESS_RATIO"));
    }

    // 3. Informative Word Ratio Check (POS Tagging)
    public boolean isUninformative(String[] tags) {
        long informativeCount = Arrays.stream(tags)
                .filter(tag -> tag.contains("NOUN") || tag.contains("ADJ") || tag.contains("VERB"))
                .count();
        System.out.println("Informative Count: " + informativeCount);
        return ((double) informativeCount / tags.length) < Double.parseDouble(RULES.get("MIN_INFORMATIVE_WORD_RATIO"));
    }

}
