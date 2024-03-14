package edu.uob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataProcess {
    // This class is used to process series of data when one specific table is selected
    private String dataStatus;

    public String dataInsert(ArrayList<String> data, String path) throws IOException {
        // In here process the process method
        FileWriter writer = new FileWriter(String.valueOf(path), true);
        BufferedWriter buffer = new BufferedWriter(writer);
        for (int i = 0; i < data.size(); i++) {
            buffer.write(data.get(i));
            if (i != data.size() - 1) {
                buffer.write("\t");
            }else{
                writer.write("\n");
            }
        }
        buffer.close();
        writer.close();
        dataStatus = "[OK]";
        return dataStatus;
    }
}
