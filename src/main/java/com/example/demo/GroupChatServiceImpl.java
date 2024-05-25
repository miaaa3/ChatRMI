package com.example.demo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatServiceImpl extends UnicastRemoteObject implements GroupChatService {
    private Map<String, List<String>> groups = new HashMap<>();
    private Map<String, List<String>> groupMessages = new HashMap<>();
    private List<String> allRegisteredMembers = new ArrayList<>();
    private String username;

    protected GroupChatServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void joinGroup(String groupName, String username) throws RemoteException {
        groups.computeIfAbsent(groupName, k -> new ArrayList<>()).add(username);
    }

    @Override
    public synchronized void leaveGroup(String groupName, String username) throws RemoteException {
        List<String> groupMembers = groups.get(groupName);
        if (groupMembers != null) {
            groupMembers.remove(username);
        }
    }

    @Override
    public synchronized void createGroup(String groupName, List<String> selectedMembers) throws RemoteException {
        groups.putIfAbsent(groupName, new ArrayList<>());
        List<String> groupMembers = groups.get(groupName);
        groupMembers.addAll(selectedMembers);
        groups.put(groupName, groupMembers);
    }

    @Override
    public synchronized void deleteGroup(String groupName) throws RemoteException {
        groups.remove(groupName);
    }

    @Override
    public synchronized List<String> getGroupList() throws RemoteException {
        return new ArrayList<>(groups.keySet());
    }

    @Override
    public synchronized List<String> getGroupMembers(String groupName) throws RemoteException {
        return groups.getOrDefault(groupName, new ArrayList<>());
    }

    @Override
    public void addMemberToGroup(String groupName, String memberName) throws RemoteException {
        if (!groups.containsKey(groupName)) {
            throw new RemoteException("Group does not exist.");
        }
        groups.get(groupName).add(memberName);

    }

    @Override
    public synchronized void sendMessage(String groupName, String message, String username) throws RemoteException {
        groupMessages.computeIfAbsent(groupName, k -> new ArrayList<>()).add(username + ": " + message);
    }

    @Override
    public synchronized List<String> getMessages(String groupName) throws RemoteException {
        return new ArrayList<>(groupMessages.getOrDefault(groupName, new ArrayList<>()));
    }

    @Override
    public synchronized List<String> getAllRegisteredMembers() throws RemoteException {
        return new ArrayList<>(allRegisteredMembers);
    }

    @Override
    public void addMember(String username) throws RemoteException {
        try {
            if (allRegisteredMembers != null) {
                this.username=username;
                allRegisteredMembers.add(username);
            } else {
                throw new RemoteException("allRegisteredMembers is not initialized");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new RemoteException("allRegisteredMembers is null", e);
        }
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

}
