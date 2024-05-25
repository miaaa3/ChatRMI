package com.example.demo;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupDialogController {

    @FXML
    private TextField groupNameField;

    @FXML
    private ListView<String> membersListView;

    private List<String> allMembers;
    private GroupChatService groupChatService;

    public void initialize() {
        try {
            String serviceName = "GroupChatService";
            groupChatService = (GroupChatService) Naming.lookup("//localhost/" + serviceName);
            allMembers = groupChatService.getAllRegisteredMembers();
            membersListView.getItems().setAll(allMembers);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void saveGroup() {
        try {
            String groupName = groupNameField.getText();
            ObservableList<String> selectedMembers = membersListView.getSelectionModel().getSelectedItems(); // Get selected members
           List<String> selectedMembersList = new ArrayList<>(selectedMembers);

            groupChatService.createGroup(groupName, selectedMembersList);

            Stage stage = (Stage) groupNameField.getScene().getWindow();
            stage.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGroupChatService(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }


}
