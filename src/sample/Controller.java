package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sample.comunicaciones.ClienteTCP;
import sample.comunicaciones.Configuracion;
import sample.comunicaciones.ServidorTCP;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.stream.IntStream;

public class Controller extends Component {

    @FXML
    private Label notServidor;

    @FXML
    private Label notCliente;

    @FXML
    private Label infoServidor;

    @FXML
    private Label infoCliente;

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
    private TextField rutaArchivoS;

    @FXML
    private TextField rutaArchivoC;

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
    private final JFileChooser fc = new JFileChooser();
    private File serverFile;
    private File clientFile;
    private static Controller instance;
    private final Configuracion configuracion = Configuracion.getInstance();

    public Controller() {
        cliente = ClienteTCP.getInstance();
        servidor = ServidorTCP.getInstance();
    }

    public static Controller getInstance() {
        return instance;
    }

    public enum Estado {
        INFO, WARN, ERROR
    }

    public void initialize() {
        hostServidor.setDisable(true);
        rutaArchivoC.setDisable(true);
        rutaArchivoS.setDisable(true);
        recibidoCliente.setDisable(true);
        recibidoServidor.setDisable(true);
        puertoCliente.setText("5000");
        puertoServidor.setText("5000");
        hostCliente.setText("localhost");
        configuracion.setTiempoEspera(1500);
        instance = this;
    }

    public void guardarConfiguracion() {
        configuracion.setLongCliente(encabezadoCliente.isSelected());
        configuracion.setLongServidor(encabezadoServidor.isSelected());
        configuracion.setEbdicCliente(ebdicCliente.isSelected());
        configuracion.setEbdicServidor(ebdicServidor.isSelected());
        configuracion.setArchivoCliente(leerCliente.isSelected());
        configuracion.setArchivoServidor(leerServidor.isSelected());
        servidor.cambiarEncoding();
        cliente.cambiarEncoding();
        if (this.serverFile != null) {
            configuracion.setServerFile(serverFile);
        } else {
            configuracion.setServerFile(null);
        }
        if (this.clientFile != null) {
            configuracion.setClientFile(clientFile);
        } else {
            configuracion.setClientFile(null);
        }

        reflejarConfiguracion();
    }

    private void reflejarConfiguracion() {
        if (configuracion.getClientFile() != null) {
            textoCliente.setText("");
            textoCliente.setDisable(true);
            enviarCliente.setText("Iniciar Envios");
        } else {
            textoCliente.setDisable(false);
            enviarCliente.setText("Enviar");
        }

        if (configuracion.getServerFile() != null) {
            textoServidor.setText("");
            textoServidor.setDisable(true);
            enviarServidor.setText("Iniciar Envios");
        } else {
            textoServidor.setDisable(false);
            enviarServidor.setText("Enviar");
        }
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
            } else {
                notificar(notCliente, "Datos Invalidos", Estado.ERROR);
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
                ServidorTCP.estado = ServidorTCP.Estado.INTENTO;

                IntStream.of(1, 2, 3).forEach(i -> pausa(100));
                if (!ServidorTCP.estado.equals(ServidorTCP.Estado.FALLIDO)) {
                    cambiarEstado(conexionServidor);
                } else {
                    notificar(notServidor, "", Estado.ERROR);
                }
            } else {
                notificar(notServidor, "Datos Invalidos", Estado.ERROR);
            }
        } else {
            servidor.cerrar();
            cambiarEstado(conexionServidor);
        }
    }

    private void notificar(Label label, String s, Estado estado) {
        Thread notifica = new Thread(() -> {
            label.setText(s);
            label.setVisible(true);
            if (estado.equals(Estado.ERROR)) {
                label.setTextFill(Color.RED);
            } else if (estado.equals(Estado.INFO)) {
                label.setTextFill(Color.BLUE);
            } else if (estado.equals(Estado.WARN)) {
                label.setTextFill(Color.ORANGE);
            }
            IntStream.range(0, 5).forEach(i -> pausa(1000));
            label.setVisible(false);
        });
        notifica.start();
        notifica.setDaemon(true);
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

    public void buscarArchivo(ActionEvent actionEvent) {
        int respuesta = fc.showOpenDialog(this);

        if (respuesta == JFileChooser.APPROVE_OPTION) {
            if (actionEvent.getSource().equals(seleccionServidor)) {
                serverFile = fc.getSelectedFile();
                rutaArchivoS.setText(serverFile.getName());
            } else if (actionEvent.getSource().equals(seleccionCliente)) {
                clientFile = fc.getSelectedFile();
                rutaArchivoC.setText(clientFile.getName());
            }
        } else {
            if (actionEvent.getSource().equals(seleccionServidor)) {
                serverFile = null;
                rutaArchivoS.setText("");
            } else if (actionEvent.getSource().equals(seleccionCliente)) {
                clientFile = null;
                rutaArchivoC.setText("");
            }
        }
    }

    public synchronized void recibidoCliente(String mensaje) {
        recibidoCliente.setText(mensaje);
    }

    public synchronized void recibidoServidor(String mensaje) {
        recibidoServidor.setText(mensaje);
    }

    public void enviarCliente() {
        if (cliente.isConnected()) {
            if (enviarCliente.getText().equals("Enviar")) {
                cliente.enviar(textoCliente.getText());
            } else {
                enviarCliente.setDisable(true);
                cliente.cargarArchivo();
                Thread hiloEnvioCliente = new Thread(() -> {
                    while (!ClienteTCP.FINISH) {
                        cliente.enviar(textoCliente.getText());
                        pausa(configuracion.getTiempoEspera());
                    }
                    enviarCliente.setDisable(false);
                });
                hiloEnvioCliente.start();
                hiloEnvioCliente.setDaemon(false);
            }
        }
    }

    public void enviarServidor() {
        if (!enviarServidor.getText().equals("Enviar")) {
            servidor.cargarArchivo();
            enviarServidor.setDisable(true);
            Thread hiloEnvios = new Thread(() -> {
                while (!ServidorTCP.FINISH) {
                    notificar(notServidor, "Enviando...", Estado.INFO);
                }
                enviarServidor.setDisable(false);
            });
            hiloEnvios.start();
            hiloEnvios.setDaemon(false);
        }
        servidor.enviar(textoServidor.getText());
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

    public void pausa(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

