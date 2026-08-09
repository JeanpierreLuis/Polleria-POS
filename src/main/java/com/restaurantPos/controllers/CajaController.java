package com.restaurantPos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class CajaController {

    @FXML
    private TableView<?> tblMovimientos;

    @FXML
    private TableColumn<?, ?> colDescripcion;

    @FXML
    private TableColumn<?, ?> colEstado;

    @FXML
    private TableColumn<?, ?> colHora;

    @FXML
    private TableColumn<?, ?> colMonto;

    @FXML
    private TableColumn<?, ?> colTipo;

    @FXML
    private TableColumn<?, ?> colUsuario;

    @FXML
    private DatePicker dpFechaMovimiento;

    @FXML
    private Label lblBilletes;

    @FXML
    private Label lblDiferencia;

    @FXML
    private Label lblEfectivoContado;

    @FXML
    private Label lblEgresos;

    @FXML
    private Label lblEstadoCaja;

    @FXML
    private Label lblIngresos;

    @FXML
    private Label lblMonedas;

    @FXML
    private Label lblSaldoEsperado;

    @FXML
    private Label lblSaldoInicial;

    @FXML
    private Label lblTurnoActual;

    @FXML
    private ComboBox<?> cbFiltroTipo;

    @FXML
    private ComboBox<?> cbTipoMovimiento;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private TextField txtMonto;

    @FXML
    void btnAbrirCaja(ActionEvent event) {

    }

    @FXML
    void btnActualizar(ActionEvent event) {

    }

    @FXML
    void btnCerrarCaja(ActionEvent event) {

    }

    @FXML
    void btnRegistrarEgreso(ActionEvent event) {

    }

    @FXML
    void btnRegistrarIngreso(ActionEvent event) {

    }

}

