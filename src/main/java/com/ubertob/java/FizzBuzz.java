package com.ubertob.java;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FizzBuzz {

    public static final String or(@Nullable String str1,
                                  @Nullable String str2) {
        if (str1 == null)
            return str2;
        else if (str2 == null)
            return str1;
        else
            return str1 + str2;
    }

    public static final String isFizz(int number) {
        return number % 3 == 0 ? "Fizz" : null;
    }

    public static final String isBuzz(int number) {
        return number % 5 == 0 ? "Buzz" : null;
    }

    @NotNull
    public static final String fizzBuzz(int number) {
        String fizzBuzz = or(isFizz(number), isBuzz(number));
        if (fizzBuzz == null) {
            fizzBuzz = String.valueOf(number);
        }

        return fizzBuzz;
    }
}
