package modelo;

public class Tuberia implements java.io.Serializable{
    private Nodo origen;
    private Nodo destino;
    private double capacidad;
    private double flujo;

    public Tuberia(Nodo origen, Nodo destino, double capacidad) {
        this.origen = origen;
        this.destino = destino;
        this.capacidad = capacidad;
        this.flujo = 0.0; // Inicia vacía
    }

    public double getCapacidadResidual() {
        return capacidad - flujo;
    }

    public Nodo getOrigen() { return origen; }
    public Nodo getDestino() { return destino; }
    public double getCapacidad() { return capacidad; }
    public double getFlujo() { return flujo; }
    public void setFlujo(double flujo) { this.flujo = flujo; }
    public void setCapacidad(double capacidad) { this.capacidad = capacidad; }
}