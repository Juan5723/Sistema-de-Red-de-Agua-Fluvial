package modelo;

public class Nodo implements java.io.Serializable{
    private String id;
    private String nombre;
    private TipoNodo tipo;

    public Nodo(String id, String nombre, TipoNodo tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public TipoNodo getTipo() { return tipo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipo(TipoNodo tipo) { this.tipo = tipo; }    
    
    @Override
    public String toString() {
        return nombre + " (" + tipo + ")";
    }
}