package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {
    
    // Componentes para la gestión de Nodos
    private JTextField txtIdNodo, txtNombreNodo;
    private JComboBox<String> cbTipoNodo;
    private JButton btnAgregarNodo;
    private JButton btnEliminarNodo;
    private JButton btnModificarNodo;

    private JTextField txtNodoOrigen, txtNodoDestino, txtCapacidad;
    private JButton btnAgregarTuberia;
    private JButton btnEliminarTuberia;
    private JButton btnModificarTuberia;
    
    private JTextField txtFuenteSim, txtSumideroSim;
    private JButton btnIniciarSimulacion;

    private JTextArea txtReporte;
    
    private LienzoRed panelLienzo;

    private JButton btnGuardarRed, btnCargarRed;

    public VentanaPrincipal() {
        setTitle("Sistema de Distribución de Agua Fluvial");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelControl = new JPanel();
        panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
        panelControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelControl.setPreferredSize(new Dimension(320, 700));

        configurarSeccionNodos(panelControl);
        panelControl.add(Box.createRigidArea(new Dimension(0, 15))); // Espaciador
        configurarSeccionTuberias(panelControl);
        panelControl.add(Box.createRigidArea(new Dimension(0, 15)));
        configurarSeccionSimulacion(panelControl);
        panelControl.add(Box.createRigidArea(new Dimension(0, 15)));
        configurarSeccionReporte(panelControl);
        panelControl.add(Box.createRigidArea(new Dimension(0, 15)));
        configurarSeccionPersistencia(panelControl);

        panelLienzo = new LienzoRed();
        panelLienzo.setBorder(BorderFactory.createTitledBorder("Vista de la Red Hídrica"));
        
        add(panelControl, BorderLayout.WEST);
        add(panelLienzo, BorderLayout.CENTER);
    }

    private void configurarSeccionNodos(JPanel contenedor) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("1. Gestionar Puntos"));

        txtIdNodo = new JTextField();
        txtNombreNodo = new JTextField();
        cbTipoNodo = new JComboBox<>(new String[]{"FUENTE", "INTERMEDIO", "SUMIDERO"});

        btnAgregarNodo = new JButton("Agregar");
        btnEliminarNodo = new JButton("Eliminar");
        btnModificarNodo = new JButton("Modificar");

        btnEliminarNodo.setBackground(new Color(192, 57, 43));
        btnEliminarNodo.setForeground(Color.WHITE);

        panel.add(new JLabel("ID Nodo:"));
        panel.add(txtIdNodo);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombreNodo);
        panel.add(new JLabel("Tipo:"));
        panel.add(cbTipoNodo);

        panel.add(btnAgregarNodo);
        panel.add(btnEliminarNodo);

        // Modificar en la base
        panel.add(new JLabel(""));
        panel.add(btnModificarNodo);

        contenedor.add(panel);
    }

    private void configurarSeccionTuberias(JPanel contenedor) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("2. Gestionar Tuberías"));

        txtNodoOrigen = new JTextField();
        txtNodoDestino = new JTextField();
        txtCapacidad = new JTextField();

        btnAgregarTuberia = new JButton("Conectar");
        btnEliminarTuberia = new JButton("Eliminar");
        btnModificarTuberia = new JButton("Modificar Cap.");

        btnEliminarTuberia.setBackground(new Color(192, 57, 43));
        btnEliminarTuberia.setForeground(Color.WHITE);

        panel.add(new JLabel("ID Origen:"));
        panel.add(txtNodoOrigen);
        panel.add(new JLabel("ID Destino:"));
        panel.add(txtNodoDestino);
        panel.add(new JLabel("Capacidad (L/s):"));
        panel.add(txtCapacidad);

        panel.add(btnAgregarTuberia);
        panel.add(btnEliminarTuberia);

        panel.add(new JLabel(""));
        panel.add(btnModificarTuberia);

        contenedor.add(panel);
    }

    private void configurarSeccionSimulacion(JPanel contenedor) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("3. Optimización"));

        txtFuenteSim = new JTextField();
        txtSumideroSim = new JTextField();
        btnIniciarSimulacion = new JButton("Simular Flujo");
        btnIniciarSimulacion.setBackground(new Color(41, 128, 185));
        btnIniciarSimulacion.setForeground(Color.WHITE);

        panel.add(new JLabel("ID Embalse:"));
        panel.add(txtFuenteSim);
        panel.add(new JLabel("ID Barrio:"));
        panel.add(txtSumideroSim);
        panel.add(new JLabel(""));
        panel.add(btnIniciarSimulacion);

        contenedor.add(panel);
    }

    private void configurarSeccionReporte(JPanel contenedor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resultados del Reporte"));

        txtReporte = new JTextArea(8, 20);
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtReporte);

        panel.add(scroll, BorderLayout.CENTER);
        contenedor.add(panel);
    }

    private void configurarSeccionPersistencia(JPanel contenedor) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("4. Archivo"));

        btnGuardarRed = new JButton("Guardar Red");
        btnCargarRed = new JButton("Cargar Red");

        panel.add(btnGuardarRed);
        panel.add(btnCargarRed);

        contenedor.add(panel);
    }

    public void escucharBotones(ActionListener escucharNodo, ActionListener escucharTuberia, 
                            ActionListener escucharSim, ActionListener escucharDelTub, 
                            ActionListener escucharModTub, ActionListener escucharDelNodo, 
                            ActionListener escucharModNodo, ActionListener escucharGuardar, 
                            ActionListener escucharCargar) {
        btnAgregarNodo.addActionListener(escucharNodo);
        btnAgregarTuberia.addActionListener(escucharTuberia);
        btnIniciarSimulacion.addActionListener(escucharSim);
        btnEliminarTuberia.addActionListener(escucharDelTub);
        btnModificarTuberia.addActionListener(escucharModTub);
        btnEliminarNodo.addActionListener(escucharDelNodo);
        btnModificarNodo.addActionListener(escucharModNodo);
        btnGuardarRed.addActionListener(escucharGuardar);
        btnCargarRed.addActionListener(escucharCargar);
    }

    public String getIdNodo() { return txtIdNodo.getText().trim(); }
    public String getNombreNodo() { return txtNombreNodo.getText().trim(); }
    public String getTipoNodo() { return cbTipoNodo.getSelectedItem().toString(); }
    
    public String getOrigenTuberia() { return txtNodoOrigen.getText().trim(); }
    public String getDestinoTuberia() { return txtNodoDestino.getText().trim(); }
    public String getCapacidadTuberia() { return txtCapacidad.getText().trim(); }
    
    public String getFuenteSim() { return txtFuenteSim.getText().trim(); }
    public String getSumideroSim() { return txtSumideroSim.getText().trim(); }

    public void mostrarReporte(String texto) {
        txtReporte.setText(texto);
    }

    public void limpiarFormularios() {
        txtIdNodo.setText(""); txtNombreNodo.setText("");
        txtNodoOrigen.setText(""); txtNodoDestino.setText(""); txtCapacidad.setText("");
    }

    public LienzoRed getLienzo() {
        return panelLienzo;
    }
}