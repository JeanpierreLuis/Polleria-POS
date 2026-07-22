package com.restaurantPos.application;

import com.restaurantPos.RestaurantPosApplication;
import com.restaurantPos.config.SpringContext;
import com.restaurantPos.utils.Paths;
import com.restaurantPos.utils.PathsCss;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class JavaFxApplication extends Application{
    private ConfigurableApplicationContext context;

    @Override
    public void init(){
        context = new SpringApplicationBuilder(RestaurantPosApplication.class).run();
        SpringContext.setContext(context);
    }

    @Override
    public void start (Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.ORDERS_PAGE));

        loader.setControllerFactory(context::getBean);
        Parent pane = loader.load();
        Scene scene = new Scene(pane, 900, 600);

        scene.getStylesheets().add(
                getClass().getResource(PathsCss.CSS_ORDER).toExternalForm()
        );
        stage.setTitle("Restaurant Brasero");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop(){
        context.close();
    }

    public static void main(String[] args){launch(args);
    }

}
