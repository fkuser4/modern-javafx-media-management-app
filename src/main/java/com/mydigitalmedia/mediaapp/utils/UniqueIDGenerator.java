package com.mydigitalmedia.mediaapp.utils;

import java.util.UUID;

public class UniqueIDGenerator {
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}