package util;

import java.util.Comparator;

public class LocComparator implements Comparator<IntLoc> {
    @Override
    public int compare(IntLoc intLoc, IntLoc t1) {
        if(intLoc.getX() == t1.getX())
            return intLoc.getY() - t1.getY();
        return intLoc.getX() - t1.getX();
    }
}
