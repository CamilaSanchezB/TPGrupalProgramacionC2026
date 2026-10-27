public class NaveCargadora extends Thread{
    
    private PlataFormaDeCarga plat;
    private String nombre;

    public NaveCargadora(String nombre,PlataFormaDeCarga plat){
        this.plat=plat;
        this.nombre=nombre;
        
    }

    @Override 
    public void run(){
        while (true) {
            try{
                Thread.sleep((long) (Math.random() * 2000));
                plat.insertar(this.nombre);
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
