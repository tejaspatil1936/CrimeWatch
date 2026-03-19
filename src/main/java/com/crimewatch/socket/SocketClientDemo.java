package com.crimewatch.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketClientDemo {
    public static void main(String[] args) throws Exception {
        try (Socket s = new Socket("localhost", 9090);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            System.out.println("Connected to CrimeWatch Socket Server. Listening for alerts...");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("ALERT >> " + line);
            }
        }
    }
}
