public class NaveConsumidora extends Thread {

private String nombre;
private PlataFormaDeCarga plat;

    public NaveConsumidora(String nombre,PlataFormaDeCarga plat){
        this.nombre=nombre;
        this.plat=plat;
    }

    @Override
    public void run(){
        while(true){
            try{
                plat.consumir(this.nombre);
                Thread.sleep( (long)(Math.random()*3000) );
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    

}
