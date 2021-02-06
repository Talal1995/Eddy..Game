package project;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class Serialization {
    private int counter = 1;

    public Serialization(){

    }
    public void Serialize(Object obj) throws FileNotFoundException {
        HashMap<UUID, int[][]> matrix = (HashMap<UUID, int[][]>) obj;
        String filename = "heatmap" + counter++;
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("./src/main/BOISEN/BinaryHeatmaps/" + filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(matrix);
            out.close();
            fileOut.close();
        }catch(Exception e){e.printStackTrace();}

    }

    public HashMap<UUID, int[][]> DeSerialize(){
        try{
            FileInputStream fileIn = new FileInputStream("./src/main/BOISEN/BinaryHeatmaps/heatmap1");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            HashMap<UUID, int[][]> matrix = (HashMap<UUID, int[][]>) in.readObject();
            in.close();
            fileIn.close();
            return matrix;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
