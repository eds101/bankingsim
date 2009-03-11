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
		
		sim.prevFedInterestRate = sim.fedInterestRate;
		
		sim.fedInterestRate = .01+sim.rng.nextDouble()*.05;
		
		//ensure that money supply never grows more than +50% or less than -50% by distributing or collecting currency from consumers and banks. 
		double oldMoneySupply = sim.moneySupply;
		double newMoneySupply = sim.totalCurrency+sim.totalDeposits;
		sim.moneySupplyGrowth = newMoneySupply/oldMoneySupply-1;
		if(sim.moneySupplyGrowth>.5) {
			double excessMoneySupply = newMoneySupply-oldMoneySupply*1.5;
			for (int i=0;i<sim.currency.length;i++) {
				sim.totalCurrency += sim.currency[i];	
			}
			for(int i=0;i<sim.currency.length;i++) {
				sim.currency[i]*=(sim.totalCurrency-excessMoneySupply)/sim.totalCurrency;
			}
		} else if (sim.moneySupplyGrowth<-.5) {
			double moneySupplyWant = oldMoneySupply*.5-newMoneySupply;
			for (int i=0;i<sim.currency.length;i++) {
				sim.totalCurrency += sim.currency[i];	
			}
			for(int i=0;i<sim.currency.length;i++) {
				sim.currency[i]*=(sim.totalCurrency+moneySupplyWant)/sim.totalCurrency;
			}
		}
		
	}
	
	public void askForLoan(int bank, double amt) {
		sim.fedLoans[bank]+=amt;
	}

}
