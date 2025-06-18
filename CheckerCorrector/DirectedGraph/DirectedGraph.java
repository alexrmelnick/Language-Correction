package DirectedGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;

import util.ListToString;

public class DirectedGraph<NodeClass> {
    private Map<NodeClass, List<NodeClass>> graph;

    public DirectedGraph() {
        graph = new HashMap<>();
    }

    public void addNode(NodeClass node) {
        graph.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(NodeClass source, NodeClass destination) {
        graph.get(source).add(destination);
    }

    public List<NodeClass> getAdjacentNodes(NodeClass node) {
        return graph.getOrDefault(node, new ArrayList<>());
    }
}