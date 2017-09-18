package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sample.comunicaciones.ClienteTCP;
import sample.comunicaciones.ServidorTCP;

public class Controller {

    @FXML
    private Tab pestanaServidor;

    @FXML
    private Circle indicadorServer;

    @FXML
    private TextField puertoServidor;

    @FXML
    private Button conexionServidor;

    @FXML
    private TextArea textoServidor;

    @FXML
    private Button enviarServidor;

    @FXML
    private Tab pestanaCliente;

    @FXML
    private Circle indicadorCliente;

    @FXML
    private TextField hostServidor;

    @FXML
    private TextField hostCliente;

    @FXML
    private TextField puertoCliente;

    @FXML
    private Button conexionCliente;

    @FXML
    private TextArea textoCliente;

    @FXML
    private TextArea recibidoServidor;

    @FXML
    private TextArea recibidoCliente;

    @FXML
    private Button enviarCliente;

    @FXML
    private Tab pestañaConfig;

    @FXML
    private CheckBox leerServidor;

    @FXML
    private CheckBox encabezadoServidor;

    @FXML
    private CheckBox ebdicServidor;

    @FXML
    private CheckBox leerCliente;

    @FXML
    private CheckBox encabezadoCliente;

    @FXML
    private CheckBox ebdicCliente;

    @FXML
    private Button botonGuardar;

    @FXML
    private Button seleccionServidor;

    @FXML
    private Button seleccionCliente;

    private ClienteTCP cliente;
    private ServidorTCP servidor;
    private static Controller instance;

    public Controller() {
        cliente = ClienteTCP.getInstance();
        servidor = ServidorTCP.getInstance();
    }

    public static Controller getInstance() {
        return instance;
    }

    public void initialize() {
        hostServidor.setDisable(true);
        instance = this;
    }

    public void buscarArchivo() {

    }

    public void guardarConfiguracion() {

    }

    public void conexioneCliente() throws Exception {
        if (conexionCliente.getText().equals("Conectar")) {
            if (validarIP(hostCliente.getText()) && validarPuerto(puertoCliente.getText())) {
                cliente.setPuerto(Integer.parseInt(puertoCliente.getText()));
                cliente.setIP(hostCliente.getText());
                cliente.conectar();

                long espera = System.currentTimeMillis() + 5000;

                while (!cliente.isConnected() && System.currentTimeMillis() < espera) {

                }
                if (cliente.isConnected()) {
                    cambiarEstado(conexionCliente);
                }
            }
        } else {
            if (cliente.isConnected()) {
                cliente.cerrar();
            }
            cambiarEstado(conexionCliente);
        }
    }

    public void conexionServidor() {
        if (conexionServidor.getText().equals("Conectar")) {
            if (validarPuerto(puertoServidor.getText())) {
                servidor.setIP("localhost");
                servidor.setPuerto(Integer.parseInt(puertoServidor.getText()));
                servidor.conectar();

                cambiarEstado(conexionServidor);
            }
        } else {
            servidor.cerrar();
            cambiarEstado(conexionServidor);
        }
    }

    private void cambiarEstado(Button boton) {
        if (boton.getText().equals("Conectar")) {
            boton.setText("Desconectar");
            boton.setTextFill(Color.RED);
            if (boton.equals(conexionCliente)) {
                indicadorCliente.setFill(Color.GREEN);
                hostCliente.setDisable(true);
                puertoCliente.setDisable(true);
                enviarCliente.setDisable(false);
            } else {
                indicadorServer.setFill(Color.GREEN);
                puertoServidor.setDisable(true);
                enviarServidor.setDisable(false);
            }
        } else {
            boton.setText("Conectar");
            boton.setTextFill(Color.GREEN);
            if (boton.equals(conexionCliente)) {
                indicadorCliente.setFill(Color.RED);
                hostCliente.setDisable(false);
                puertoCliente.setDisable(false);
                enviarCliente.setDisable(true);
            } else {
                indicadorServer.setFill(Color.RED);
                puertoServidor.setDisable(false);
                enviarServidor.setDisable(true);
            }
        }
    }

    public void recibidoCliente(String mensaje) {
        recibidoCliente.setText(mensaje);
    }

    public void recibidoServidor(String mensaje) {
        recibidoServidor.setText(mensaje);
    }

    public void enviarCliente() {
        if (!textoCliente.getText().equals("") && cliente.isConnected()) {
            cliente.enviar(textoCliente.getText());
        }
    }

    public void enviarServidor() {
        if (!textoServidor.getText().equals("")) {
            servidor.enviar(textoServidor.getText());
        }
    }

    private boolean validarPuerto(String campo) {
        if (campo.equals("") || !campo.matches("\\d+")) {
            return false;
        }
        return true;
    }

    private boolean validarIP(String campo) {
        if (campo.equals("") && !campo.matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")) {
            return false;
        }
        return true;
    }
}

