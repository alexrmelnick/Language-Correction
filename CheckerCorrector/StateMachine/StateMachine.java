package StateMachine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DirectedGraph.DirectedGraph;
import DirectedGraph.DFS;

import util.TwoListStruct;
import util.ListToString;
import util.StringFileWriter;
import util.StringToList;
import TypoCorrector.TypoCorrector;

public class StateMachine{

    public int isStateMachineFollowed(DirectedGraph<State> graph, List<State> actions, State initialState, int initialConf) {
        int confidence = initialConf;
        ////System.out.println("----------------------------------------------------");
        State currentState = initialState;
        for (State action : actions) {
            ////System.out.print(currentState);
            List<State> transitions = graph.getAdjacentNodes(currentState);
            if (!transitions.contains(action)) {
                ////System.out.print("? " + action);
                currentState = State.START;
                confidence += 10; // Action not allowed in current state
                
            }
            ////System.out.println();
            currentState = action; // Transition to the next state
        }
        return confidence;
    }
    public TwoListStruct suggestedStateMachine(DirectedGraph<State> graph, List<State> actions, State initialState) {
        ////System.out.println("----------------------------------------------------");
        State currentState = initialState;
        boolean flag = false;
        List<State> suggestedAction = new ArrayList<>();
        List<Integer> flags         = new ArrayList<>();
        State tempState = currentState;
        //suggestedAction.add(currentState);
        int cnt = 0;

        StateMachine SM = new StateMachine();
        int confidence = SM.isStateMachineFollowed(graph, actions, actions.get(0), 0);
        if(confidence<10){
            for(int i =0; i<actions.size(); i++)
                flags.add(0);
            return TwoListStruct.of(actions, flags);
        }else{
            Set<String> allPaths = new HashSet<>();
            DFS dfs = DFS.of();
            allPaths.addAll(dfs.dfs(graph, actions.get(0), actions.size()+1));
            StringFileWriter sfw = StringFileWriter.of("all_path.txt", "\n");
            for(String path: allPaths)
                sfw.appendString(path);
            try {
                sfw.writeToFile();
                TypoCorrector tc = TypoCorrector.of("all_path.txt", true, -6, 0, -3, -1);
                ListToString lts = ListToString.of();
                for(State action: actions)
                    lts.addString(action);
                String suggestedActionsString = tc.closestWord(lts.getString());
                ////System.out.println(lts.getString() + " -> " + suggestedActionsString);
                List<State> parts = StringToList.split(suggestedActionsString);
                suggestedAction.addAll(parts);
                flags.addAll(tc.traceBack());
                return TwoListStruct.of(suggestedAction, flags);
            } catch (IOException e) {
                System.err.println("An error occurred while writing to the file: " + e.getMessage());
            }

        }
        return TwoListStruct.of(suggestedAction, flags);
    }


    /*
    public TwoListStruct suggestedStateMachine(DirectedGraph<State> graph, List<State> actions, State initialState) {
        
        ////System.out.println("----------------------------------------------------");
        State currentState = initialState;
        boolean flag = false;
        List<State> suggestedAction = new ArrayList<>();
        List<Integer> flags         = new ArrayList<>();
        State tempState = currentState;
        //suggestedAction.add(currentState);
        int cnt = 0;
        for(int i=0; i<actions.size(); i++){
        //for (State action : actions) {
            State action = actions.get(i);
            //System.out.print(currentState);
            
            List<State> transitions = graph.getAdjacentNodes(currentState);
            if(flag && cnt <2){
                List<State> tempTransitions = graph.getAdjacentNodes(tempState);
                for(State checkState: tempTransitions){
                    if(checkState != State.START){
                        List<State> checkTransitions = graph.getAdjacentNodes(checkState);
                        if(checkTransitions.contains(action)){
                            //System.out.print("| updated to: "+ checkState);
                            suggestedAction.add(checkState);
                            flags.add(1);
                        }
                    }
                }
                flag = false;
                cnt = 0;
            }
            if (!transitions.contains(action)) {
                //System.out.print("? " + action);
                tempState = currentState;
                currentState = State.START;
                flag = true;
                cnt ++;
            }
            if(!flag){
                suggestedAction.add(currentState);
                System.out.print("| no update");
                flags.add(0);
                currentState = action; // Transition to the next state
            }else if(cnt <2){
                suggestedAction.add(currentState);
                flags.add(0);
                System.out.print("| no update");
                List<State> tempTransitions = graph.getAdjacentNodes(tempState);
                for(State checkState: tempTransitions){
                    if(checkState != State.START){
                        List<State> checkTransitions = graph.getAdjacentNodes(checkState);
                        if(checkTransitions.contains(action)){
                            suggestedAction.add(checkState);
                            System.out.print("| updated to with missing: "+ checkState + " =)");
                            flags.add(2);
                            currentState = checkState;
                            tempState = currentState;
                            i--;
                        }
                    }
                }
                flag = false;
                cnt = 0;
            }else{
                currentState = action; // Transition to the next state
            }
            System.out.println();
            //System.out.println();
            
        }
        
        return TwoListStruct.of(suggestedAction, flags);
    }
     */
}