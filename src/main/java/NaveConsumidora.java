public class NaveConsumidora implements Runnable {
	private final PlataFormaDeCarga p;
	
	public NaveConsumidora(PlataFormaDeCarga p) {
		this.p=p;
	}
	
	@Override
	public void run() {
		try {
			p.sacar();
			Thread.sleep(500);
			
		}catch(InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		
		
	}

}