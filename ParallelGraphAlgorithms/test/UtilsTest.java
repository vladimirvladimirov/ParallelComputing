import com.fmi.parallelcomputing.utils.Interval;
import com.fmi.parallelcomputing.utils.Utils;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void testGetIntInRange() {
        Random rand = new Random();

        for(int i=1; i<100000; i++) {
            int from = Math.abs(rand.nextInt());
            int to = Math.abs(rand.nextInt());

            if (from > to) {
                int tmp = from;
                from = to;
                to = tmp;
            }

            int g = Utils.getIntInRange(from, to);
            assertTrue(g >= from && g <= to);
        }
    }

    @Test
         public void testDivisionLoad() {
        for(int i=0; i<50; i++) {
            int totalLoad = Utils.getIntInRange(100,1000);
            int threadCount = Utils.getIntInRange(1,20);
            List<Interval> division = Utils.divideLoad(totalLoad, threadCount);
//            System.out.println(String.format("Load: %d Threads: %d",totalLoad,threadCount));

            int totalIntervalLength = 0;
            for(Interval interval : division) {
//                System.out.println(String.format("%d %d = %d",interval.getA(),interval.getB(),interval.getB() - interval.getA() + 1));
                totalIntervalLength += interval.getB() - interval.getA() + 1;
            }
            assertEquals(threadCount, division.size());
            assertEquals(totalLoad, totalIntervalLength);
        }
    }
    @Test
    public void testDivisionLoadStartingFrom() {
        for(int i=0; i<50; i++) {
            int totalLoad = Utils.getIntInRange(100,1000);
            int threadCount = Utils.getIntInRange(1,20);
            int startingFrom = Utils.getIntInRange(-10,10);
            List<Interval> division = Utils.divideLoad(totalLoad, threadCount, startingFrom);
            System.out.println(String.format("Load: %d Threads: %d Starting from %d",totalLoad,threadCount, startingFrom));

            int totalIntervalLength = 0;
            for(Interval interval : division) {
                System.out.println(String.format("%d %d = %d",interval.getA(),interval.getB(),interval.getB() - interval.getA() + 1));
                totalIntervalLength += interval.getB() - interval.getA() + 1;
            }
            assertEquals(startingFrom, division.get(0).getA());
            assertEquals(threadCount, division.size());
            assertEquals(totalLoad, totalIntervalLength);
        }
    }
}