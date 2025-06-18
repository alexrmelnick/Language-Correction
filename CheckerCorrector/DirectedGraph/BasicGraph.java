package DirectedGraph;
import StateMachine.State;

public class BasicGraph {
    public DirectedGraph<State> graph;
    public BasicGraph(){
        graph = new DirectedGraph<>();
        makeBasicGraph();
    }
    public void makeBasicGraph(){
        State cur = State.first();
        for(int i=0; i<State.values().length; i++){
            graph.addNode(cur);
            cur = cur.next();
        }

        cur = State.first();
        for(int i=1; i<State.values().length; i++){
            cur = cur.next();
            graph.addEdge(State.first(), cur);
        }

        graph.addEdge(State.START,     State.PRONOUN);
        graph.addEdge(State.PRONOUN,   State.VERB);
        graph.addEdge(State.VERB,      State.ADVERB);
        graph.addEdge(State.ADVERB,    State.ADJECTIVE);
        graph.addEdge(State.VERB,      State.ARTICLE);
        graph.addEdge(State.ARTICLE,   State.ADVERB);
        graph.addEdge(State.ARTICLE,   State.ADJECTIVE);
        graph.addEdge(State.ARTICLE,   State.NOUN);
        graph.addEdge(State.ADJECTIVE, State.DOT);
        graph.addEdge(State.ADJECTIVE, State.NOUN);
        graph.addEdge(State.ADJECTIVE, State.COMMA);
        graph.addEdge(State.NOUN,      State.DOT);
        graph.addEdge(State.NOUN,      State.COMMA);
        graph.addEdge(State.DOT,       State.END);
        //graph.addEdge(State.COMMA,     State.PRONOUN);
        graph.addEdge(State.COMMA,     State.CONJ);
        graph.addEdge(State.CONJ,      State.PRONOUN);
        graph.addEdge(State.PRONOUN,   State.CAN);
        graph.addEdge(State.CAN,       State.VERB);
        graph.addEdge(State.IF,        State.PRONOUN);
        graph.addEdge(State.THAT,      State.PRONOUN);
        graph.addEdge(State.IF,        State.NOUN);
        graph.addEdge(State.THAT,      State.NOUN);
        graph.addEdge(State.NOUN,      State.VERB);
        graph.addEdge(State.NOUN,      State.CAN);
        //graph.addEdge(State.VERB,      State.NOT);
        graph.addEdge(State.PRONOUN,   State.DOES);
        graph.addEdge(State.NOUN,      State.DOES);
        graph.addEdge(State.DOES,      State.NOT);
        graph.addEdge(State.NOT,       State.VERB);
        graph.addEdge(State.NOT,       State.ADVERB);
        graph.addEdge(State.NOT,       State.ADJECTIVE);
        graph.addEdge(State.NOT,       State.ARTICLE);
        graph.addEdge(State.OF,        State.NOUN);
        graph.addEdge(State.NOUN,      State.OF);
        graph.addEdge(State.NOUN,      State.IS);
        graph.addEdge(State.PRONOUN,   State.IS);
        graph.addEdge(State.IS,        State.ADJECTIVE);
        graph.addEdge(State.IS,        State.ADVERB);
        graph.addEdge(State.IS,        State.ARTICLE);
        graph.addEdge(State.THAT,      State.IF);


    }
    public DirectedGraph<State> getGraph() {
        return graph;
    }
}
