package bankingsim;


public class Fed {
		
	private BankingSim sim;
	
	public Fed(BankingSim sim){
		this.sim = sim;
	}
	
	public void run(){
		sim.rr += (sim.rng.nextDouble()-.5)*.01; //changes between -.5% and +.5%
		if(sim.rr<0) {
			sim.rr = 0;
		} else if (sim.rr > 1) {
			sim.rr = 1;
		}
	}

}
