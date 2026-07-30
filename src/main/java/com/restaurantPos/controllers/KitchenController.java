package com.restaurantPos.controllers;

import com.restaurantPos.models.DetallePedido;
import com.restaurantPos.models.Pedidos;
import com.restaurantPos.repositories.PedidosRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;

@RequiredArgsConstructor
@Component
public class KitchenController {
    private final PedidosRepository pedidosRepository;

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
    private TableView<DetallePedido> tblDetalleCocina;

    @FXML
    private TableColumn<DetallePedido, Integer> colDetalleCantidad;

    @FXML
    private TableColumn<DetallePedido, String> colDetalleProducto;


    @FXML
    public void initialize() {

        configurarTablaDetalle();
        cargarPedidosPendientes();
    }

    private void cargarPedidosPendientes(){
        pedidosContainer.getChildren().clear();

        List<Pedidos> pedidos = pedidosRepository.findByEstadoConDetalles(Pedidos.EstadoPedido.PENDIENTE);

        for (Pedidos pedido : pedidos) {
            VBox card =  new VBox(5);
            card.getStyleClass().add("pedido-card");
            card.setPrefWidth(220);

            Label lblTitulo = new Label("Pedido #" + pedido.getId_pedido());
            Label lblMesa = new Label("Mesa: " + pedido.getMesa().getNumero());

            card.getChildren().addAll(lblTitulo, lblMesa);

            for(DetallePedido detalle : pedido.getDetalles()){
                Label lblproducto = new Label("x " + detalle.getCantidad() + " " +  detalle.getProducto().getNombre());

                card.getChildren().add(lblproducto);
            }
            Label lblTotal = new Label("Total: S/ " + pedido.getTotal());

            card.getChildren().add(lblTotal);

            card.setOnMouseClicked(e -> mostrarPedido(pedido));

            pedidosContainer.getChildren().add(card);

        }
    }
    private void mostrarPedido(Pedidos pedido){
        cargarCabeceraPedido(pedido);
        cargarDetallePedido(pedido);
    }

    private void cargarCabeceraPedido(Pedidos pedido){
        lblPedidoSeleccionado.setText("Pedido #" + pedido.getId_pedido());
        lblEstadoPedido.setText(pedido.getEstado().name());
        lblMesaPedido.setText("Mesa " + pedido.getMesa().getNumero());
    }

    public void configurarTablaDetalle(){
        colDetalleCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDetalleProducto.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProducto().getNombre()));
    }

    private void cargarDetallePedido(Pedidos pedido){
        tblDetalleCocina.getItems().clear();
        tblDetalleCocina.getItems().addAll(pedido.getDetalles());
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
