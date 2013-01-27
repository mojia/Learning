package com.m.server.httpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class PowerHttpClient {

    public void post(String filePath, String postUrl) {
        String content = preparedContent(filePath);

        // 调用curl
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(Arrays.asList("curl", "-d", content, postUrl));
        Process process = null;

        try {
            process = processBuilder.start();
            output(process);
            outputErr(process);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void outputErr(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                process.getErrorStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    private void output(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    private String preparedContent(String filepath) {
        StringBuffer result = new StringBuffer();

        File file = new File(filepath);
        if (!file.exists()) {
            System.out.println("file not exists!");
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return null;
        } catch (IOException e) {
            System.err.println(e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }
}
