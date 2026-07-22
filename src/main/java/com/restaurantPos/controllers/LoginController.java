package com.restaurantPos.controllers;
import com.restaurantPos.services.NavigationService;
import com.restaurantPos.services.UsuarioService;
import com.restaurantPos.utils.Paths;
import com.restaurantPos.utils.PathsCss;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginController {
    private final UsuarioService usuarioService;
    private final NavigationService navigationService;


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
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            navigationService.changeScene(stage, Paths.DASHBOARD_PAGE, PathsCss.CSS_DASHBOARD);



        }catch(Exception e){
            e.printStackTrace();
            lblMensaje.setText("Error al abrir dashboard");
        }

    }else{
        lblMensaje.setText("Usuario o contraseña incorrectos");
        }
    }

}
