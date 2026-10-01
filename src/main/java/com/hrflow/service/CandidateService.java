package com.hrflow.service;

import com.hrflow.dsa.search.SkillSearchManager;
import com.hrflow.dsa.sort.CandidateSortEngine;
import com.hrflow.dsa.stack.HRActionStack;
import com.hrflow.model.Candidate;
import com.hrflow.model.HRAction;
import com.hrflow.storage.DatabaseManager;

import java.util.*;

/**
 * Service managing Candidates, Skill indexing (HashMap + Linear Search),
 * Ranking (Selection Sort & Insertion Sort), and Undo History (Stack).
 */
public class CandidateService {
    private final DatabaseManager db;
    private final List<Candidate> candidates;
    private final SkillSearchManager searchManager;
    private final HRActionStack actionStack;

    public CandidateService() {
        this.db = DatabaseManager.getInstance();
        this.candidates = new ArrayList<>();
        this.searchManager = new SkillSearchManager();
        this.actionStack = new HRActionStack();
        reload();
    }

    public synchronized void reload() {
        candidates.clear();
        searchManager.clear();
        candidates.addAll(db.loadAllCandidates());

        for (Candidate c : candidates) {
            for (String skill : c.getSkills()) {
                searchManager.registerSkill(skill, c);
            }
        }
    }

    public List<Candidate> getAllCandidates() {
        return Collections.unmodifiableList(candidates);
    }

    /**
     * Skill autocomplete using Linear Search filtering over known skills.
     */
    public List<String> autocompleteSkills(String query, int limit) {
        return searchManager.autocomplete(query, limit);
    }

    /**
     * O(1) Candidate lookup via HashMap index.
     */
    public Set<Candidate> searchCandidatesBySkill(String skill) {
        return searchManager.getCandidatesBySkill(skill);
    }

    /**
     * Multi-skill matching using HashSet and nested loop verification.
     */
    public Set<Candidate> searchCandidatesBySkills(Collection<String> skills) {
        return searchManager.findCandidatesWithAllSkills(skills, candidates);
    }

    /**
     * Ranks candidates against required skills and extracts Top-K using Selection Sort.
     */
    public List<Candidate> rankCandidates(Set<String> requiredSkills, int topK) {
        return CandidateSortEngine.rankAndGetTopK(candidates, requiredSkills, topK);
    }

    /**
     * Sorts all candidates by score using Insertion Sort.
     */
    public List<Candidate> getCandidatesSortedByInsertionSort() {
        return CandidateSortEngine.sortByInsertionSort(candidates);
    }

    /**
     * Updates candidate status with Undo Stack tracking.
     */
    public void updateCandidateStatus(Candidate candidate, Candidate.CandidateStatus newStatus) {
        Candidate.CandidateStatus oldStatus = candidate.getStatus();
        candidate.setStatus(newStatus);
        db.insertCandidates(List.of(candidate));

        // Push to Undo Stack with both undo and redo logic
        actionStack.pushAction(new HRAction(
                UUID.randomUUID().toString(),
                newStatus == Candidate.CandidateStatus.SHORTLISTED ? HRAction.ActionType.SHORTLIST :
                newStatus == Candidate.CandidateStatus.REJECTED ? HRAction.ActionType.REJECT : HRAction.ActionType.STATUS_CHANGE,
                String.format("Changed %s status from %s to %s", candidate.getName(), oldStatus, newStatus),
                () -> {
                    // Reversible undo callback
                    candidate.setStatus(oldStatus);
                    db.insertCandidates(List.of(candidate));
                },
                () -> {
                    // Re-apply redo callback
                    candidate.setStatus(newStatus);
                    db.insertCandidates(List.of(candidate));
                }
        ));
    }

    /**
     * Executes Undo from Stack.
     */
    public HRAction undoLastAction() {
        return actionStack.undo();
    }

    /**
     * Executes Redo from Stack.
     */
    public HRAction redoLastAction() {
        return actionStack.redo();
    }

    public boolean canUndo() {
        return actionStack.canUndo();
    }

    public boolean canRedo() {
        return actionStack.canRedo();
    }

    public List<HRAction> getActionHistory() {
        return actionStack.getHistoryAuditLog();
    }

    public HRActionStack getActionStack() {
        return actionStack;
    }

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
        for (String skill : candidate.getSkills()) {
            searchManager.registerSkill(skill, candidate);
        }
        db.insertCandidates(List.of(candidate));
    }
}
