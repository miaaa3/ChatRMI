package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientJavaFX extends Application {

    private static String username;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        showNameInputDialog(primaryStage);
    }

    private void showNameInputDialog(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientJavaFX.class.getResource("name_input.fxml"));
        Parent root = loader.load();
        NameInputController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setClientApp(this);

        primaryStage.setTitle("Enter Your Name");
        primaryStage.setScene(new Scene(root, 400, 200));
        primaryStage.show();
    }

    public void showClientInterface(String name) {
        username = name;
        try {
            FXMLLoader loader = new FXMLLoader(ClientJavaFX.class.getResource("client_interface.fxml"));
            Parent root = loader.load();
            ClientController controller = loader.getController();
            controller.setUsername(name);

            Stage stage = new Stage();
            stage.setTitle("Client Interface");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

