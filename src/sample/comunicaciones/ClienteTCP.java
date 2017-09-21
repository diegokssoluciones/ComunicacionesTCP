package sample.comunicaciones;

import com.ks.lib.tcp.Cliente;
import com.ks.lib.tcp.EventosTCP;
import com.ks.lib.tcp.Tcp;
import com.ks.lib.tcp.protocolos.Iso;
import sample.Controller;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Created by mcalzada on 12/09/17.
 */
public class ClienteTCP extends Cliente implements EventosTCP {

    public static final ClienteTCP INSTANCE = new ClienteTCP();
    public static boolean FINISH;
    private Charset encoding;
    private final Configuracion configuracion = Configuracion.getInstance();

    Queue<String> mensajes;

    private ClienteTCP() {
        this.setEventos(this);
        encoding = StandardCharsets.ISO_8859_1;
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
        }
        if (configuracion.isLongCliente()) {
            mensaje = Iso.obtenerLongitud(mensaje.length()) + mensaje;
        }
        System.out.println(mensaje);
        super.enviar(mensaje);
    }

    public void cargarArchivo() {
        FINISH = false;
        try {
            Reader r = new InputStreamReader(new FileInputStream(configuracion.getClientFile().getAbsolutePath()), encoding);
            BufferedReader br = new BufferedReader(r);
            Scanner reader = new Scanner(br);
            while (reader.hasNext()) {
                mensajes.add(reader.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private synchronized String obtenerMensaje() {
        if (!mensajes.isEmpty()) {
            return mensajes.poll();
        } else {
            FINISH = true;
        }
        return "";
    }

    public void cerrar() {
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void cambiarEncoding(){
        encoding = StandardCharsets.ISO_8859_1;
        if (configuracion.isEbdicServidor()) {
            if (Charset.isSupported("Cp284")) {
                encoding = Charset.forName("Cp284");
            }
        }
    }
}
