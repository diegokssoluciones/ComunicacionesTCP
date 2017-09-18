package sample.comunicaciones;

import com.ks.lib.tcp.Cliente;
import com.ks.lib.tcp.EventosTCP;
import com.ks.lib.tcp.Tcp;
import sample.Controller;

/**
 * Created by mcalzada on 12/09/17.
 */
public class ClienteTCP extends Cliente implements EventosTCP {

    public static final ClienteTCP INSTANCE = new ClienteTCP();

    private ClienteTCP() {
        this.setEventos(this);
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

    public void cerrar(){
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
