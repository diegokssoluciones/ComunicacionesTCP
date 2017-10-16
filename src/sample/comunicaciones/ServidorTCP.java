package sample.comunicaciones;

import com.ks.lib.tcp.Cliente;
import com.ks.lib.tcp.Comunicaciones;
import com.ks.lib.tcp.EventosTCP;
import com.ks.lib.tcp.Servidor;
import com.ks.lib.tcp.Tcp;
import com.ks.lib.tcp.protocolos.Iso;
import javafx.application.Platform;
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
public class ServidorTCP extends Servidor implements EventosTCP {

  private static final ServidorTCP INSTANCE = new ServidorTCP();
  public static Estado estado;
  private Charset encoding;
  public static boolean FINISH;

  private final Configuracion configuracion = Configuracion.getInstance();
  Queue<String> mensajes;

  private ServidorTCP() {
    this.setEventos(this);
    mensajes = new LinkedList<>();
  }

  public static ServidorTCP getInstance() {
    return INSTANCE;
  }

  public enum Estado {
    INTENTO, FALLIDO
  }

  @Override
  public void conexionEstablecida(Cliente cliente) {
    Platform
        .runLater(() -> Controller.getInstance().clientesConectados(this.getClientesConectados()));
  }

  @Override
  public void errorConexion(String s) {
    estado = Estado.FALLIDO;
  }

  @Override
  public void datosRecibidos(String s, byte[] bytes, Tcp tcp) {
    Controller.getInstance().recibidoServidor(s);
  }

  @Override
  public void cerrarConexion(Cliente cliente) {
    Platform
        .runLater(() -> Controller.getInstance().clientesConectados(this.getClientesConectados()));
  }

  @Override
  public void enviar(String mensaje) {
    if (configuracion.getServerFile() != null) {
      mensaje = obtenerMensaje();
    }
    if (configuracion.isLongServidor()) {
      mensaje = Iso.obtenerLongitud(mensaje.length()) + mensaje;
    }

    mensaje.replace("<STX>", Comunicaciones.STX);
    mensaje.replace("<ETX>", Comunicaciones.ETX);

    super.enviar(mensaje);
  }

  private synchronized String obtenerMensaje() {
    if (!mensajes.isEmpty()) {
      return mensajes.poll();
    } else {
      FINISH = true;
    }
    return "";
  }

  public void cargarArchivo() {
    FINISH = false;
    try {
      Reader r = new InputStreamReader(
          new FileInputStream(configuracion.getServerFile().getAbsolutePath()),
          StandardCharsets.ISO_8859_1);
      BufferedReader br = new BufferedReader(r);
      Scanner reader = new Scanner(br);
      while (reader.hasNext()) {
        mensajes.add(reader.next());
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void cerrar() {
    super.cerrar();
    Platform
        .runLater(() -> Controller.getInstance().clientesConectados(0));
  }

  public void cambiarEncoding() {
    encoding = StandardCharsets.ISO_8859_1;
    if (configuracion.isEbdicServidor()) {
      if (Charset.isSupported("Cp284")) {
        encoding = Charset.forName("Cp284");
      }
    }
  }
}