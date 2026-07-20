package modelo;

import java.util.*;

public class RedDistribucion implements java.io.Serializable{
    private List<Nodo> nodos;
    private List<Tuberia> tuberias;

    public RedDistribucion() {
        this.nodos = new ArrayList<>();
        this.tuberias = new ArrayList<>();
    }

    public void agregarNodo(Nodo n) { nodos.add(n); }
    public void agregarTuberia(Tuberia t) { tuberias.add(t); }

    public List<Nodo> getNodos() { return nodos; }
    public List<Tuberia> getTuberias() { return tuberias; }
    
    public double calcularFlujoMaximo(Nodo fuente, Nodo sumidero) {
        for (Tuberia t : tuberias) {
            t.setFlujo(0);
        }

        double flujoMaximo = 0;
        
        Map<Nodo, Tuberia> caminoPadre = new HashMap<>();

        while (buscarCaminoBFS(fuente, sumidero, caminoPadre)) {
            
            double flujoCamino = Double.MAX_VALUE;
            Nodo actual = sumidero;
            
            while (actual != fuente) {
                Tuberia t = caminoPadre.get(actual);
                flujoCamino = Math.min(flujoCamino, t.getCapacidadResidual());
                actual = t.getOrigen();
            }

            actual = sumidero;
            while (actual != fuente) {
                Tuberia t = caminoPadre.get(actual);
                t.setFlujo(t.getFlujo() + flujoCamino);
                actual = t.getOrigen();
            }

            flujoMaximo += flujoCamino;
            caminoPadre.clear();
        }

        return flujoMaximo;
    }

    private boolean buscarCaminoBFS(Nodo fuente, Nodo sumidero, Map<Nodo, Tuberia> caminoPadre) {
        Set<Nodo> visitados = new HashSet<>();
        Queue<Nodo> cola = new LinkedList<>();

        cola.add(fuente);
        visitados.add(fuente);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();

            if (actual == sumidero) {
                return true;
            }

            for (Tuberia t : tuberias) {
                if (t.getOrigen().equals(actual)) {
                    Nodo vecino = t.getDestino();
                    
                    if (!visitados.contains(vecino) && t.getCapacidadResidual() > 0) {
                        cola.add(vecino);
                        visitados.add(vecino);
                        caminoPadre.put(vecino, t);
                    }
                }
            }
        }
        return false;
    }

    public boolean eliminarTuberia(Nodo origen, Nodo destino) {
        for (Tuberia t : tuberias) {
            if (t.getOrigen().equals(origen) && t.getDestino().equals(destino)) {
                tuberias.remove(t);
                return true;
            }
        }
        return false;
    }

    public boolean modificarCapacidadTuberia(Nodo origen, Nodo destino, double nuevaCapacidad) {
        for (Tuberia t : tuberias) {
            if (t.getOrigen().equals(origen) && t.getDestino().equals(destino)) {
                t.setFlujo(0);
                t.setCapacidad(nuevaCapacidad);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarNodo(String id) {
        Nodo nodoAEliminar = null;
        for (Nodo n : nodos) {
            if (n.getId().equalsIgnoreCase(id)) {
                nodoAEliminar = n;
                break;
            }
        }

        if (nodoAEliminar != null) {
            nodos.remove(nodoAEliminar);
            
            Nodo finalNodoAEliminar = nodoAEliminar;
            tuberias.removeIf(t -> t.getOrigen().equals(finalNodoAEliminar) || t.getDestino().equals(finalNodoAEliminar));
            
            for (Tuberia t : tuberias) t.setFlujo(0);
            
            return true;
        }
        return false;
    }

    public boolean modificarNodo(String id, String nuevoNombre, TipoNodo nuevoTipo) {
        for (Nodo n : nodos) {
            if (n.getId().equalsIgnoreCase(id)) {
                if (n.getTipo() != nuevoTipo) {
                    for (Tuberia t : tuberias) t.setFlujo(0);
                }
                n.setNombre(nuevoNombre);
                n.setTipo(nuevoTipo);
                return true;
            }
        }
        return false;
    }
}