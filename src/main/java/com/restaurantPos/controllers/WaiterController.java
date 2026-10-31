package com.restaurantPos.controllers;

import com.restaurantPos.models.DetallePedido;
import com.restaurantPos.models.Mesas;
import com.restaurantPos.models.Pedidos;
import com.restaurantPos.repositories.PedidosRepository;
import com.restaurantPos.services.MesasService;
import com.restaurantPos.services.NavigationService;
import com.restaurantPos.services.PedidoService;
import com.restaurantPos.utils.Paths;
import com.restaurantPos.utils.PathsCss;
import com.restaurantPos.utils.TiempoAcumulado;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WaiterController {

    private static final List<Pedidos.EstadoPedido> ESTADOS_ACTIVOS = List.of(
            Pedidos.EstadoPedido.PENDIENTE,
            Pedidos.EstadoPedido.PREPARANDO,
            Pedidos.EstadoPedido.LISTO
    );

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String FILTRO_TODOS = "Todos";
    private static final String FILTRO_TODAS_LAS_MESAS = "Todas";

    private final PedidosRepository pedidosRepository;
    private final PedidoService pedidoService;
    private final MesasService mesasService;
    private final NavigationService navigationService;

    private final ObservableList<Pedidos> pedidos = FXCollections.observableArrayList();
    private final ObservableList<DetallePedido> detallePedido = FXCollections.observableArrayList();

    private FilteredList<Pedidos> pedidosFiltrados;
    private Pedidos pedidoSeleccionado;
    private Timeline reloj;

    @FXML
    private Button btnActualizarSeguimiento;

    @FXML
    private Button btnMarcarServido;

    @FXML
    private Button btnNotificarCliente;

    @FXML
    private ComboBox<String> cbFiltroEstado;

    @FXML
    private ComboBox<String> cbFiltroMesa;

    @FXML
    private TableColumn<Pedidos, String> colCliente;

    @FXML
    private TableColumn<DetallePedido, Integer> colDetalleCantidad;

    @FXML
    private TableColumn<DetallePedido, String> colDetalleEstado;

    @FXML
    private TableColumn<DetallePedido, String> colDetalleNota;

    @FXML
    private TableColumn<DetallePedido, String> colDetalleProducto;

    @FXML
    private TableColumn<Pedidos, String> colEstadoCocina;

    @FXML
    private TableColumn<Pedidos, String> colHoraEnvio;

    @FXML
    private TableColumn<Pedidos, String> colMesa;

    @FXML
    private TableColumn<Pedidos, String> colObservaciones;

    @FXML
    private TableColumn<Pedidos, String> colPedido;

    @FXML
    private TableColumn<Pedidos, String> colPrioridad;

    @FXML
    private TableColumn<Pedidos, String> colTiempoEspera;

    @FXML
    private Label lblEsperaSeleccionada;

    @FXML
    private Label lblEstadoCocinaSeleccionado;

    @FXML
    private Label lblMensajeSala;

    @FXML
    private Label lblMesaClienteSeleccionado;

    @FXML
    private Label lblPedidoDestacado;

    @FXML
    private Label lblPedidoSeleccionado;

    @FXML
    private Label lblPedidosEnviados;

    @FXML
    private Label lblPedidosListos;

    @FXML
    private Label lblPedidosPendientes;

    @FXML
    private Label lblPedidosPreparacion;

    @FXML
    private Label lblSiguienteAccion;

    @FXML
    private Label lblTotalSeguimiento;

    @FXML
    private Label lblUltimaActualizacion;

    @FXML
    private TableView<DetallePedido> tblDetallePedidoCocina;

    @FXML
    private TableView<Pedidos> tblPedidosCocina;

    @FXML
    private TextField txtBuscarPedido;

    @FXML
    public void initialize() {
        configurarFiltros();
        configurarTablaPedidos();
        configurarTablaDetalle();
        configurarBusqueda();
        cargarPedidos();
        iniciarReloj();
    }

    private void configurarFiltros() {
        cbFiltroEstado.getItems().setAll(
                FILTRO_TODOS,
                formatearEstado(Pedidos.EstadoPedido.PENDIENTE),
                formatearEstado(Pedidos.EstadoPedido.PREPARANDO),
                formatearEstado(Pedidos.EstadoPedido.LISTO)
        );
        cbFiltroEstado.getSelectionModel().selectFirst();

        List<String> mesas = new ArrayList<>();
        mesas.add(FILTRO_TODAS_LAS_MESAS);
        mesasService.listarMesas().stream()
                .map(Mesas::getNumero)
                .filter(numero -> numero != null && !numero.isBlank())
                .distinct()
                .sorted()
                .forEach(mesas::add);

        cbFiltroMesa.getItems().setAll(mesas);
        cbFiltroMesa.getSelectionModel().selectFirst();
    }

    private void configurarTablaPedidos() {
        colPedido.setCellValueFactory(cellData ->
                new SimpleStringProperty("#" + cellData.getValue().getId_pedido()));
        colMesa.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerMesa(cellData.getValue())));
        colCliente.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerCliente(cellData.getValue())));
        colHoraEnvio.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearHora(cellData.getValue().getFecha())));
        colEstadoCocina.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearEstado(cellData.getValue().getEstado())));
        colTiempoEspera.setCellValueFactory(cellData ->
                new SimpleStringProperty(calcularEspera(cellData.getValue())));
        colPrioridad.setCellValueFactory(cellData ->
                new SimpleStringProperty(calcularPrioridad(cellData.getValue())));
        colObservaciones.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerObservaciones(cellData.getValue())));

        pedidosFiltrados = new FilteredList<>(pedidos, pedido -> true);
        tblPedidosCocina.setItems(pedidosFiltrados);

        tblPedidosCocina.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> mostrarDetalle(seleccionado));
    }

    private void configurarTablaDetalle() {
        colDetalleCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDetalleProducto.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerProducto(cellData.getValue())));
        colDetalleEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(pedidoSeleccionado == null
                        ? "-"
                        : formatearEstado(pedidoSeleccionado.getEstado())));
        colDetalleNota.setCellValueFactory(cellData -> new SimpleStringProperty("Sin nota"));

        tblDetallePedidoCocina.setItems(detallePedido);
    }

    private void configurarBusqueda() {
        txtBuscarPedido.textProperty().addListener(
                (observable, anterior, nuevo) -> aplicarFiltros());
        cbFiltroEstado.valueProperty().addListener(
                (observable, anterior, nuevo) -> aplicarFiltros());
        cbFiltroMesa.valueProperty().addListener(
                (observable, anterior, nuevo) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String busqueda = txtBuscarPedido.getText() == null
                ? ""
                : txtBuscarPedido.getText().trim().toLowerCase();

        String estadoSeleccionado = cbFiltroEstado.getValue();
        String mesaSeleccionada = cbFiltroMesa.getValue();

        pedidosFiltrados.setPredicate(pedido -> {
            if (!busqueda.isBlank() && !coincideBusqueda(pedido, busqueda)) {
                return false;
            }

            if (estadoSeleccionado != null
                    && !FILTRO_TODOS.equals(estadoSeleccionado)
                    && !estadoSeleccionado.equals(formatearEstado(pedido.getEstado()))) {
                return false;
            }

            return mesaSeleccionada == null
                    || FILTRO_TODAS_LAS_MESAS.equals(mesaSeleccionada)
                    || mesaSeleccionada.equals(obtenerNumeroMesa(pedido));
        });

        actualizarResumen();
        actualizarPedidoDestacado();
    }

    private boolean coincideBusqueda(Pedidos pedido, String busqueda) {
        return String.valueOf(pedido.getId_pedido()).contains(busqueda)
                || obtenerNumeroMesa(pedido).toLowerCase().contains(busqueda)
                || obtenerCliente(pedido).toLowerCase().contains(busqueda)
                || obtenerTipoPedido(pedido).toLowerCase().contains(busqueda);
    }

    private void cargarPedidos() {
        Long idSeleccionado = pedidoSeleccionado == null
                ? null
                : pedidoSeleccionado.getId_pedido();

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);

        List<Pedidos> cargados = pedidosRepository.findByEstadoConDetalles(
                ESTADOS_ACTIVOS, inicioDia, finDia);

        pedidos.setAll(cargados.stream()
                .sorted(Comparator.comparing(
                        Pedidos::getFecha,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList());

        if (idSeleccionado != null) {
            pedidos.stream()
                    .filter(pedido -> pedido.getId_pedido() == idSeleccionado)
                    .findFirst()
                    .ifPresent(pedido ->
                            tblPedidosCocina.getSelectionModel().select(pedido));
        }

        actualizarResumen();
        actualizarPedidoDestacado();
        actualizarUltimaActualizacion();
        tblPedidosCocina.refresh();
    }

    private void mostrarDetalle(Pedidos pedido) {
        pedidoSeleccionado = pedido;

        if (pedido == null) {
            limpiarDetalle();
            return;
        }

        detallePedido.setAll(pedido.getDetalles() == null
                ? List.of()
                : pedido.getDetalles());

        lblPedidoSeleccionado.setText("Pedido #" + pedido.getId_pedido());
        lblMesaClienteSeleccionado.setText(
                obtenerMesa(pedido) + " - Cliente: " + obtenerCliente(pedido));
        lblEstadoCocinaSeleccionado.setText(formatearEstado(pedido.getEstado()));
        aplicarEstiloEstado(lblEstadoCocinaSeleccionado, pedido.getEstado());
        lblEsperaSeleccionada.setText(calcularEspera(pedido));
        lblMensajeSala.setText(construirMensajeSala(pedido));
        lblSiguienteAccion.setText(obtenerSiguienteAccion(pedido));
        aplicarEstiloEstado(lblSiguienteAccion, pedido.getEstado());

        tblDetallePedidoCocina.refresh();
    }

    private void limpiarDetalle() {
        detallePedido.clear();
        lblPedidoSeleccionado.setText("Pedido -");
        lblMesaClienteSeleccionado.setText("Selecciona un pedido para ver el detalle");
        lblEstadoCocinaSeleccionado.setText("-");
        aplicarEstiloEstado(lblEstadoCocinaSeleccionado, null);
        lblEsperaSeleccionada.setText("-");
        lblMensajeSala.setText("Selecciona un pedido para consultar su estado y las acciones disponibles.");
        lblSiguienteAccion.setText("Selecciona un pedido de la lista");
        aplicarEstiloEstado(lblSiguienteAccion, null);
        tblDetallePedidoCocina.refresh();
    }

    private void actualizarResumen() {
        long pendientes = pedidos.stream()
                .filter(pedido -> pedido.getEstado() == Pedidos.EstadoPedido.PENDIENTE)
                .count();
        long preparando = pedidos.stream()
                .filter(pedido -> pedido.getEstado() == Pedidos.EstadoPedido.PREPARANDO)
                .count();
        long listos = pedidos.stream()
                .filter(pedido -> pedido.getEstado() == Pedidos.EstadoPedido.LISTO)
                .count();

        lblPedidosEnviados.setText(String.valueOf(pedidos.size()));
        lblPedidosPendientes.setText(String.valueOf(pendientes));
        lblPedidosPreparacion.setText(String.valueOf(preparando));
        lblPedidosListos.setText(String.valueOf(listos));

        int visibles = pedidosFiltrados == null ? pedidos.size() : pedidosFiltrados.size();
        lblTotalSeguimiento.setText(visibles + (visibles == 1 ? " activo" : " activos"));
    }

    private void actualizarPedidoDestacado() {
        if (pedidosFiltrados == null) {
            lblPedidoDestacado.setText("Sin pedidos activos");
            return;
        }

        Pedidos destacado = pedidosFiltrados.stream()
                .max(Comparator.comparingLong(this::calcularMinutosEspera))
                .orElse(null);

        if (destacado == null) {
            lblPedidoDestacado.setText("Sin pedidos activos");
            return;
        }

        lblPedidoDestacado.setText(
                "#" + destacado.getId_pedido()
                        + " - " + obtenerMesa(destacado)
                        + " (" + formatearEstado(destacado.getEstado()) + ")");
    }

    private void actualizarUltimaActualizacion() {
        lblUltimaActualizacion.setText(LocalTime.now().format(HORA_FORMATTER));
    }

    private void iniciarReloj() {
        reloj = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            actualizarUltimaActualizacion();
            tblPedidosCocina.refresh();

            if (pedidoSeleccionado != null) {
                lblEsperaSeleccionada.setText(calcularEspera(pedidoSeleccionado));
                lblMensajeSala.setText(construirMensajeSala(pedidoSeleccionado));
                lblSiguienteAccion.setText(obtenerSiguienteAccion(pedidoSeleccionado));
            }
        }));
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }

    @FXML
    void btnIrOrders(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            navigationService.changeScene(stage, Paths.ORDERS_PAGE, PathsCss.CSS_ORDERS);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo abrir la vista de pedidos: " + e.getMessage());
        }
    }

    @FXML
    void btnActualizarSeguimiento(ActionEvent event) {
        cargarPedidos();
    }

    @FXML
    void btnNotificarCliente(ActionEvent event) {
        if (pedidoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Seleccione un pedido para notificar al cliente.");
            return;
        }

        String mensaje = construirMensajeSala(pedidoSeleccionado);
        lblMensajeSala.setText(mensaje);
        mostrarAlerta(Alert.AlertType.INFORMATION,
                "Notificacion para " + obtenerMesa(pedidoSeleccionado) + ":\n\n" + mensaje);
    }

    @FXML
    void btnMarcarServido(ActionEvent event) {
        if (pedidoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Seleccione un pedido para marcarlo como servido.");
            return;
        }

        if (pedidoSeleccionado.getEstado() != Pedidos.EstadoPedido.LISTO) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Solo se pueden marcar como servidos los pedidos en estado Listo.");
            return;
        }

        long idPedido = pedidoSeleccionado.getId_pedido();
        pedidoService.cambiarEstado(idPedido, Pedidos.EstadoPedido.SERVIDO);

        pedidoSeleccionado = null;
        tblPedidosCocina.getSelectionModel().clearSelection();
        cargarPedidos();

        mostrarAlerta(Alert.AlertType.INFORMATION,
                "El pedido #" + idPedido + " fue marcado como servido.");
    }

    private String obtenerProducto(DetallePedido detalle) {
        if (detalle.getProducto() == null || detalle.getProducto().getNombre() == null) {
            return "-";
        }
        return detalle.getProducto().getNombre();
    }

    private String obtenerCliente(Pedidos pedido) {
        if (pedido.getCliente() == null
                || pedido.getCliente().getNombre() == null
                || pedido.getCliente().getNombre().isBlank()) {
            return "Cliente general";
        }
        return pedido.getCliente().getNombre();
    }

    private String obtenerMesa(Pedidos pedido) {
        String numero = obtenerNumeroMesa(pedido);
        return numero.isBlank() ? "Sin mesa" : "Mesa " + numero;
    }

    private String obtenerNumeroMesa(Pedidos pedido) {
        if (pedido.getMesa() == null || pedido.getMesa().getNumero() == null) {
            return "";
        }
        return pedido.getMesa().getNumero();
    }

    private String obtenerTipoPedido(Pedidos pedido) {
        if (pedido.getTipoPedido() == null || pedido.getTipoPedido().isBlank()) {
            return "-";
        }
        return pedido.getTipoPedido();
    }

    private String obtenerObservaciones(Pedidos pedido) {
        List<String> observaciones = new ArrayList<>();

        if (pedido.getNotas() != null && !pedido.getNotas().isBlank()) {
            observaciones.add(pedido.getNotas());
        }

        String tipoPedido = obtenerTipoPedido(pedido);
        if (!tipoPedido.equals("-")) {
            observaciones.add("Tipo: " + tipoPedido);
        }

        if (pedido.getCliente() != null
                && pedido.getCliente().getTelefono() != null
                && !pedido.getCliente().getTelefono().isBlank()) {
            observaciones.add("Tel: " + pedido.getCliente().getTelefono());
        }

        return observaciones.isEmpty()
                ? "Sin observaciones"
                : String.join(" | ", observaciones);
    }

    private String formatearHora(LocalDateTime fecha) {
        return fecha == null ? "-" : fecha.format(HORA_FORMATTER);
    }

    private String formatearEstado(Pedidos.EstadoPedido estado) {
        if (estado == null) {
            return "-";
        }

        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case PREPARANDO -> "En preparacion";
            case LISTO -> "Listo";
            case SERVIDO -> "Servido";
            case ANULADO -> "Anulado";
        };
    }

    private String calcularEspera(Pedidos pedido) {
        return pedido.getFecha() == null
                ? "-"
                : TiempoAcumulado.calcularTiempoEspera(pedido);
    }

    private long calcularMinutosEspera(Pedidos pedido) {
        if (pedido.getFecha() == null) {
            return 0;
        }
        return java.time.Duration.between(pedido.getFecha(), LocalDateTime.now()).toMinutes();
    }

    private String calcularPrioridad(Pedidos pedido) {
        long minutos = calcularMinutosEspera(pedido);

        if (minutos >= 15) {
            return "Alta";
        }
        if (minutos >= 8) {
            return "Media";
        }
        return "Normal";
    }

    private String construirMensajeSala(Pedidos pedido) {
        long minutos = calcularMinutosEspera(pedido);

        return switch (pedido.getEstado()) {
            case PENDIENTE ->
                    "Pedido recibido por cocina. Aun no inicia la preparacion.";
            case PREPARANDO -> minutos >= 15
                    ? "El pedido supera los 15 min. Revisar cocina y avisar al cliente."
                    : "El pedido sigue en preparacion. Avisar al cliente si supera 15 min.";
            case LISTO ->
                    "Pedido listo para servir. Llevar a la mesa y confirmar la entrega.";
            case SERVIDO ->
                    "Pedido servido al cliente.";
            case ANULADO ->
                    "Pedido anulado. No realizar acciones de servicio.";
        };
    }

    private String obtenerSiguienteAccion(Pedidos pedido) {
        long minutos = calcularMinutosEspera(pedido);

        return switch (pedido.getEstado()) {
            case PENDIENTE ->
                    "Confirmar recepcion con cocina";
            case PREPARANDO -> minutos >= 15
                    ? "Revisar demora con cocina y avisar al cliente"
                    : "Confirmar tiempo estimado con cocina";
            case LISTO ->
                    "Llevar el pedido a la mesa";
            case SERVIDO ->
                    "Pedido completado";
            case ANULADO ->
                    "Sin acciones pendientes";
        };
    }

    private void aplicarEstiloEstado(Label label, Pedidos.EstadoPedido estado) {
        label.getStyleClass().removeAll("danger-text", "warning-text", "success-text");

        if (estado == null) {
            return;
        }

        switch (estado) {
            case PENDIENTE, ANULADO -> label.getStyleClass().add("danger-text");
            case PREPARANDO -> label.getStyleClass().add("warning-text");
            case LISTO, SERVIDO -> label.getStyleClass().add("success-text");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Seguimiento de pedidos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
