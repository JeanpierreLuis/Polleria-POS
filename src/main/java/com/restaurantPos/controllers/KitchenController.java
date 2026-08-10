package com.restaurantPos.controllers;

import com.restaurantPos.models.DetallePedido;
import com.restaurantPos.models.Pedidos;
import com.restaurantPos.repositories.PedidosRepository;
import com.restaurantPos.services.PedidoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.restaurantPos.utils.TiempoAcumulado;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import static com.restaurantPos.models.Pedidos.EstadoPedido.PENDIENTE;


@RequiredArgsConstructor
@Component
public class KitchenController {
    private final PedidosRepository pedidosRepository;
    private final PedidoService pedidoService;
    private Pedidos pedidoSeleccionado;
    private TiempoAcumulado tiempoAcumulado;
    private Timeline reloj;

    @FXML
    private FlowPane pedidosContainer;

    @FXML
    private Label lblPedidoSeleccionado;

    @FXML
    private Label lblEstadoPedido;

    @FXML
    private Label lblListos;

    @FXML
    private Label lblPendientes;

    @FXML
    private Label lblPreparando;

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
        actulizarResumen();
        iniciarReloj();
    }

    private void cargarPedidosPendientes(){
        pedidosContainer.getChildren().clear();

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);

        List<Pedidos> pedidos = pedidosRepository.findByEstadoConDetalles
                (List.of(
                        PENDIENTE,
                        Pedidos.EstadoPedido.PREPARANDO
                        ),
                        inicioDia,
                        finDia
                );

        for (Pedidos pedido : pedidos) {
            VBox card =  new VBox(5);
            card.getStyleClass().add("pedido-card");
            card.setPrefWidth(220);

            Label lblTitulo = new Label("Pedido #" + pedido.getId_pedido());
            Label lblEstado = new Label("Estado: " + pedido.getEstado().name());

            Label lblMesa = new Label("Mesa: " + pedido.getMesa().getNumero());

            card.getChildren().addAll(lblTitulo, lblEstado, lblMesa);

            Separator separator = new Separator();
            card.getChildren().add(separator);
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

        pedidoSeleccionado = pedido;
        cargarCabeceraPedido(pedido);
        cargarDetallePedido(pedido);
    }

    private void cargarCabeceraPedido(Pedidos pedido){
        lblPedidoSeleccionado.setText("Pedido #" + pedido.getId_pedido());
        lblEstadoPedido.setText(pedido.getEstado().name());
        lblMesaPedido.setText("Mesa " + pedido.getMesa().getNumero());

        lblTiempoPedido.setText(tiempoAcumulado.calcularTiempoEspera(pedido));
    }

    public void configurarTablaDetalle(){
        colDetalleCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );
        colDetalleProducto.setCellValueFactory(
                cellData -> new SimpleStringProperty
                        (cellData.getValue().getProducto().getNombre()
                        )
        );
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
    void btnMarcarRecibido(ActionEvent event) {
        lblEstadoPedido.setText("Recibido");
    }

    @FXML
    void btnIniciarPreparacion(ActionEvent event) {
        if (pedidoSeleccionado == null ){
            return;
        }
        pedidoService.cambiarEstado(
                pedidoSeleccionado.getId_pedido(),
                Pedidos.EstadoPedido.PREPARANDO
        );

        cargarPedidosPendientes();
        actulizarResumen();
        lblEstadoPedido.setText("PREPARANDO");
    }
    @FXML
    void btnMarcarListo(ActionEvent event) {

        if(pedidoSeleccionado == null){
            return;
        }
        pedidoService.cambiarEstado(
                pedidoSeleccionado.getId_pedido(),
                Pedidos.EstadoPedido.LISTO
        );

        cargarPedidosPendientes();
        actulizarResumen();

        lblPedidoSeleccionado.setText("-");
        lblEstadoPedido.setText("-");
        lblMesaPedido.setText("-");
        lblTiempoPedido.setText("-");

        pedidoSeleccionado = null;
    }

    private void actulizarResumen(){
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();

        LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);

        Long pendientes = pedidosRepository.countByEstadoAndFecha(
                PENDIENTE, inicioDia, finDia);

        Long preparando = pedidosRepository.countByEstadoAndFecha(
                Pedidos.EstadoPedido.PREPARANDO, inicioDia, finDia);

        Long listos = pedidosRepository.countByEstadoAndFecha(
                Pedidos.EstadoPedido.LISTO, inicioDia, finDia);

        lblPendientes.setText(String.valueOf(pendientes));
        lblPreparando.setText(String.valueOf(preparando));
        lblListos.setText(String.valueOf(listos));
    }
private void iniciarReloj() {
    reloj = new Timeline(
            new KeyFrame(
                    Duration.seconds(1), e -> {
                if (pedidoSeleccionado != null) {
                    lblTiempoPedido.setText(
                            tiempoAcumulado.calcularTiempoEspera(pedidoSeleccionado)
                    );
                }
            }
            )
    );
    reloj.setCycleCount(Timeline.INDEFINITE);
    reloj.play();
}
}
