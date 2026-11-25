package com.bookloop;

import com.bookloop.algorithm.CycleDetection;
import com.bookloop.algorithm.ExchangeGraph;
import com.bookloop.model.ExchangeCycle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CycleDetectionTest {

    @Test
    @DisplayName("Should detect a 3-student circular exchange cycle (A -> B -> C -> A)")
    public void testThreeStudentCycle() {
        ExchangeGraph graph = new ExchangeGraph();
        graph.addStudent(1, "Alice");
        graph.addStudent(2, "Bob");
        graph.addStudent(3, "Carol");

        // Alice has Book X that Bob wants
        graph.addEdge(1, 2, 101, "Algorithms");
        // Bob has Book Y that Carol wants
        graph.addEdge(2, 3, 102, "Operating Systems");
        // Carol has Book Z that Alice wants
        graph.addEdge(3, 1, 103, "Database Systems");

        CycleDetection detector = new CycleDetection(graph, 4);
        List<ExchangeCycle> cycles = detector.findAllCycles();

        assertNotNull(cycles);
        assertEquals(1, cycles.size(), "Should discover exactly 1 simple 3-cycle");

        ExchangeCycle cycle = cycles.get(0);
        assertEquals(3, cycle.getLength(), "Cycle length should be 3 students");
        assertEquals(3, cycle.getSteps().size(), "Should have 3 handover steps");
    }

    @Test
    @DisplayName("Should detect direct 2-student mutual exchange (A <-> B)")
    public void testDirectTwoStudentCycle() {
        ExchangeGraph graph = new ExchangeGraph();
        graph.addStudent(10, "David");
        graph.addStudent(20, "Eva");

        graph.addEdge(10, 20, 201, "Computer Networks");
        graph.addEdge(20, 10, 202, "Digital Electronics");

        CycleDetection detector = new CycleDetection(graph, 4);
        List<ExchangeCycle> cycles = detector.findAllCycles();

        assertEquals(1, cycles.size());
        assertEquals(2, cycles.get(0).getLength());
    }

    @Test
    @DisplayName("Should return empty list when no cycles exist in acyclic graph")
    public void testAcyclicGraph() {
        ExchangeGraph graph = new ExchangeGraph();
        graph.addStudent(1, "Alice");
        graph.addStudent(2, "Bob");
        graph.addStudent(3, "Carol");

        graph.addEdge(1, 2, 101, "Book 1");
        graph.addEdge(2, 3, 102, "Book 2");

        CycleDetection detector = new CycleDetection(graph, 4);
        List<ExchangeCycle> cycles = detector.findAllCycles();

        assertTrue(cycles.isEmpty(), "Acyclic graph should have 0 cycles");
    }
}
