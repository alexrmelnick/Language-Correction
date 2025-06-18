package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import StateMachine.State;

public class ListToString {
    StringBuilder finalString;
    private ListToString(){
        finalString = new StringBuilder();
    }
    public static ListToString of (){
        return new ListToString();
    }
    public void addString(State s){
        finalString.append(State.mapToChar(s));//.toString().substring(0,3));
        finalString.append(".");
    }
    public String getString(){
        return finalString.toString();
    }
}
