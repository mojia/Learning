package com.m.server.location.mean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MeanTest {

    public static void main(String[] args) {
        String path = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {

        }
    }
}
