package com.fmi.parallelcomputing.utils;

import java.io.IOException;

/**
 * Created by Dev on 3/6/2015.
 */
public interface Reader {

    public int getNextInt() throws IOException;

    public long getNextLong()throws IOException;

    public String getNextString()throws IOException;

    public String getNextLine()throws IOException;

}
