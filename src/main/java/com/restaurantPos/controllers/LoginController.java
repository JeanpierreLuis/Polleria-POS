package com.restaurantPos.controllers;
import com.restaurantPos.services.UsuarioService;
import com.restaurantPos.utils.Paths;
import com.restaurantPos.utils.PathsCss;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import com.restaurantPos.config.SpringContext;



@Component
public class LoginController {
    private final UsuarioService usuarioService;
    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    @FXML
    private Button btnLogin;

    @FXML
    private Label lblMensaje;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsuario;

    @FXML
    void btnIngresarSistema(ActionEvent event) {
    String username = txtUsuario.getText().trim();
    String password = txtPassword.getText();

    if(username.isEmpty()|| password.isEmpty()){
        lblMensaje.setText("Por favor ingrese un nombre");
        return;
    }

    boolean autenticado = usuarioService.login(username, password);

    if (autenticado) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.DASHBOARD_PAGE));

            loader.setControllerFactory(SpringContext.getContext()::getBean);

            Parent pane = loader.load();

            Scene scene = new Scene(pane, 900,  600);

            scene.getStylesheets().add(getClass().getResource(PathsCss.CSS_DASHBOARD).toExternalForm());

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

             stage.setScene(scene);
             stage.centerOnScreen();

        }catch(Exception e){
            e.printStackTrace();
            lblMensaje.setText("Error al abrir dashboard");
        }

    }else{
        lblMensaje.setText("Usuario o contraseña incorrectos");
        }
    }

}
