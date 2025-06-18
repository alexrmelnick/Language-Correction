package util;

import java.util.ArrayList;
import java.util.List;

public class TwoListStruct<NodeClass, ChangeClass> {
    ArrayList<NodeClass> outputList;
    ArrayList<ChangeClass> changesList;

    TwoListStruct(List<NodeClass> outputList, List<ChangeClass> changesList) {
        this.outputList = new ArrayList<>();
        this.changesList = new ArrayList<>();
        this.outputList.addAll(outputList);
        this.changesList.addAll(changesList);
    }

    public static <NC, CC> TwoListStruct<NC, CC> of (List<NC> outputList, List<CC> changesList){
        return new TwoListStruct<>(outputList, changesList);
    }

    public List<ChangeClass> getChangesList() {
        return changesList;
    }
    public List<NodeClass> getOutputList() {
        return outputList;
    }
    public void displayArrays(){
        for(NodeClass out: outputList)
            System.out.print(out + ", ");
        System.out.println("\n-------------");
        for(ChangeClass cha: changesList)
            System.out.print(cha + ", ");
        System.out.println("\n***************************************************");
    }
}