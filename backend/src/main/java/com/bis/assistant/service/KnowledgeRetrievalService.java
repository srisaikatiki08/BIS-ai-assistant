package com.bis.assistant.service;

import com.bis.assistant.model.KnowledgeChunk;
import com.bis.assistant.repository.KnowledgeChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KnowledgeRetrievalService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeRetrievalService.class);

    private static final Set<String> STOP_WORDS = Set.of(
            "what", "is", "the", "a", "an", "are", "for", "to", "of",
            "in", "on", "and", "or", "how", "which", "does", "do", "can",
            "tell", "me", "about", "with", "at", "by", "from", "as", "into",
            "it", "its", "that", "this", "these", "those", "i", "you", "we",
            "give", "show", "details", "please", "find"
    );

    private static final int MAX_RESULTS = 5;
    private static final int MIN_WORD_LENGTH = 3;

    private final KnowledgeChunkRepository knowledgeChunkRepository;

    public KnowledgeRetrievalService(KnowledgeChunkRepository knowledgeChunkRepository) {
        this.knowledgeChunkRepository = knowledgeChunkRepository;
    }

    /**
     * Extracts useful keywords, retrieves candidate KnowledgeChunks from PostgreSQL,
     * scores each chunk deterministically for query relevance, and returns the top 5 highest-ranked chunks.
     */
    public List<KnowledgeChunk> retrieveRelevantChunks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> keywords = extractKeywords(query);
        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Gather candidate chunks across all query keywords (deduplicated by primary key)
        Map<Long, KnowledgeChunk> candidateMap = new LinkedHashMap<>();

        for (String keyword : keywords) {
            List<KnowledgeChunk> matched = knowledgeChunkRepository.searchByKeyword(keyword);
            if (matched != null) {
                for (KnowledgeChunk chunk : matched) {
                    if (chunk != null && chunk.getId() != null) {
                        candidateMap.putIfAbsent(chunk.getId(), chunk);
                    }
                }
            }
        }

        if (candidateMap.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Score each candidate chunk deterministically
        List<ScoredChunk> scoredChunks = new ArrayList<>();
        for (KnowledgeChunk chunk : candidateMap.values()) {
            int score = calculateRelevanceScore(chunk, query, keywords);
            if (score > 0) {
                scoredChunks.add(new ScoredChunk(chunk, score));
            }
        }

        // 3. Sort by descending relevance score (with ID ascending as stable tie-breaker)
        scoredChunks.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.score, a.score);
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Long.compare(
                    a.chunk.getId() != null ? a.chunk.getId() : 0L,
                    b.chunk.getId() != null ? b.chunk.getId() : 0L
            );
        });

        // 4. Return top 5 highest-scoring chunks
        return scoredChunks.stream()
                .limit(MAX_RESULTS)
                .map(sc -> sc.chunk)
                .toList();
    }

    /**
     * Calculates a deterministic relevance score for a chunk against the query.
     */
    public int calculateRelevanceScore(KnowledgeChunk chunk, String rawQuery, List<String> keywords) {
        if (chunk == null || keywords == null || keywords.isEmpty()) {
            return 0;
        }

        int score = 0;
        int distinctMatchedKeywords = 0;
        String queryLower = rawQuery != null ? rawQuery.toLowerCase() : "";

        String doc = chunk.getDocument() != null ? chunk.getDocument() : "";
        String title = chunk.getTitle() != null ? chunk.getTitle() : "";
        String section = chunk.getSection() != null ? chunk.getSection() : "";
        String clause = chunk.getClause() != null ? chunk.getClause() : "";
        String content = chunk.getContent() != null ? chunk.getContent() : "";

        for (String keyword : keywords) {
            boolean matchedAnyField = false;

            // Document name match (+10)
            if (containsWord(doc, keyword)) {
                score += 10;
                matchedAnyField = true;
            }

            // Title match (+8)
            if (containsWord(title, keyword)) {
                score += 8;
                matchedAnyField = true;
            }

            // Section match (+6)
            if (containsWord(section, keyword)) {
                score += 6;
                matchedAnyField = true;
            }

            // Clause match (+5)
            if (containsWord(clause, keyword)) {
                score += 5;
                matchedAnyField = true;
            }

            // Content occurrences (+3 per occurrence, max 5 occurrences = max +15)
            int contentCount = countOccurrences(content, keyword);
            if (contentCount > 0) {
                score += Math.min(contentCount, 5) * 3;
                matchedAnyField = true;
            }

            if (matchedAnyField) {
                distinctMatchedKeywords++;
            }
        }

        // Multi-term co-occurrence bonus (+15 for each additional distinct matched keyword)
        if (distinctMatchedKeywords > 1) {
            score += (distinctMatchedKeywords - 1) * 15;
        }

        // Exact Standard Identifier Match Bonus (+35)
        if (!doc.isBlank()) {
            String cleanDoc = doc.replaceAll("\\s+", " ").trim().toLowerCase();
            if (queryLower.contains(cleanDoc)) {
                score += 35;
            } else {
                String docPrefixNum = cleanDoc.replaceAll("(?i):\\d{4}.*$", "").trim();
                if (!docPrefixNum.isBlank() && docPrefixNum.length() >= 4 && queryLower.contains(docPrefixNum)) {
                    score += 35;
                }
            }
        }

        // Exact phrase match bonus (+20)
        if (keywords.size() >= 2) {
            String combinedKeywords = String.join(" ", keywords);
            if (content.toLowerCase().contains(combinedKeywords) || title.toLowerCase().contains(combinedKeywords)) {
                score += 20;
            }
        }

        return score;
    }

    /**
     * Builds a structured text representation of the retrieved knowledge chunks
     * to inject into the Gemini system prompt for grounding.
     */
    public String buildKnowledgeContext(String query) {
        List<KnowledgeChunk> chunks = retrieveRelevantChunks(query);
        return buildKnowledgeContext(chunks);
    }

    /**
     * Formats retrieved knowledge chunks into structured source blocks.
     */
    public String buildKnowledgeContext(List<KnowledgeChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return "No directly matching BIS knowledge chunks were found in the local knowledge base.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            KnowledgeChunk chunk = chunks.get(i);
            sb.append(String.format("[Source %d]%n", i + 1));

            if (chunk.getDocument() != null && !chunk.getDocument().isBlank()) {
                sb.append("Document: ").append(chunk.getDocument().trim()).append("\n");
            }
            if (chunk.getTitle() != null && !chunk.getTitle().isBlank()) {
                sb.append("Title: ").append(chunk.getTitle().trim()).append("\n");
            }
            if (chunk.getSection() != null && !chunk.getSection().isBlank()) {
                sb.append("Section: ").append(chunk.getSection().trim()).append("\n");
            }
            if (chunk.getClause() != null && !chunk.getClause().isBlank()) {
                sb.append("Clause: ").append(chunk.getClause().trim()).append("\n");
            }
            if (chunk.getPageNumber() != null) {
                sb.append("Page: ").append(chunk.getPageNumber()).append("\n");
            }
            if (chunk.getContent() != null && !chunk.getContent().isBlank()) {
                sb.append("Content: ").append(chunk.getContent().trim()).append("\n");
            }
            if (chunk.getSourceUrl() != null && !chunk.getSourceUrl().isBlank()) {
                sb.append("Source URL: ").append(chunk.getSourceUrl().trim()).append("\n");
            }

            if (i < chunks.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString().trim();
    }

    /**
     * Tokenizes query into distinct, non-stopword keywords of meaningful length.
     */
    public List<String> extractKeywords(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Split by non-alphanumeric characters into standard words
        String[] tokens = query.toLowerCase().split("[^a-zA-Z0-9]+");
        Set<String> keywords = new LinkedHashSet<>();

        for (String rawToken : tokens) {
            String token = rawToken.trim();
            if (token.length() >= MIN_WORD_LENGTH && !STOP_WORDS.contains(token)) {
                keywords.add(token);
            }
        }

        return new ArrayList<>(keywords);
    }

    private int countOccurrences(String text, String keyword) {
        if (text == null || text.isBlank() || keyword == null || keyword.isBlank()) {
            return 0;
        }
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private boolean containsWord(String text, String keyword) {
        if (text == null || text.isBlank() || keyword == null || keyword.isBlank()) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(text).find();
    }

    private record ScoredChunk(KnowledgeChunk chunk, int score) {}
}
