package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import modelo.*;
import vista.VentanaPrincipal;

public class RedController {
    private RedDistribucion modelo;
    private VentanaPrincipal vista;

    public RedController(RedDistribucion modelo, VentanaPrincipal vista) {
        this.modelo = modelo;
        this.vista = vista;

        this.vista.escucharBotones(
            new ListenerAgregarNodo(),
            new ListenerAgregarTuberia(),
            new ListenerSimularFlujo(),
            new ListenerEliminarTuberia(),
            new ListenerModificarTuberia(),
            new ListenerEliminarNodo(),
            new ListenerModificarNodo(),
            new ListenerGuardarRed(),
            new ListenerCargarRed()
        );
    }

    private class ListenerAgregarNodo implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = vista.getIdNodo();
            String nombre = vista.getNombreNodo();
            String tipoStr = vista.getTipoNodo();

            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Por favor, llene todos los campos del Nodo.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Nodo n : modelo.getNodos()) {
                if (n.getId().equalsIgnoreCase(id)) {
                    JOptionPane.showMessageDialog(vista, "El ID del nodo ya existe.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            TipoNodo tipo = TipoNodo.valueOf(tipoStr);
            Nodo nuevoNodo = new Nodo(id, nombre, tipo);
            
            modelo.agregarNodo(nuevoNodo);
            
            vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
            vista.limpiarFormularios();
            vista.mostrarReporte("Nodo registrado con éxito: " + nombre);
        }
    }

    private class ListenerAgregarTuberia implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String origenId = vista.getOrigenTuberia();
            String destinoId = vista.getDestinoTuberia();
            String capacidadStr = vista.getCapacidadTuberia();

            if (origenId.isEmpty() || destinoId.isEmpty() || capacidadStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Por favor, llene todos los campos de la Tubería.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Nodo origen = buscarNodoPorId(origenId);
            Nodo destino = buscarNodoPorId(destinoId);

            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(vista, "El ID de origen o destino no existe.", "Nodos No Encontrados", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (origen.equals(destino)) {
                JOptionPane.showMessageDialog(vista, "Una tubería no puede conectarse al mismo nodo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double capacidad = Double.parseDouble(capacidadStr);
                if (capacidad <= 0) {
                    JOptionPane.showMessageDialog(vista, "La capacidad debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Tuberia nuevaTuberia = new Tuberia(origen, destino, capacidad);
                modelo.agregarTuberia(nuevaTuberia);

                vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
                vista.limpiarFormularios();
                vista.mostrarReporte("Tubería conectada: " + origen.getNombre() + " -> " + destino.getNombre());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "La capacidad debe ser un valor numérico válido.", "Error de Tipo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ListenerSimularFlujo implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fuenteId = vista.getFuenteSim();
            String sumideroId = vista.getSumideroSim();

            if (fuenteId.isEmpty() && sumideroId.isEmpty()) {
                ejecutarSimulacionGeneral();
                return;
            }

            if (fuenteId.isEmpty() || sumideroId.isEmpty()) {
                JOptionPane.showMessageDialog(vista, 
                    "Para una simulación específica, ingrese ambos IDs.\nPara una simulación general, deje ambos campos vacíos.", 
                    "Campos Incompletos", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Nodo fuente = buscarNodoPorId(fuenteId);
            Nodo sumidero = buscarNodoPorId(sumideroId);

            if (fuente == null || sumidero == null) {
                JOptionPane.showMessageDialog(vista, "Los IDs de simulación indicados no existen.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fuente.getTipo() != TipoNodo.FUENTE || sumidero.getTipo() != TipoNodo.SUMIDERO) {
                JOptionPane.showMessageDialog(vista, "El origen debe ser tipo FUENTE y el destino tipo SUMIDERO.", "Validación de Roles", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double flujoMaximo = modelo.calcularFlujoMaximo(fuente, sumidero);
            vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
            
            mostrarReporteEspecifico(fuente, sumidero, flujoMaximo);
        }

        private void ejecutarSimulacionGeneral() {
            java.util.List<Nodo> fuentes = new java.util.ArrayList<>();
            java.util.List<Nodo> sumideros = new java.util.ArrayList<>();

            for (Nodo n : modelo.getNodos()) {
                if (n.getTipo() == TipoNodo.FUENTE) fuentes.add(n);
                if (n.getTipo() == TipoNodo.SUMIDERO) sumideros.add(n);
            }

            if (fuentes.isEmpty() || sumideros.isEmpty()) {
                JOptionPane.showMessageDialog(vista, 
                    "Para una simulación general se necesita al menos un nodo FUENTE y un nodo SUMIDERO en el mapa.", 
                    "Faltan Elementos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder reporte = new StringBuilder();
            reporte.append("=====================================\n");
            reporte.append("   REPORTE DE SIMULACIÓN GENERAL\n");
            reporte.append("=====================================\n\n");

            for (Nodo f : fuentes) {
                for (Nodo s : sumideros) {
                    for (Tuberia t : modelo.getTuberias()) t.setFlujo(0);
                    
                    double flujo = modelo.calcularFlujoMaximo(f, s);
                    
                    reporte.append(String.format("• Desde: %s (%s)\n  Hasta: %s (%s)\n", 
                        f.getNombre(), f.getId(), s.getNombre(), s.getId()));
                    reporte.append(String.format("  -> Flujo Óptimo Entregado: %.1f L/s\n\n", flujo));
                }
            }

            vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());

            reporte.append("Estado de Capacidad de Tuberías:\n");
            for (Tuberia t : modelo.getTuberias()) {
                String estado = (t.getFlujo() >= t.getCapacidad() && t.getCapacidad() > 0) ? "CRÍTICO (CUELLO DE BOTELLA)" : "OK";
                reporte.append(String.format("- Tubería [%s -> %s]: %.1f/%.1f L/s (%s)\n",
                        t.getOrigen().getId(), t.getDestino().getId(), t.getFlujo(), t.getCapacidad(), estado));
            }

            gestionarVentanaYGuardado(reporte.toString());
        }

        private void mostrarReporteEspecifico(Nodo f, Nodo s, double flujoMaximo) {
            StringBuilder reporte = new StringBuilder();
            reporte.append("===============================\n");
            reporte.append("    RESULTADO DE RED ESPECÍFICA\n");
            reporte.append("===============================\n");
            reporte.append(String.format("Desde: %s -> Hasta: %s\n", f.getNombre(), s.getNombre()));
            reporte.append(String.format("Flujo Máximo: %.1f L/s\n\n", flujoMaximo));
            reporte.append("Estado de Tuberías:\n");

            for (Tuberia t : modelo.getTuberias()) {
                String estado = (t.getFlujo() >= t.getCapacidad() && t.getCapacidad() > 0) ? "CRÍTICO (CUELLO DE BOTELLA)" : "OK";
                reporte.append(String.format("- [%s] -> [%s]: %.1f/%.1f L/s (%s)\n",
                        t.getOrigen().getId(), t.getDestino().getId(), t.getFlujo(), t.getCapacidad(), estado));
            }
            gestionarVentanaYGuardado(reporte.toString());
        }
    }

    private class ListenerEliminarTuberia implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String origenId = vista.getOrigenTuberia();
            String destinoId = vista.getDestinoTuberia();

            if (origenId.isEmpty() || destinoId.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Indique ID Origen e ID Destino para eliminar la tubería.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Nodo origen = buscarNodoPorId(origenId);
            Nodo destino = buscarNodoPorId(destinoId);

            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(vista, "Los IDs especificados no existen.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean exito = modelo.eliminarTuberia(origen, destino);

            if (exito) {
                for (Tuberia t : modelo.getTuberias()) t.setFlujo(0);
                
                vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
                vista.limpiarFormularios();
                vista.mostrarReporte("Tubería eliminada con éxito: " + origen.getNombre() + " -> " + destino.getNombre());
            } else {
                JOptionPane.showMessageDialog(vista, "No existe ninguna tubería directa entre esos dos puntos.", "No Encontrada", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ListenerModificarTuberia implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String origenId = vista.getOrigenTuberia();
            String destinoId = vista.getDestinoTuberia();
            String capacidadStr = vista.getCapacidadTuberia();

            if (origenId.isEmpty() || destinoId.isEmpty() || capacidadStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Indique Origen, Destino y la nueva Capacidad.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Nodo origen = buscarNodoPorId(origenId);
            Nodo destino = buscarNodoPorId(destinoId);

            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(vista, "Los IDs especificados no existen.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double nuevaCapacidad = Double.parseDouble(capacidadStr);
                if (nuevaCapacidad <= 0) {
                    JOptionPane.showMessageDialog(vista, "La capacidad debe ser positiva.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean exito = modelo.modificarCapacidadTuberia(origen, destino, nuevaCapacidad);

                if (exito) {
                    for (Tuberia t : modelo.getTuberias()) t.setFlujo(0);
                    
                    vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
                    vista.limpiarFormularios();
                    vista.mostrarReporte("Capacidad modificada a " + nuevaCapacidad + " L/s para: " + origen.getNombre() + " -> " + destino.getNombre());
                } else {
                    JOptionPane.showMessageDialog(vista, "No existe una tubería que conectar entre esos nodos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "La capacidad debe ser un número válido.", "Error de Tipo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ListenerEliminarNodo implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = vista.getIdNodo();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Ingrese el ID del nodo que desea eliminar.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(vista, 
                "¿Está seguro de eliminar el nodo? Se borrarán también todas las tuberías conectadas a él.", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                
            if (confirmacion != JOptionPane.YES_OPTION) return;

            boolean exito = modelo.eliminarNodo(id);

            if (exito) {
                vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
                vista.limpiarFormularios();
                vista.mostrarReporte("Nodo con ID [" + id + "] y sus conexiones asociadas han sido eliminados.");
            } else {
                JOptionPane.showMessageDialog(vista, "No se encontró ningún nodo con el ID especificado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ListenerModificarNodo implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = vista.getIdNodo();
            String nuevoNombre = vista.getNombreNodo();
            String tipoStr = vista.getTipoNodo();

            if (id.isEmpty() || nuevoNombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Indique el ID del nodo y los nuevos datos (Nombre/Tipo) a aplicar.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            TipoNodo nuevoTipo = TipoNodo.valueOf(tipoStr);
            boolean exito = modelo.modificarNodo(id, nuevoNombre, nuevoTipo);

            if (exito) {
                vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
                vista.limpiarFormularios();
                vista.mostrarReporte("Nodo [" + id + "] modificado con éxito a: " + nuevoNombre + " (" + tipoStr + ")");
            } else {
                JOptionPane.showMessageDialog(vista, "No se encontró ningún nodo con el ID especificado para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ListenerGuardarRed implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (modelo.getNodos().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "No hay datos en la red para guardar.", "Mapa Vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser selector = new JFileChooser();
            selector.setDialogTitle("Guardar Estado de la Red Hídrica");
            selector.setSelectedFile(new java.io.File("mi_red_hidrica.dat"));

            int resultado = selector.showSaveDialog(vista);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = selector.getSelectedFile();
                
                String ruta = archivo.getAbsolutePath();
                if (!ruta.toLowerCase().endsWith(".dat")) {
                    archivo = new java.io.File(ruta + ".dat");
                }

                try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(archivo))) {
                    oos.writeObject(modelo);
                    vista.mostrarReporte("Red guardada exitosamente en:\n" + archivo.getName());
                    JOptionPane.showMessageDialog(vista, "Red guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(vista, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class ListenerCargarRed implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser selector = new JFileChooser();
            selector.setDialogTitle("Cargar Estado de la Red Hídrica");

            int resultado = selector.showOpenDialog(vista);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = selector.getSelectedFile();

                try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(archivo))) {
                    
                    modelo = (RedDistribucion) ois.readObject();
                    
                    vista.getLienzo().actualizarRed(modelo.getNodos(), modelo.getTuberias());
                    
                    vista.limpiarFormularios();
                    vista.mostrarReporte("Red hídrica restaurada con éxito desde:\n" + archivo.getName());
                    JOptionPane.showMessageDialog(vista, "Red cargada correctamente al sistema.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vista, "Error al cargar el archivo. Asegúrese de que sea un respaldo válido: " + ex.getMessage(), "Error de Lectura", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private Nodo buscarNodoPorId(String id) {
        for (Nodo n : modelo.getNodos()) {
            if (n.getId().equalsIgnoreCase(id)) {
                return n;
            }
        }
        return null;
    }

    private void gestionarVentanaYGuardado(String reporteTexto) {
        JTextArea areaTexto = new JTextArea(20, 45);
        areaTexto.setText(reporteTexto);
        areaTexto.setEditable(false);
        areaTexto.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(areaTexto);

        Object[] opciones = {"Guardar en archivo TXT", "Cerrar"};

        int seleccion = JOptionPane.showOptionDialog(
            vista, 
            scroll, 
            "Reporte de Optimización Fluvial", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.INFORMATION_MESSAGE, 
            null, 
            opciones, 
            opciones[0]
        );

        if (seleccion == JOptionPane.YES_OPTION) {
            JFileChooser selectorArchivo = new JFileChooser();
            selectorArchivo.setDialogTitle("Guardar Reporte de Red Hídrica");
                
            selectorArchivo.setSelectedFile(new java.io.File("Reporte_Flujo_Maximo.txt"));

            int resultado = selectorArchivo.showSaveDialog(vista);

            if (resultado == JFileChooser.APPROVE_OPTION) {
                java.io.File archivoDestino = selectorArchivo.getSelectedFile();
                    
                String ruta = archivoDestino.getAbsolutePath();
                if (!ruta.toLowerCase().endsWith(".txt")) {
                    archivoDestino = new java.io.File(ruta + ".txt");
                }

                try (java.io.PrintWriter escritor = new java.io.PrintWriter(new java.io.FileWriter(archivoDestino))) {
                    escritor.print(reporteTexto);
                    JOptionPane.showMessageDialog(vista, "Reporte exportado exitosamente en:\n" + archivoDestino.getAbsolutePath(), "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(vista, "Error al intentar escribir el archivo: " + ex.getMessage(), "Error de Disco", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}