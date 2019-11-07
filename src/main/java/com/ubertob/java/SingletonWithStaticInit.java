package com.ubertob.java;

public class SingletonWithStaticInit {

    static String name;
    static String doubleName;

    public static SingletonWithStaticInit INSTANCE;

    static {

        INSTANCE = new SingletonWithStaticInit();

        name = "myName";

        doubleName = INSTANCE.getName() + INSTANCE.getName();

    }

    public final String getName(){
        return name;
    }

    public final String getDoubleName(){
        return doubleName;
    }


}
