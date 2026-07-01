package com.restaurantPos.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class NavigationService {

    private final ApplicationContext context;
    public NavigationService(ApplicationContext context) {
        this.context = context;
    }

    public void changeScene(Stage stage, String fxmlPath) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.centerOnScreen();

    }

}
