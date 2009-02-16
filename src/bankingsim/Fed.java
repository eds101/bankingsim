package bankingsim;


public class Fed extends BankingSim {
		
	public Fed(){
		
	}
	
	public void run(){
		rr = 0.01*rng.nextInt(11);
	}

}
