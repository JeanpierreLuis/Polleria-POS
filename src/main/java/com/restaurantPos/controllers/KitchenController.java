package com.restaurantPos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import org.springframework.stereotype.Component;

@Component
public class KitchenController {

    @FXML
    private FlowPane pedidosContainer;

    @FXML
    private Label lblPedidoSeleccionado;

    @FXML
    private Label lblEstadoPedido;

    @FXML
    private Label lblMesaPedido;

    @FXML
    private Label lblTiempoPedido;

    @FXML
    private TableView<Object> tblDetalleCocina;

    @FXML
    private TableColumn<Object, String> colCantidad;

    @FXML
    private TableColumn<Object, String> colProducto;

    @FXML
    private TableColumn<Object, String> colNotas;

    @FXML
    public void initialize() {
        lblPedidoSeleccionado.setText("Pedido #1024");
        lblEstadoPedido.setText("Pendiente");
        lblMesaPedido.setText("Mesa 07");
        lblTiempoPedido.setText("Hace 8 min");
    }

    @FXML
    void btnActualizarPedidos(ActionEvent event) {
        // Pendiente: recargar pedidos desde PedidoService cuando exista estado de cocina.
    }

    @FXML
    void btnVerPedido(ActionEvent event) {
        lblPedidoSeleccionado.setText("Pedido seleccionado");
    }

    @FXML
    void btnMarcarRecibido(ActionEvent event) {
        lblEstadoPedido.setText("Recibido");
    }

    @FXML
    void btnIniciarPreparacion(ActionEvent event) {
        lblEstadoPedido.setText("En preparacion");
    }

    @FXML
    void btnMarcarListo(ActionEvent event) {
        lblEstadoPedido.setText("Listo para entregar");
    }
}
