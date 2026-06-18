package com.restaurantPos.application;

import com.restaurantPos.utils.Paths;
import com.restaurantPos.utils.PathsCss;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxApplication extends Application{

    @Override
    public void start (Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.LOGIN_PAGE));
        Parent pane = loader.load();
        Scene scene = new Scene(pane, 900, 600);

        scene.getStylesheets().add(
                getClass().getResource(PathsCss.CSS_LOGIN).toExternalForm()
        );
        stage.setTitle("Restaurant Brasero");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

}
