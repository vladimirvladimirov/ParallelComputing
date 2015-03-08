package com.fmi.parallelcomputing.core.testgen;

import com.fmi.parallelcomputing.core.Constants;
import com.fmi.parallelcomputing.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Test generator for the Partition problem. The whole idea is to check whether given set of integers can be divided
 * in two part so that the sum of the elements in each subset is the same.
 */
public class PartitionProblemTestGenerator {

    private PrintWriter setOut;

    private Map<Constants.PartitionProblemConstants, Integer> inputProperties;

    private int numberCount;

    private List<Integer> generatedSet;


    public PartitionProblemTestGenerator(Map<Constants.PartitionProblemConstants, Integer> inputProperties, String inputFilename) throws FileNotFoundException {
        this.inputProperties = inputProperties;
        setOut = new PrintWriter(new FileOutputStream(inputFilename));
    }

    public static void main(String [] args) throws Exception {
        Map<Constants.PartitionProblemConstants, Integer> inputConstraints = new HashMap<Constants.PartitionProblemConstants, Integer>();
        inputConstraints.put(Constants.PartitionProblemConstants.NUMBERS_COUNT, 32);
        inputConstraints.put(Constants.PartitionProblemConstants.MIN_NUMBER, 0);
        inputConstraints.put(Constants.PartitionProblemConstants.MAX_NUMBER, 100);

        PartitionProblemTestGenerator generator = new PartitionProblemTestGenerator(inputConstraints, Constants.PARTITION_INPUT_FILENAME);
        generator.createInput();
        generator.printInput();
    }

    private void printInput() {
        setOut.println(numberCount);
        for(int i=0; i<Math.max(numberCount,generatedSet.size()); i++) {
            setOut.print(String.format("%d ", generatedSet.get(i)));
        }
        setOut.flush();
        setOut.close();
    }

    private void createInput() {
        numberCount = inputProperties.get(Constants.PartitionProblemConstants.NUMBERS_COUNT);
        generatedSet = new LinkedList<Integer>();

        int minNumber = inputProperties.get(Constants.PartitionProblemConstants.MIN_NUMBER);
        int maxNumber = inputProperties.get(Constants.PartitionProblemConstants.MAX_NUMBER);

        for(int i=0; i<numberCount; i++) {
            generatedSet.add(Utils.getIntInRange(minNumber,maxNumber));
        }
    }

}
