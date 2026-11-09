package model;

public final class Coordenada {
    private final int x;
    private final int y;

    public Coordenada(int x, int y){
        this.x = x;
        this.y = y;
    }

    public float distancia(Coordenada c2){
        return (float)Math.sqrt(Math.pow((x - c2.getX()), 2) + Math.pow((y - c2.getY()), 2));
    }

    /** @return cantidad de pasos en grilla (sin diagonales); no modifica nada */
    public int distanciaManhattan(Coordenada otra) {
        return Math.abs(x - otra.x) + Math.abs(y - otra.y);
    }

    /** @return una coordenada nueva desplazada (dx, dy); esta no cambia */
    public Coordenada desplazar(int dx, int dy) {
        return new Coordenada(x + dx, y + dy);
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    @Override 
    public boolean equals(Object o){
        if(this == o) return true;
        if(o instanceof Coordenada){
            Coordenada c2 = (Coordenada)o;
            return (this.x == c2.getX()) && (this.y == c2.getY()); 
        }
        else
            return false;
    }
    @Override 
    public int hashCode(){
        return 31*x + y;
    }
    @Override 
    public String toString(){
        return "(" + x + ", " + y + ")";
    }

}
