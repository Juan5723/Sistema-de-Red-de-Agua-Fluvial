import modelo.RedDistribucion;
import vista.VentanaPrincipal;
import controlador.RedController;

public class Main {
    public static void main(String[] args) {
        RedDistribucion modelo = new RedDistribucion();
        VentanaPrincipal vista = new VentanaPrincipal();
        
        RedController controlador = new RedController(modelo, vista);

        // Hacer visible la interfaz gráfica
        java.awt.EventQueue.invokeLater(() -> {
            vista.setVisible(true);
        });
    }
}