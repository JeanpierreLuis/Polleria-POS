package com.restaurantPos.controllers;

import com.restaurantPos.models.DetallePedido;
import com.restaurantPos.models.Mesas;
import com.restaurantPos.models.Productos;
import com.restaurantPos.services.MesasService;
import com.restaurantPos.services.ProductosService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class OrdersController {

    // ==========================
    // SERVICES
    // ==========================

    private final MesasService mesasService;
    private final ProductosService productosService;

    // ==========================
    // VARIABLES
    // ==========================

    private ObservableList<DetallePedido> detallePedido;

    private ObservableList<Productos> productos;

    // ==========================
    // COMPONENTES FXML
    // ==========================

    @FXML
    private ComboBox<Mesas> cbMesa;

    @FXML
    private ComboBox<String> cbTipoPedido;

    @FXML
    private TextField txtBuscarProducto;

    @FXML
    private TextField txtCliente;

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnLimpiarPedido;

    @FXML
    private Button btnQuitarProducto;

    @FXML
    private Button btnRegistrarPedido;

    // Tabla Productos

    @FXML
    private TableView<Productos> tblProductos;

    @FXML
    private TableColumn<Productos, String> colProductoNombre;

    @FXML
    private TableColumn<Productos, BigDecimal> colProductoPrecio;

    // Tabla Detalle Pedido

    @FXML
    private TableView<DetallePedido> tblDetalle;

    @FXML
    private TableColumn<DetallePedido, String> colDetalleProducto;

    @FXML
    private TableColumn<DetallePedido, Integer> colDetalleCantidad;

    @FXML
    private TableColumn<DetallePedido, Double> colDetallePrecio;

    @FXML
    private TableColumn<DetallePedido, Double> colDetalleSubtotal;

    // ==========================
    // INITIALIZE
    // ==========================

    @FXML
    public void initialize() {

        cbTipoPedido.getItems().addAll("Mesa", "Llevar");
        cbMesa.getItems().addAll(mesasService.listarMesas());

        configurarTablaProductos();
        configurarTablaDetallePedido();

        cargarProductos();
        configurarBusqueda();
    }

    // ==========================
    // CONFIGURACIÓN DE TABLAS
    // ==========================

    private void configurarTablaProductos() {

        colProductoNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colProductoPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio"));
    }

    private void configurarTablaDetallePedido() {

        colDetalleProducto.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getProducto()
                                .getNombre()
                ));

        colDetalleCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad"));

        colDetallePrecio.setCellValueFactory(
                new PropertyValueFactory<>("preciounitario"));

        colDetalleSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subtotal"));
    }

    // ==========================
    // CARGA DE DATOS
    // ==========================

    private void cargarProductos() {

        productos = FXCollections.observableArrayList(productosService.listarProductos());

        detallePedido = FXCollections.observableArrayList();

        tblDetalle.setItems(detallePedido);
    }

    private void configurarBusqueda() {

        FilteredList<Productos> filtroProducto =
                new FilteredList<>(productos, p -> true);

        txtBuscarProducto.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    filtroProducto.setPredicate(producto -> {

                        if (newValue == null || newValue.isBlank()) {
                            return true;
                        }

                        String texto = newValue.toLowerCase();

                        return producto.getNombre()
                                .toLowerCase()
                                .contains(texto);
                    });
                });

        tblProductos.setItems(filtroProducto);
    }

    // ==========================
    // EVENTOS
    // ==========================

    @FXML
    void btnAgregarProducto(ActionEvent event) {

        Productos productoSeleccionado =
                tblProductos.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            return;
        }

        for (DetallePedido detalle : detallePedido) {

            if (detalle.getProducto().getId_producto()
                    == productoSeleccionado.getId_producto()) {

                detalle.setCantidad(detalle.getCantidad() + 1);

                detalle.setSubtotal(
                        detalle.getCantidad()
                                * detalle.getPreciounitario()
                );

                tblDetalle.refresh();
                calcularTotal();

                return;
            }
        }

        double precioProducto =
                productoSeleccionado.getPrecio().doubleValue();

        DetallePedido nuevoDetalle = DetallePedido.builder()
                .producto(productoSeleccionado)
                .cantidad(1)
                .preciounitario(precioProducto)
                .subtotal(precioProducto)
                .build();

        detallePedido.add(nuevoDetalle);

        calcularTotal();
    }

    @FXML
    void btnBuscarProducto(ActionEvent event) {

    }

    @FXML
    void btnLimpiarPedido(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea limpiar este pedido?");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            detallePedido.clear();
            cbMesa.getSelectionModel().clearSelection();
            cbTipoPedido.getSelectionModel().clearSelection();
            lblTotal.setText("S/ 0.00");
        }

    }

    @FXML
    void btnQuitarProducto(ActionEvent event) {
        DetallePedido detalleSeleccionado = tblDetalle.getSelectionModel().getSelectedItem();

        if (detalleSeleccionado == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione un producto del detalle.");
            alert.showAndWait();

            return;
        }

        if (detalleSeleccionado == null) {
            return;
        }
        if (detalleSeleccionado.getCantidad() > 1){
            detalleSeleccionado.setCantidad(detalleSeleccionado.getCantidad() - 1);

          detalleSeleccionado.setSubtotal(
                  detalleSeleccionado.getCantidad() * detalleSeleccionado.getPreciounitario());

            tblDetalle.refresh();
        }else{
            detallePedido.remove(detalleSeleccionado);
        }

        calcularTotal();
    }

    @FXML
    void btnRegistrarPedido(ActionEvent event) {

    }

    // ==========================
    // MÉTODOS AUXILIARES
    // ==========================

    private void calcularTotal() {

        double total = detallePedido.stream()
                .mapToDouble(detalle ->
                        detalle.getSubtotal() != null
                                ? detalle.getSubtotal()
                                : 0.0)
                .sum();

        BigDecimal totalRedondeado = BigDecimal.valueOf(total)
                .setScale(2, RoundingMode.HALF_UP);

        lblTotal.setText("S/ " + totalRedondeado);
    }

}