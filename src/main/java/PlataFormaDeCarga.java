public class PlataFormaDeCarga {
    private int cantAct = 0;
    private int capacidadMax = 5;

    public synchronized void insertar(String nombreGeneradora){
        while(cantAct > capacidadMax){
            try{
                wait();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        cantAct++;
        System.out.println("bateria agregada");
        notifyAll();
    }

    public synchronized void consumir(String nombreConsumidora){
        while(cantAct==0){
            try{
                wait();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }

        }
        cantAct--;
        System.out.println("bateria consumida");
        notifyAll();
    }

}
