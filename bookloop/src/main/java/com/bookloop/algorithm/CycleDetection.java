package com.bookloop.algorithm;

import com.bookloop.model.ExchangeCycle;

import java.util.*;

public class CycleDetection {
    private final ExchangeGraph graph;
    private final int maxCycleLength;

    public CycleDetection(ExchangeGraph graph, int maxCycleLength) {
        this.graph = graph;
        this.maxCycleLength = maxCycleLength;
    }

    public List<ExchangeCycle> findAllCycles() {
        List<ExchangeCycle> result = new ArrayList<>();
        Set<String> seenHashes = new HashSet<>();

        for (int startNode : graph.getAllStudents()) {
            List<Integer> path = new ArrayList<>();
            List<ExchangeGraph.Edge> edgePath = new ArrayList<>();
            Set<Integer> visited = new HashSet<>();

            dfs(startNode, startNode, path, edgePath, visited, result, seenHashes);
        }

        return result;
    }

    public List<ExchangeCycle> findCyclesForStudent(int studentId) {
        List<ExchangeCycle> all = findAllCycles();
        List<ExchangeCycle> studentCycles = new ArrayList<>();
        for (ExchangeCycle c : all) {
            if (c.getParticipantIds().contains(studentId)) {
                studentCycles.add(c);
            }
        }
        return studentCycles;
    }

    private void dfs(int startNode, int currentNode,
                     List<Integer> path,
                     List<ExchangeGraph.Edge> edgePath,
                     Set<Integer> visited,
                     List<ExchangeCycle> result,
                     Set<String> seenHashes) {

        if (path.size() >= maxCycleLength) return;

        for (ExchangeGraph.Edge edge : graph.getNeighbors(currentNode)) {
            int nextNode = edge.getToStudentId();

            // Found a cycle back to startNode! (Cycles must have length >= 2)
            if (nextNode == startNode && path.size() >= 1) {
                List<Integer> cycleNodes = new ArrayList<>(path);
                cycleNodes.add(currentNode);
                String hash = canonicalCycleHash(cycleNodes);

                if (!seenHashes.contains(hash)) {
                    seenHashes.add(hash);

                    ExchangeCycle cycle = new ExchangeCycle();
                    cycle.setCycleHash(hash);
                    cycle.setParticipantIds(new ArrayList<>(cycleNodes));
                    cycle.setLength(cycleNodes.size());

                    List<ExchangeCycle.Step> steps = new ArrayList<>();
                    for (int i = 0; i < edgePath.size(); i++) {
                        ExchangeGraph.Edge e = edgePath.get(i);
                        int giver = path.get(i);
                        int receiver = (i + 1 < path.size()) ? path.get(i + 1) : currentNode;

                        ExchangeCycle.Step step = new ExchangeCycle.Step();
                        step.setPositionOrder(i + 1);
                        step.setGiverId(giver);
                        step.setGiverName(graph.getStudentName(giver));
                        step.setReceiverId(receiver);
                        step.setReceiverName(graph.getStudentName(receiver));
                        step.setListingId(e.getListingId());
                        step.setBookTitle(e.getBookTitle());
                        steps.add(step);
                    }

                    // Add final edge closing the loop
                    ExchangeCycle.Step lastStep = new ExchangeCycle.Step();
                    lastStep.setPositionOrder(steps.size() + 1);
                    lastStep.setGiverId(currentNode);
                    lastStep.setGiverName(graph.getStudentName(currentNode));
                    lastStep.setReceiverId(startNode);
                    lastStep.setReceiverName(graph.getStudentName(startNode));
                    lastStep.setListingId(edge.getListingId());
                    lastStep.setBookTitle(edge.getBookTitle());
                    steps.add(lastStep);

                    cycle.setSteps(steps);
                    result.add(cycle);
                }
            } else if (!visited.contains(nextNode) && nextNode != startNode) {
                visited.add(nextNode);
                path.add(currentNode);
                edgePath.add(edge);

                dfs(startNode, nextNode, path, edgePath, visited, result, seenHashes);

                path.remove(path.size() - 1);
                edgePath.remove(edgePath.size() - 1);
                visited.remove(nextNode);
            }
        }
    }

    private String canonicalCycleHash(List<Integer> nodes) {
        List<Integer> sorted = new ArrayList<>(nodes);
        Collections.sort(sorted);
        StringBuilder sb = new StringBuilder();
        for (int n : sorted) {
            sb.append(n).append("-");
        }
        return sb.toString();
    }
}
