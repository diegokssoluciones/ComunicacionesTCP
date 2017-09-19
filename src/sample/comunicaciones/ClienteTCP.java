package sample.comunicaciones;

import com.ks.lib.tcp.Cliente;
import com.ks.lib.tcp.EventosTCP;
import com.ks.lib.tcp.Tcp;
import com.ks.lib.tcp.protocolos.Iso;
import sample.Controller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Created by mcalzada on 12/09/17.
 */
public class ClienteTCP extends Cliente implements EventosTCP {

    public static final ClienteTCP INSTANCE = new ClienteTCP();
    private final Configuracion configuracion = Configuracion.getInstance();
    Queue<String> mensajes;

    private ClienteTCP() {
        this.setEventos(this);
        mensajes = new LinkedList<>();
    }

    public static ClienteTCP getInstance() {
        return INSTANCE;
    }

    @Override
    public void conexionEstablecida(Cliente cliente) {
        System.out.println("Se establecio conexion con " + cliente);
    }

    @Override
    public void errorConexion(String s) {

    }

    @Override
    public void datosRecibidos(String s, byte[] bytes, Tcp tcp) {
        Controller.getInstance().recibidoCliente(s);
    }

    @Override
    public void cerrarConexion(Cliente cliente) {

    }

    @Override
    public void enviar(String mensaje) {

        if (configuracion.getClientFile() != null) {
            mensaje = obtenerMensaje();
        } else {
            if (configuracion.isLongCliente()) {
                mensaje = Iso.obtenerLongitud(mensaje.length()) + mensaje;
            }
        }

        super.enviar(mensaje);
    }

    private synchronized String obtenerMensaje() {
        String response = "";
        try {
            if (mensajes.isEmpty()) {
                Reader r = new InputStreamReader(new FileInputStream(configuracion.getClientFile().getAbsolutePath()), StandardCharsets.ISO_8859_1);
                BufferedReader br = new BufferedReader(r);
                Scanner reader = new Scanner(br);
                while (reader.hasNext()) {
                    mensajes.add(reader.next());
                }
            }

            response = mensajes.peek();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void cerrar() {
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
