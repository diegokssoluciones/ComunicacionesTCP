package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
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

public class Controller extends Component {

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

    public Controller() {
        cliente = ClienteTCP.getInstance();
        servidor = ServidorTCP.getInstance();
    }

    public static Controller getInstance() {
        return instance;
    }

    public void initialize() {
        hostServidor.setDisable(true);
        rutaArchivoC.setDisable(true);
        rutaArchivoS.setDisable(true);
        instance = this;
    }


    public void guardarConfiguracion() {
        Configuracion configuracion = Configuracion.getInstance();
        configuracion.setLongCliente(encabezadoCliente.isSelected());
        configuracion.setLongServidor(encabezadoServidor.isSelected());
        configuracion.setEbdicCliente(ebdicCliente.isSelected());
        configuracion.setEbdicServidor(ebdicServidor.isSelected());
        configuracion.setArchivoCliente(leerCliente.isSelected());
        configuracion.setArchivoServidor(leerServidor.isSelected());
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
        Configuracion configuracion = Configuracion.getInstance();

        if (configuracion.getClientFile() != null) {
            textoCliente.setText("");
            textoCliente.setDisable(true);
            enviarCliente.setText("Iniciar Envios");
        }
        else{
            textoCliente.setDisable(false);
            enviarCliente.setText("Enviar");
        }

        if(configuracion.getServerFile() != null){
            textoServidor.setText("");
            textoServidor.setDisable(true);
            enviarServidor.setText("Iniciar Envios");
        }
        else{
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

