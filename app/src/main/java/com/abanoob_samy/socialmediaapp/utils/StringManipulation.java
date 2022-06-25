package com.abanoob_samy.socialmediaapp.utils;

public class StringManipulation {

    public static String expandUsername(String userName) {

        return userName.replace(".", " ");
    }

    public static String condenseUsername(String userName) {

        return userName.replace(" ", ".");
    }
}
