package com.sunny.player;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        int a=10,b=20;
        a^=b;
        b^=a;
        a^=b;
        System.out.println(a + " " + b);
        System.out.println(0x10000000);
        System.out.println(Integer.toBinaryString(Integer.MAX_VALUE));
        System.out.println(Long.toBinaryString(Long.MAX_VALUE));
        System.out.println(Integer.MAX_VALUE - 0x11111111);
    }
}