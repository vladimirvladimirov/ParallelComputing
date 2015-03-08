package com.fmi.parallelcomputing.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * This class provides functionality for reading from file.
 */
public class FileReader implements Reader {

    private BufferedReader in;

    private StringTokenizer tok;

    public FileReader(String filename) throws IOException {
        in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
    }

    @Override
    public int getNextInt() throws IOException {
        prepareToken();
        return Integer.parseInt(tok.nextToken());
    }


    @Override
    public long getNextLong() throws IOException {
        prepareToken();
        return Long.parseLong(tok.nextToken());
    }

    @Override
    public String getNextString()  throws IOException{
        prepareToken();
        return tok.nextToken();
    }

    @Override
    public String getNextLine()  throws IOException{
        prepareToken();
        String wholeLine = tok.toString();
        tok = null;
        return wholeLine;
    }

    private void prepareToken() throws IOException {
        while (tok == null || !tok.hasMoreTokens()) {
            tok = new StringTokenizer(in.readLine());
        }
    }
}
