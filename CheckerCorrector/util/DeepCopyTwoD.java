package util;

public class DeepCopyTwoD {
    private int[][] array;

    private DeepCopyTwoD(int[][] array) {
        this.array = array;
    }

    public static DeepCopyTwoD createEmpty() {
        return new DeepCopyTwoD(new int[0][0]);
    }

    public void setArray(int[][] newArray) {
        // Perform deep copy of the input array
       // displayArray();
        int[][] copiedArray = new int[newArray.length][];
        for (int i = 0; i < newArray.length; i++) {
            copiedArray[i] = new int[newArray[i].length];
            System.arraycopy(newArray[i], 0, copiedArray[i], 0, newArray[i].length);
        }
        
        this.array = copiedArray;
    }

    public int[][] getArray() {
        // Perform deep copy before returning the array
        //displayArray();
        int[][] copiedArray = new int[array.length][];
        for (int i = 0; i < array.length; i++) {
            copiedArray[i] = new int[array[i].length];
            System.arraycopy(array[i], 0, copiedArray[i], 0, array[i].length);
        }
        
        return copiedArray;
    }

    public void displayArray() {
        for (int[] row : array) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------");
    }
}
