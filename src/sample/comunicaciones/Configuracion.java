package sample.comunicaciones;

import org.jetbrains.annotations.Contract;

import java.io.File;

public class Configuracion {

    boolean longServidor;
    boolean longCliente;
    boolean ebdicServidor;
    boolean ebdicCliente;
    boolean archivoServidor;
    boolean archivoCliente;
    File serverFile;
    File clientFile;
    String rutaArchServidor;
    String rutaArchCliente;

    private Configuracion() {

    }

    @Contract(pure = true)
    public static Configuracion getInstance() {
        return Singleton.INSTANCE;
    }

    public static class Singleton {
        private static final Configuracion INSTANCE = new Configuracion();
    }

    public boolean isLongServidor() {
        return longServidor;
    }

    public void setLongServidor(boolean longServidor) {
        this.longServidor = longServidor;
    }

    public boolean isLongCliente() {
        return longCliente;
    }

    public void setLongCliente(boolean longCliente) {
        this.longCliente = longCliente;
    }

    public boolean isEbdicServidor() {
        return ebdicServidor;
    }

    public void setEbdicServidor(boolean ebdicServidor) {
        this.ebdicServidor = ebdicServidor;
    }

    public boolean isEbdicCliente() {
        return ebdicCliente;
    }

    public void setEbdicCliente(boolean ebdicCliente) {
        this.ebdicCliente = ebdicCliente;
    }

    public boolean isArchivoServidor() {
        return archivoServidor;
    }

    public void setArchivoServidor(boolean archivoServidor) {
        this.archivoServidor = archivoServidor;
    }

    public boolean isArchivoCliente() {
        return archivoCliente;
    }

    public void setArchivoCliente(boolean archivoCliente) {
        this.archivoCliente = archivoCliente;
    }

    public String getRutaArchServidor() {
        return rutaArchServidor;
    }

    public void setRutaArchServidor(String rutaArchServidor) {
        this.rutaArchServidor = rutaArchServidor;
    }

    public String getRutaArchCliente() {
        return rutaArchCliente;
    }

    public void setRutaArchCliente(String rutaArchCliente) {
        this.rutaArchCliente = rutaArchCliente;
    }

    public File getServerFile() {
        return serverFile;
    }

    public void setServerFile(File serverFile) {
        this.serverFile = serverFile;
    }

    public File getClientFile() {
        return clientFile;
    }

    public void setClientFile(File clientFile) {
        this.clientFile = clientFile;
    }
}
