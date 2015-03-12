package com.fmi.parallelcomputing.utils.logs;

import com.fmi.parallelcomputing.utils.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by vivanovv on 3/12/2015.
 */
public class LogNormalizer {

    public static void main(String [] args) {
        File currentDir = new File("./logs_backup");
        String col [] = {"%-10s", "%-25s", "%-10s"};
        String val [] = {"%-10d", "%-25.5f", "%-10.5f"};
        StringBuilder newOutput = null;

        for(File file : currentDir.listFiles()) {
            if (file.getName().endsWith(".log")) {
                try {
                    FileReader reader = new FileReader(file.getName());
                    newOutput = new StringBuilder();

                    newOutput.append(String.format("%s\r\n",reader.getNextLine()));
                    newOutput.append(String.format("%s\r\n", reader.getNextLine()));
                    newOutput.append(String.format("%s\r\n", reader.getNextLine()));
                    newOutput.append(String.format(col[0], reader.getNextString()));
                    newOutput.append(String.format(col[1], reader.getNextString() + " " + reader.getNextString() + " " + reader.getNextString()));
                    newOutput.append(String.format(col[2], "Improvement koefficient\r\n"));

                    double normalExecTime = -10.0;
                    while (true) {
                        int threadCount = reader.getNextInt();
                        double execTime = reader.getNextDouble();

                        if (normalExecTime < 0) {
                            normalExecTime = execTime;
                        }

                        newOutput.append(String.format(val[0],threadCount));
                        newOutput.append(String.format(val[1],execTime));
                        newOutput.append(String.format(val[2],normalExecTime / execTime));
                        newOutput.append("\r\n");
                    }
                } catch (Exception e) {
                    System.out.println(newOutput.toString());
//                    try {
//                        PrintWriter out = new PrintWriter(file);
//                        out.print(newOutput.toString());
//                        out.flush();
//                        out.close();
//                    } catch (FileNotFoundException e1) {
//                    }
                }
            }
        }
    }
}
