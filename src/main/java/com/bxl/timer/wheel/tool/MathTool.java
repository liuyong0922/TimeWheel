package com.bxl.timer.wheel.tool;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/19
 */
public class MathTool {

    public static void main(String[] args) {
        System.out.println(MathTool.power(8, 10));
        System.out.println(Math.pow(8, 10));
    }
    //整数 a的n次幂。
    //例如 n=7  二进制 0   1                 1          1
    //a的n次幂 = a的2次幂×a的2次幂  ×   a的1次幂×a的1次幂  ×a
    public static long power(long a, int n) {
        int rtn = 1;
        while (n >= 1) {
            if((n & 1) == 1){
                rtn *= a;
            }
            a *= a;
            n = n >> 1;
        }
        return rtn;
    }
}
