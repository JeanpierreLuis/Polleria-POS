package com.restaurantPos.controllers;

import com.restaurantPos.models.Caja;
import com.restaurantPos.models.DetallePedido;
import com.restaurantPos.models.Pedidos;
import com.restaurantPos.models.Usuarios;
import com.restaurantPos.models.Ventas;
import com.restaurantPos.repositories.CajaRepository;
import com.restaurantPos.repositories.PedidosRepository;
import com.restaurantPos.repositories.VentasRepository;
import com.restaurantPos.utils.Session;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CajaController {

    private static final BigDecimal IGV_RATE = BigDecimal.valueOf(0.18);
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TICKET_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PedidosRepository pedidosRepository;
    private final VentasRepository ventasRepository;
    private final CajaRepository cajaRepository;

    private final ObservableList<Pedidos> pedidosListos = FXCollections.observableArrayList();
    private final ObservableList<DetallePedido> detallePedido = FXCollections.observableArrayList();

    private FilteredList<Pedidos> pedidosFiltrados;
    private Pedidos pedidoSeleccionado;
    private Ventas ventaRegistrada;
    private Caja cajaActiva;

    @FXML
    private TableView<DetallePedido> tblDetalleBoleta;

    @FXML
    private TableView<Pedidos> tblPedidosCaja;

    @FXML
    private Button btnActualizarPedidos;

    @FXML
    private Button btnGenerarBoleta;

    @FXML
    private Button btnRegistrarVenta;

    @FXML
    private Button btnVistaPreviaBoleta;

    @FXML
    private ComboBox<Ventas.MetodoPago> cbMetodoPago;

    @FXML
    private TableColumn<DetallePedido, Integer> colBoletaCantidad;

    @FXML
    private TableColumn<DetallePedido, Double> colBoletaPrecio;

    @FXML
    private TableColumn<DetallePedido, String> colBoletaProducto;

    @FXML
    private TableColumn<DetallePedido, Double> colBoletaSubtotal;

    @FXML
    private TableColumn<Pedidos, String> colPedidoCliente;

    @FXML
    private TableColumn<Pedidos, String> colPedidoEstado;

    @FXML
    private TableColumn<Pedidos, String> colPedidoHora;

    @FXML
    private TableColumn<Pedidos, String> colPedidoMesa;

    @FXML
    private TableColumn<Pedidos, Long> colPedidoNumero;

    @FXML
    private TableColumn<Pedidos, String> colPedidoTipo;

    @FXML
    private TableColumn<Pedidos, BigDecimal> colPedidoTotal;

    @FXML
    private Label lblBoletaCajero;

    @FXML
    private Label lblBoletaCliente;

    @FXML
    private Label lblBoletaEstadoVenta;

    @FXML
    private Label lblBoletaIgv;

    @FXML
    private Label lblBoletaMesa;

    @FXML
    private Label lblBoletaPedido;

    @FXML
    private Label lblBoletaSubtotal;

    @FXML
    private Label lblBoletaTotal;

    @FXML
    private Label lblBoletaVenta;

    @FXML
    private Label lblCajaActiva;

    @FXML
    private Label lblEstadoCajaBoleta;

    @FXML
    private Label lblMetodoPagoResumen;

    @FXML
    private Label lblPedidoSeleccionado;

    @FXML
    private Label lblPedidosPendientes;

    @FXML
    private Label lblTotalCobroResumen;

    @FXML
    private Label lblTurnoBoleta;

    @FXML
    private TextArea txtTicketPreview;

    @FXML
    private TextField txtBuscarPedido;

    @FXML
    public void initialize() {
        if (!esVistaCaja()) {
            return;
        }

        cbMetodoPago.setItems(FXCollections.observableArrayList(Ventas.MetodoPago.values()));
        cbMetodoPago.valueProperty().addListener((observable, oldValue, newValue) ->
                lblMetodoPagoResumen.setText(newValue == null ? "-" : formatearMetodoPago(newValue)));

        configurarTablaPedidos();
        configurarTablaDetalle();
        configurarBusqueda();
        cargarPedidosListos();
        limpiarSeleccion();
    }

    private void configurarTablaPedidos() {
        colPedidoNumero.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId_pedido()));
        colPedidoHora.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearHora(cellData.getValue().getFecha())));
        colPedidoCliente.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerCliente(cellData.getValue())));
        colPedidoMesa.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerMesa(cellData.getValue())));
        colPedidoTipo.setCellValueFactory(new PropertyValueFactory<>("tipoPedido"));
        colPedidoEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado().name()));
        colPedidoTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblPedidosCaja.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, pedido) -> seleccionarPedido(pedido));
    }

    private void configurarTablaDetalle() {
        colBoletaProducto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProducto().getNombre()));
        colBoletaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colBoletaPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colBoletaSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tblDetalleBoleta.setItems(detallePedido);
    }

    private void configurarBusqueda() {
        pedidosFiltrados = new FilteredList<>(pedidosListos, pedido -> true);
        txtBuscarPedido.textProperty().addListener((observable, oldValue, texto) ->
                pedidosFiltrados.setPredicate(pedido -> coincideBusqueda(pedido, texto)));
        tblPedidosCaja.setItems(pedidosFiltrados);
    }


    private void cargarPedidosListos() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);

        List<Pedidos> pedidos = pedidosRepository.findByEstadoConDetalles(
                List.of(Pedidos.EstadoPedido.LISTO), inicioDia, finDia);

        pedidosListos.setAll(pedidos.stream()
                .filter(pedido -> !ventasRepository.existsByPedidoAndEstadopago(pedido, Ventas.EstadoPago.PAGADO))
                .toList());

        lblPedidosPendientes.setText(String.valueOf(pedidosListos.size()));
        tblPedidosCaja.refresh();
    }

    private void seleccionarPedido(Pedidos pedido) {
        pedidoSeleccionado = pedido;
        ventaRegistrada = null;
        txtTicketPreview.clear();
        btnGenerarBoleta.setDisable(true);

        if (pedido == null) {
            limpiarSeleccion();
            return;
        }

        detallePedido.setAll(pedido.getDetalles());
        lblPedidoSeleccionado.setText("#" + pedido.getId_pedido());
        lblBoletaPedido.setText("#" + pedido.getId_pedido());
        lblBoletaCliente.setText(obtenerCliente(pedido));
        lblBoletaMesa.setText(obtenerMesa(pedido));
        lblBoletaCajero.setText(obtenerCajero());
        lblBoletaVenta.setText("Pendiente");
        lblBoletaEstadoVenta.setText("Sin registrar");
        actualizarTotales(pedido.getTotal());
    }

    @FXML
    void btnActualizarPedidos(ActionEvent event) {
        cargarPedidosListos();
        limpiarSeleccion();
    }

    @FXML
    void btnRegistrarVenta(ActionEvent event) {
        if (pedidoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un pedido listo para cobrar.");
            return;
        }


        Ventas.MetodoPago metodoPago = cbMetodoPago.getSelectionModel().getSelectedItem();
        if (metodoPago == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un metodo de pago.");
            return;
        }

        if (ventasRepository.existsByPedidoAndEstadopago(pedidoSeleccionado, Ventas.EstadoPago.PAGADO)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Este pedido ya tiene una venta pagada registrada.");
            cargarPedidosListos();
            limpiarSeleccion();
            return;
        }

        ventaRegistrada = Ventas.builder()
                .pedido(pedidoSeleccionado)
                .caja(cajaActiva)
                .usuario(Session.getUsuarioActual())
                .total(normalizarMonto(pedidoSeleccionado.getTotal()))
                .fecha(LocalDateTime.now())
                .estadopago(Ventas.EstadoPago.PAGADO)
                .metodoPago(metodoPago)
                .build();

        ventaRegistrada = ventasRepository.save(ventaRegistrada);
        lblBoletaVenta.setText("#" + ventaRegistrada.getId_venta());
        lblBoletaEstadoVenta.setText(ventaRegistrada.getEstadopago().name());
        btnGenerarBoleta.setDisable(false);

        cargarPedidosListos();
        generarTicketPreview();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Venta registrada correctamente.");
    }

    @FXML
    void btnVistaPreviaBoleta(ActionEvent event) {
        if (pedidoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un pedido para generar la vista previa.");
            return;
        }

        generarTicketPreview();
    }

    @FXML
    void btnGenerarBoleta(ActionEvent event) {
        if (ventaRegistrada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Registre la venta antes de generar el ticket.");
            return;
        }

        generarTicketPreview();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Ticket generado para la venta #" + ventaRegistrada.getId_venta() + ".");
    }

    @FXML
    void btnAbrirCaja(ActionEvent event) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "La apertura de caja se gestiona desde el modulo de movimientos.");
    }

    @FXML
    void btnActualizar(ActionEvent event) {
        if (esVistaCaja()) {
            cargarPedidosListos();
        }
    }

    @FXML
    void btnCerrarCaja(ActionEvent event) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "El cierre de caja se gestiona desde el modulo de movimientos.");
    }

    @FXML
    void btnRegistrarEgreso(ActionEvent event) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Los egresos se registran desde el modulo de movimientos.");
    }

    @FXML
    void btnRegistrarIngreso(ActionEvent event) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Los ingresos manuales se registran desde el modulo de movimientos.");
    }

    private void limpiarSeleccion() {
        pedidoSeleccionado = null;
        ventaRegistrada = null;
        detallePedido.clear();
        cbMetodoPago.getSelectionModel().clearSelection();
        tblPedidosCaja.getSelectionModel().clearSelection();
        lblPedidoSeleccionado.setText("-");
        lblMetodoPagoResumen.setText("-");
        lblTotalCobroResumen.setText("S/ 0.00");
        lblBoletaVenta.setText("-");
        lblBoletaPedido.setText("-");
        lblBoletaCliente.setText("-");
        lblBoletaMesa.setText("-");
        lblBoletaCajero.setText(obtenerCajero());
        lblBoletaEstadoVenta.setText("-");
        lblBoletaSubtotal.setText("S/ 0.00");
        lblBoletaIgv.setText("S/ 0.00");
        lblBoletaTotal.setText("S/ 0.00");
        txtTicketPreview.clear();
        btnGenerarBoleta.setDisable(true);
    }

    private boolean esVistaCaja() {
        return tblPedidosCaja != null && cbMetodoPago != null;
    }

    private void actualizarTotales(BigDecimal totalPedido) {
        BigDecimal total = normalizarMonto(totalPedido);
        BigDecimal subtotal = total.divide(BigDecimal.ONE.add(IGV_RATE), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);

        lblTotalCobroResumen.setText(formatearMoneda(total));
        lblBoletaSubtotal.setText(formatearMoneda(subtotal));
        lblBoletaIgv.setText(formatearMoneda(igv));
        lblBoletaTotal.setText(formatearMoneda(total));
    }

    private void generarTicketPreview() {
        if (pedidoSeleccionado == null) {
            txtTicketPreview.clear();
            return;
        }

        StringBuilder ticket = new StringBuilder();
        ticket.append("RESTAURANT POS\n");
        ticket.append("Ticket de venta\n");
        ticket.append("------------------------------\n");
        ticket.append("Venta: ").append(ventaRegistrada == null ? "Pendiente" : "#" + ventaRegistrada.getId_venta()).append('\n');
        ticket.append("Pedido: #").append(pedidoSeleccionado.getId_pedido()).append('\n');
        ticket.append("Fecha: ").append(LocalDateTime.now().format(TICKET_FORMATTER)).append('\n');
        ticket.append("Caja: ").append(cajaActiva == null ? "-" : "#" + cajaActiva.getId()).append('\n');
        ticket.append("Cajero: ").append(obtenerCajero()).append('\n');
        ticket.append("Cliente: ").append(obtenerCliente(pedidoSeleccionado)).append('\n');
        ticket.append("Mesa: ").append(obtenerMesa(pedidoSeleccionado)).append('\n');
        ticket.append("Pago: ").append(cbMetodoPago.getValue() == null ? "-" : formatearMetodoPago(cbMetodoPago.getValue())).append('\n');
        ticket.append("------------------------------\n");

        for (DetallePedido detalle : detallePedido) {
            ticket.append(detalle.getCantidad())
                    .append(" x ")
                    .append(detalle.getProducto().getNombre())
                    .append("  ")
                    .append(formatearMoneda(BigDecimal.valueOf(detalle.getSubtotal())))
                    .append('\n');
        }

        ticket.append("------------------------------\n");
        ticket.append("Subtotal: ").append(lblBoletaSubtotal.getText()).append('\n');
        ticket.append("IGV: ").append(lblBoletaIgv.getText()).append('\n');
        ticket.append("Total: ").append(lblBoletaTotal.getText()).append('\n');

        txtTicketPreview.setText(ticket.toString());
    }

    private boolean coincideBusqueda(Pedidos pedido, String texto) {
        if (texto == null || texto.isBlank()) {
            return true;
        }

        String busqueda = texto.toLowerCase();
        return String.valueOf(pedido.getId_pedido()).contains(busqueda)
                || obtenerCliente(pedido).toLowerCase().contains(busqueda)
                || obtenerMesa(pedido).toLowerCase().contains(busqueda)
                || obtenerTipoPedido(pedido).toLowerCase().contains(busqueda);
    }

    private String obtenerCliente(Pedidos pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getNombre() == null) {
            return "Cliente general";
        }
        return pedido.getCliente().getNombre();
    }

    private String obtenerMesa(Pedidos pedido) {
        if (pedido.getMesa() == null || pedido.getMesa().getNumero() == null) {
            return "-";
        }
        return "Mesa " + pedido.getMesa().getNumero();
    }

    private String obtenerTipoPedido(Pedidos pedido) {
        if (pedido.getTipoPedido() == null || pedido.getTipoPedido().isBlank()) {
            return "-";
        }
        return pedido.getTipoPedido();
    }

    private String obtenerCajero() {
        Usuarios usuario = Session.getUsuarioActual();
        if (usuario == null || usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            return "Caja";
        }
        return usuario.getNombre();
    }

    private String formatearHora(LocalDateTime fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(HORA_FORMATTER);
    }

    private String formatearMetodoPago(Ventas.MetodoPago metodoPago) {
        return metodoPago.name().replace("_", " ");
    }

    private BigDecimal normalizarMonto(BigDecimal monto) {
        if (monto == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return monto.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatearMoneda(BigDecimal monto) {
        return "S/ " + normalizarMonto(monto);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Caja");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
