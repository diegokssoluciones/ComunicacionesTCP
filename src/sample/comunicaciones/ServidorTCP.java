package sample.comunicaciones;

import com.ks.lib.tcp.Cliente;
import com.ks.lib.tcp.EventosTCP;
import com.ks.lib.tcp.Servidor;
import com.ks.lib.tcp.Tcp;
import sample.Controller;

/**
 * Created by mcalzada on 12/09/17.
 */
public class ServidorTCP extends Servidor implements EventosTCP {

    private static final ServidorTCP INSTANCE = new ServidorTCP();

    private ServidorTCP() {
        this.setEventos(this);
    }

    public static ServidorTCP getInstance() {
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
        Controller.getInstance().recibidoServidor(s);
    }

    @Override
    public void cerrarConexion(Cliente cliente) {

    }

    public void cerrar() {
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
