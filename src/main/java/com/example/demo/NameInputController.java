package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

public class NameInputController {

    @FXML
    private TextField nameField;
    private GroupChatService groupChatService;
    private Stage stage;
    private ClientJavaFX clientApp;

    public void setClientApp(ClientJavaFX clientApp) {
        this.clientApp = clientApp;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        try {
            String serviceName = "GroupChatService";
            groupChatService = (GroupChatService) Naming.lookup("//localhost/" + serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void submitName() {
        String name = nameField.getText();
        if (!name.isEmpty()) {
            try {
                groupChatService.addMember(name);stage.close();
                clientApp.showClientInterface(name);
                List<String> members = groupChatService.getAllRegisteredMembers();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
