package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientController {
    @FXML
    private TextField usernameField;
    @FXML
    private ListView<String> groupMembersListView;
    @FXML
    private ListView<HBox> chatListView;  // Use ListView to display chat messages
    @FXML
    private TextField messageField;
    @FXML
    private ListView<String> groupListView;
    @FXML
    private ListView<String> membersListView;
    @FXML
    private TextField groupNameField;
    @FXML
    private Label welcomeLabel;

    private GroupChatService groupChatService;
    private String username;
    private String currentGroup;

    private Stage stage;

    private Map<String, List<HBox>> groupMediaMap = new HashMap<>();


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        try {
            String serviceName = "GroupChatService";
            groupChatService = (GroupChatService) Naming.lookup("//localhost/" + serviceName);
            updateGroupList();
            username = groupChatService.getUsername();
            groupListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    currentGroup = newValue;
                    updateGroupMembersList();
                    try {
                        updateChatArea();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    @FXML
    private void createGroup() {
        try {
            FXMLLoader loader = new FXMLLoader(ClientJavaFX.class.getResource("create_group_dialog.fxml"));
            Parent root = loader.load();

            CreateGroupDialogController controller = loader.getController();
            controller.setGroupChatService(groupChatService);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(groupListView.getScene().getWindow());
            dialogStage.setTitle("Create New Group");
            dialogStage.setScene(new Scene(root));

            dialogStage.show();

            updateGroupList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteGroup() {
        try {
            String groupName = groupListView.getSelectionModel().getSelectedItem();
            if (groupName != null) {
                groupChatService.deleteGroup(groupName);
                updateGroupList();
                updateGroupMembersList();
                updateChatArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectGroup() throws RemoteException {
        currentGroup = groupListView.getSelectionModel().getSelectedItem();
        updateGroupMembersList();
        updateChatArea();
    }

    @FXML
    private void joinGroup() {
        try {
            String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null && !selectedGroup.isEmpty()) {
                groupChatService.joinGroup(selectedGroup, username);
                currentGroup = selectedGroup;
                updateGroupList();
                updateGroupMembersList();
                updateChatArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void leaveGroup() {
        try {
            groupChatService.leaveGroup(currentGroup, username);
            updateGroupMembersList();
            updateChatArea();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        try {
            String message = messageField.getText();
            groupChatService.sendMessage(currentGroup, message, username);
            messageField.clear();
            updateChatArea();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGroupList() {
        try {
            List<String> groups = groupChatService.getGroupList();
            groupListView.getItems().setAll(groups);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void updateGroupMembersList() {
        try {
            List<String> members = groupChatService.getGroupMembers(currentGroup);
            groupMembersListView.getItems().setAll(members);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateRegisteredMembers() {
        try {
            List<String> members = groupChatService.getAllRegisteredMembers();
            membersListView.getItems().setAll(members);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void sendMedia() throws RemoteException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Media File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi"),
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            if (isImageFile(selectedFile)) {
                sendImage(selectedFile);
            } else {
                sendNonImageFile(selectedFile);
            }
        }
    }

    private boolean isImageFile(File file) {
        String[] imageExtensions = {".png", ".jpg", ".gif"};
        for (String extension : imageExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private void sendImage(File imageFile) throws RemoteException {
        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        HBox imageBox = new HBox();
        imageBox.getChildren().addAll(new Label("You sent an image:"), imageView);

        if (!groupMediaMap.containsKey(currentGroup)) {
            groupMediaMap.put(currentGroup, new ArrayList<>());
        }
        groupMediaMap.get(currentGroup).add(imageBox);

        updateChatArea();
    }

    private void sendNonImageFile(File file) throws RemoteException {
        HBox fileBox = new HBox();
        fileBox.getChildren().add(new Label("You sent a file: " + file.getName()));

        if (!groupMediaMap.containsKey(currentGroup)) {
            groupMediaMap.put(currentGroup, new ArrayList<>());
        }
        groupMediaMap.get(currentGroup).add(fileBox);

        updateChatArea();
    }

    private void updateChatArea() throws RemoteException {
        chatListView.getItems().clear();

        List<String> messages = groupChatService.getMessages(currentGroup);
        for (String message : messages) {
            HBox messageBox = new HBox();
            Label messageLabel = new Label(message);
            messageBox.getChildren().add(messageLabel);
            chatListView.getItems().add(messageBox);
        }

        if (groupMediaMap.containsKey(currentGroup)) {
            List<HBox> mediaList = groupMediaMap.get(currentGroup);
            for (HBox mediaBox : mediaList) {
                chatListView.getItems().add(mediaBox);
            }
        }
    }
}
