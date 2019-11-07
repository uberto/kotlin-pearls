package com.ubertob.java;

public class Main {

    static public void main(String[] args){

        System.out.println("here");
        System.out.println("name " + SingletonWithStaticInit.INSTANCE.getName());
        System.out.println("doublename " + SingletonWithStaticInit.INSTANCE.getDoubleName());
    }
}
