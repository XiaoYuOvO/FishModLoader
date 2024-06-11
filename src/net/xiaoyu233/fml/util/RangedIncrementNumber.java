package net.xiaoyu233.fml.util;

public class RangedIncrementNumber {
    private final int max;
    private int current;
    private RangedIncrementNumber(int initial, int max){
        this.current = initial;
        this.max = max;
    }

    public static RangedIncrementNumber create(int initial, int max){
        return new RangedIncrementNumber(initial, max);
    }

    public int getNext(){
        if (current < max){
            return current++;
        }
        throw new IndexOutOfBoundsException("The ranged increment number is overflow, max: " + max);
    }
}
