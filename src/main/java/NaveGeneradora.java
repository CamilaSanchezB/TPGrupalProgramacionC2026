public class NaveGeneradora implements Runnable {
	private final PlataFormaDeCarga b;

	
	public NaveGeneradora (PlataFormaDeCarga b) {
		this.b=b;
	}
	
	@Override
	public void run() {
		String nombre = Thread.currentThread().getName();
		try {
			b.depositar(new Bateria());
			//Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Thread.currentThread().interrupt();
		}
		
		
	}

}
