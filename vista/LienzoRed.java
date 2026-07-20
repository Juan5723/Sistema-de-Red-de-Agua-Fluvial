package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import modelo.Nodo;
import modelo.Tuberia;
import modelo.TipoNodo;

public class LienzoRed extends JPanel {
    private List<Nodo> nodos;
    private List<Tuberia> tuberias;
    private Map<Nodo, Point> posiciones;

    public LienzoRed() {
        this.nodos = new ArrayList<>();
        this.tuberias = new ArrayList<>();
        this.posiciones = new HashMap<>();
        setBackground(Color.WHITE);
    }

    public void actualizarRed(List<Nodo> nuevosNodos, List<Tuberia> nuevasTuberias) {
        this.nodos = nuevosNodos;
        this.tuberias = nuevasTuberias;
        
        for (Nodo n : nodos) {
            if (!posiciones.containsKey(n)) {
                int x = (int) (Math.random() * (getWidth() - 100)) + 50;
                int y = (int) (Math.random() * (getHeight() - 100)) + 50;
                posiciones.put(n, new Point(x, y));
            }
        }
        repaint(); // Forzar a Java a volver a dibujar la pantalla
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Bordes suaves

        for (Tuberia t : tuberias) {
            Point p1 = posiciones.get(t.getOrigen());
            Point p2 = posiciones.get(t.getDestino());
            
            if (p1 != null && p2 != null) {
                // Definir color según el flujo (Requerimiento de código de colores)
                if (t.getFlujo() == 0) {
                    g2.setColor(Color.LIGHT_GRAY);
                } else if (t.getFlujo() >= t.getCapacidad()) {
                    g2.setColor(Color.RED); // Cuello de botella / Presión máxima
                } else {
                    g2.setColor(new Color(41, 128, 185)); // Azul agua en movimiento
                }
                
                g2.setStroke(new BasicStroke(3)); // Grosor de la línea
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                
                g2.setColor(Color.BLACK);
                int medX = (p1.x + p2.x) / 2;
                int medY = (p1.y + p2.y) / 2;
                g2.drawString(t.getFlujo() + "/" + t.getCapacidad(), medX, medY - 5);
            }
        }

        int radio = 30;
        for (Nodo n : nodos) {
            Point p = posiciones.get(n);
            if (p != null) {
                // Asignar color por tipo de nodo
                if (n.getTipo() == TipoNodo.FUENTE) {
                    g2.setColor(new Color(39, 174, 96)); // Verde Embalse
                } else if (n.getTipo() == TipoNodo.SUMIDERO) {
                    g2.setColor(new Color(230, 126, 34)); // Naranja Barrio
                } else {
                    g2.setColor(new Color(149, 165, 166)); // Gris Estación
                }
                
                g2.fillOval(p.x - radio/2, p.y - radio/2, radio, radio);
                g2.setColor(Color.DARK_GRAY);
                g2.drawOval(p.x - radio/2, p.y - radio/2, radio, radio);
                
                // Dibujar el nombre y el ID abajo del círculo
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(n.getNombre() + " (" + n.getId() + ")", p.x - radio, p.y + radio);
            }
        }
    }
}