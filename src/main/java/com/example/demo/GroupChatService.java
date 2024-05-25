package com.example.demo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GroupChatService extends Remote {
    void joinGroup(String groupName, String username) throws RemoteException;
    void leaveGroup(String groupName, String username) throws RemoteException;
    void createGroup(String groupName, List<String> selectedMembers) throws RemoteException;
    void deleteGroup(String groupName) throws RemoteException;
    List<String> getGroupList() throws RemoteException;
    List<String> getGroupMembers(String groupName) throws RemoteException;
    void addMemberToGroup(String groupName, String memberName) throws RemoteException;
    void sendMessage(String groupName, String message, String username) throws RemoteException;
    List<String> getMessages(String groupName) throws RemoteException;
    List<String> getAllRegisteredMembers() throws RemoteException;
    void addMember(String username) throws RemoteException; // New method for adding a member
    String getUsername() throws RemoteException;

}
