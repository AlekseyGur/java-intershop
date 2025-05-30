package ru.alexgur.blog;

import java.util.Random;

public class UtilsTests {

    public static String genEmail() {
        return UtilsTests.genString(15) + "@test.test";
    }

    public static String genString(int length) {
        String randomChars = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomChars += (char) (random.nextInt(26) + 'a');
        }
        return randomChars;
    }
}
