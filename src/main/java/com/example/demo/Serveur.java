package com.example.demo;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Serveur {
    public static void main(String[] args) {
        try {
            GroupChatService chatService = new GroupChatServiceImpl();

            LocateRegistry.createRegistry(1099);

            Naming.rebind("//localhost/GroupChatService", chatService);
            System.out.println("Server ready for chat");
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
