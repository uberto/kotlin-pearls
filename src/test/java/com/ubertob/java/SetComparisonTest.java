package com.ubertob.java;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SetComparisonTest {

    @Test
    public void testSetEqualAndHash(){
        Set<String> mySet = Set.of("abc", "de", "f");
        Set<String> myOtherSet = Set.of("de", "f", "abc");

        assertTrue(mySet.hashCode() == myOtherSet.hashCode());
        assertTrue(mySet.equals(myOtherSet));

    }


}