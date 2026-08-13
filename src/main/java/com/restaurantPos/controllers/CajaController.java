package com.restaurantPos.controllers;

import com.restaurantPos.models.DetallePedido;
import com.restaurantPos.models.Pedidos;
import com.restaurantPos.models.Usuarios;
import com.restaurantPos.models.Ventas;
import com.restaurantPos.repositories.PedidosRepository;
import com.restaurantPos.repositories.VentasRepository;
import com.restaurantPos.services.ImpresoraService;
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

    private static final BigDecimal IGV_RATE = BigDecimal.valueOf(0);
    private static final int TICKET_WIDTH =48;
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TICKET_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PedidosRepository pedidosRepository;
    private final VentasRepository ventasRepository;
    private final ImpresoraService impresoraService;

    private final ObservableList<Pedidos> pedidosListos = FXCollections.observableArrayList();
    private final ObservableList<DetallePedido> detallePedido = FXCollections.observableArrayList();

    private FilteredList<Pedidos> pedidosFiltrados;
    private Pedidos pedidoSeleccionado;
    private Ventas ventaRegistrada;

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
    private Label lblMetodoPagoResumen;

    @FXML
    private Label lblPedidoSeleccionado;

    @FXML
    private Label lblPedidosPendientes;

    @FXML
    private Label lblTotalCobroResumen;

    @FXML
    private TextArea txtTicketPreview;

    @FXML
    private TextField txtBuscarPedido;

    @FXML
    public void initialize() {
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

        List<Pedidos> pedidos = pedidosRepository.findTodosPedidos(inicioDia, finDia
        );

        pedidosListos.setAll(pedidos);

        lblPedidosPendientes.setText(String.valueOf(pedidosListos.size()));
        tblPedidosCaja.refresh();
    }

    private void seleccionarPedido(Pedidos pedido) {

        if (pedido == null) {
            limpiarSeleccion();
            return;
        }
        pedidoSeleccionado = pedido;

        detallePedido.setAll(pedido.getDetalles());
        lblPedidoSeleccionado.setText("#" + pedido.getId_pedido());
        lblBoletaPedido.setText("#" + pedido.getId_pedido());
        lblBoletaCliente.setText(obtenerCliente(pedido));
        lblBoletaMesa.setText(obtenerMesa(pedido));
        lblBoletaCajero.setText(obtenerCajero());
        lblBoletaVenta.setText("Pendiente");
        actualizarTotales(pedido.getTotal());

        boolean pagado = ventasRepository.existsByPedidoAndEstadopago(
                pedido, Ventas.EstadoPago.PAGADO
        );

        if (pagado) {
            lblBoletaEstadoVenta.setText("PAGADO");


        ventaRegistrada = ventasRepository.findByPedidoAndEstadopago(
                pedido, Ventas.EstadoPago.PAGADO).orElse(null);

        if (ventaRegistrada != null) {
            lblBoletaVenta.setText("# " + ventaRegistrada.getId_venta()
            );

        }else {
            lblBoletaVenta.setText("Venta registrada"
            );
        }

        btnRegistrarVenta.setDisable(true);
        btnGenerarBoleta.setDisable(false);
        generarTicketPreview();

        }else{
            lblBoletaEstadoVenta.setText("PEDIENTE DE PAGO"
            );
            ventaRegistrada = null;

            btnRegistrarVenta.setDisable(false);
            btnGenerarBoleta.setDisable(true);

            txtTicketPreview.clear();
        }
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

        try{
            String ticket = construirTicket();
            impresoraService.imprimirTicket(ticket);
            generarTicketPreview();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Ticket generado para la venta #" + ventaRegistrada.getId_venta() + ".");
        }catch (Exception e){
            mostrarAlerta(Alert.AlertType.ERROR, "La venta fue Registrada, pero no se pudo imprimir el ticketn\n\n"
            + e.getMessage());
        }

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
        txtTicketPreview.setText(construirTicket());
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

    private String construirTicket(){
        if (pedidoSeleccionado == null) {
            return "";
        }

        StringBuilder ticket = new StringBuilder();
        ticket.append(centrarTexto("RESTAURANT POS")).append('\n');
        ticket.append(centrarTexto("TICKET DE VENTA")).append('\n');
        ticket.append(lineaSeparadora('=')).append('\n');
        ticket.append(filaTicket("Venta", ventaRegistrada == null ? "Pendiente" : "#" + ventaRegistrada.getId_venta())).append('\n');
        ticket.append(filaTicket("Pedido", "#" + pedidoSeleccionado.getId_pedido())).append('\n');
        ticket.append(filaTicket("Fecha", LocalDateTime.now().format(TICKET_FORMATTER))).append('\n');
        ticket.append(filaTicket("Cajero", obtenerCajero())).append('\n');
        ticket.append(filaTicket("Cliente", obtenerCliente(pedidoSeleccionado))).append('\n');
        ticket.append(filaTicket("Mesa", obtenerMesa(pedidoSeleccionado))).append('\n');
        ticket.append(filaTicket("Pago", cbMetodoPago.getValue() == null ? "-" : formatearMetodoPago(cbMetodoPago.getValue()))).append('\n');
        ticket.append(lineaSeparadora('-')).append('\n');
        ticket.append(filaTicket("Cant  Producto", "Importe")).append('\n');
        ticket.append(lineaSeparadora('-')).append('\n');

        for (DetallePedido detalle : detallePedido) {
            agregarDetalleTicket(ticket, detalle);
        }

        ticket.append(lineaSeparadora('-')).append('\n');
        ticket.append(filaTicket("Subtotal", lblBoletaSubtotal.getText())).append('\n');
        ticket.append(filaTicket("IGV", lblBoletaIgv.getText())).append('\n');
        ticket.append(filaTicket("TOTAL", lblBoletaTotal.getText())).append('\n');

        ticket.append(lineaSeparadora('=')).append('\n');
        ticket.append(centrarTexto("GRACIAS POR SU COMPRA")).append('\n');
        ticket.append("\n\n\n");

        return ticket.toString();
    }

    private void agregarDetalleTicket(StringBuilder ticket, DetallePedido detalle) {
        String cantidad = String.valueOf(detalle.getCantidad() == null ? 0 : detalle.getCantidad());
        String producto = detalle.getProducto() == null ? "Producto" : detalle.getProducto().getNombre();
        String importe = formatearMoneda(BigDecimal.valueOf(detalle.getSubtotal() == null ? 0 : detalle.getSubtotal()));
        String prefijo = cantidad + " x ";
        int anchoProducto = TICKET_WIDTH - prefijo.length() - importe.length() - 1;

        if (anchoProducto < 8) {
            ticket.append(filaTicket(prefijo.trim(), importe)).append('\n');
            agregarLineasProducto(ticket, producto, TICKET_WIDTH);
            return;
        }

        List<String> lineasProducto = dividirTexto(producto, anchoProducto);
        ticket.append(rellenarDerecha(prefijo + lineasProducto.getFirst(), TICKET_WIDTH - importe.length()))
                .append(importe)
                .append('\n');

        for (int i = 1; i < lineasProducto.size(); i++) {
            ticket.append(rellenarDerecha(" ".repeat(prefijo.length()) + lineasProducto.get(i), TICKET_WIDTH)).append('\n');
        }
    }

    private void agregarLineasProducto(StringBuilder ticket, String producto, int ancho) {
        for (String linea : dividirTexto(producto, ancho)) {
            ticket.append(linea).append('\n');
        }
    }

    private String filaTicket(String izquierda, String derecha) {
        String textoIzquierda = izquierda == null ? "" : izquierda.trim();
        String textoDerecha = derecha == null ? "" : derecha.trim();
        int espacioDisponible = TICKET_WIDTH - textoDerecha.length() - 1;

        if (espacioDisponible < 4 || textoIzquierda.length() > espacioDisponible) {
            return textoIzquierda + '\n' + rellenarIzquierda(textoDerecha, TICKET_WIDTH);
        }

        return rellenarDerecha(textoIzquierda, TICKET_WIDTH - textoDerecha.length()) + textoDerecha;
    }

    private List<String> dividirTexto(String texto, int ancho) {
        String valor = texto == null || texto.isBlank() ? "-" : texto.trim();
        if (valor.length() <= ancho) {
            return List.of(valor);
        }

        java.util.ArrayList<String> lineas = new java.util.ArrayList<>();
        StringBuilder linea = new StringBuilder();
        for (String palabra : valor.split("\\s+")) {
            if (linea.isEmpty()) {
                linea.append(palabra);
            } else if (linea.length() + 1 + palabra.length() <= ancho) {
                linea.append(' ').append(palabra);
            } else {
                lineas.add(linea.toString());
                linea.setLength(0);
                linea.append(palabra);
            }

            while (linea.length() > ancho) {
                lineas.add(linea.substring(0, ancho));
                linea.delete(0, ancho);
            }
        }

        if (!linea.isEmpty()) {
            lineas.add(linea.toString());
        }
        return lineas;
    }

    private String centrarTexto(String texto) {
        String valor = texto == null ? "" : texto.trim();
        if (valor.length() >= TICKET_WIDTH) {
            return valor.substring(0, TICKET_WIDTH);
        }
        int espaciosIzquierda = (TICKET_WIDTH - valor.length()) / 2;
        return " ".repeat(espaciosIzquierda) + valor;
    }

    private String lineaSeparadora(char caracter) {
        return String.valueOf(caracter).repeat(TICKET_WIDTH);
    }

    private String rellenarDerecha(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto;
        }
        return texto + " ".repeat(ancho - texto.length());
    }

    private String rellenarIzquierda(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto;
        }
        return " ".repeat(ancho - texto.length()) + texto;
    }
}
