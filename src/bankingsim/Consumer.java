package bankingsim;

public class Consumer {
	
	private BankingSim sim;
	
	public Consumer(BankingSim sim) {
		this.sim = sim;
	}
	
	public void run() {
		//gdpGrowthRate fluctuates
		sim.gdpGrowthRate = (sim.rng.nextDouble()*13-3)/100;
		
		//grow gdp by gdpGrowthRate
		sim.gdp=sim.gdp*(1+sim.gdpGrowthRate);
		
		sim.avgConsumerDeposits = sim.gdp/sim.numConsumers*(sim.fedInterestRate+sim.bidAskRate-.1+sim.rng.nextDouble()*.25); //savings rate is directly related to interest rate for consumers
		
		//Calculate desired currency for each consumer
		double desiredCurrency[] = new double[sim.numConsumers];
		double totalCur = 0;
		for (int c= 0;c<sim.numConsumers;c++ ) {
			sim.currency[sim.numBanks+c]+=(sim.gdp-sim.gdp/(1+sim.gdpGrowthRate))/sim.numConsumers;
			desiredCurrency[c]=sim.gdp/sim.numConsumers*Math.pow(Math.E,sim.rng.nextGaussian());
			totalCur+=desiredCurrency[c];
		}
		for(int c=0;c<sim.numConsumers;c++) {
			desiredCurrency[c] /= totalCur/(sim.gdp-sim.numConsumers*sim.avgConsumerDeposits);
		}
		
		
		
		//pay back consumer loans with interest = prevFedFundsRate + prevBidAskRate
		for(int c=0;c<sim.numConsumers;c++) {
			for (int b=0;b<sim.numBanks;b++) { 
					if (sim.loans[b][sim.numBanks+c] <= sim.currency[sim.numBanks+c])
						payBackLoan(c,b,sim.loans[b][sim.numBanks+c]);
					else {
						sim.totalAmountDefaulted+=sim.loans[b][sim.numBanks+c]-sim.currency[sim.numBanks+c];
						payBackLoan(c,b,sim.currency[sim.numBanks+c]);
					}
			}
		}
		
		//given desired currency, consumer either wants to withdraw/borrow or save
		for(int c =0;c<sim.numConsumers;c++) {
			if(desiredCurrency[c]<sim.currency[sim.numBanks+c]) //if desire less currency
				depositInBank(c,sim.rng.nextInt(sim.numBanks),sim.currency[sim.numBanks+c]-desiredCurrency[c]); // then deposit the difference at some random bank
		}
		for(int c =0;c<sim.numConsumers;c++) {
			if(desiredCurrency[c]>sim.currency[sim.numBanks+c]) { // consumer desires more currency
				if(sim.consumerDeposits[c] > 0) { //if consumer has some deposits in some bank
					for(int b=0;b<sim.numBanks;b++) { //then first try to increase currency by withdrawing deposits
						// TODO: make it so that person can only withdraw if bank has reserves to give them.
						if (sim.deposits[c][b]>=desiredCurrency[c]-sim.currency[sim.numBanks+c]) {
							withdrawFromBank(c,b,desiredCurrency[c]-sim.currency[sim.numBanks+c]); 
							break;
						} else 
							withdrawFromBank(c,b,sim.deposits[c][b]);
					}
				}
				if(desiredCurrency[c]>sim.currency[sim.numBanks+c]) { //if still want more currency
					for(int b=0;b<sim.numBanks;b++) {  //then try to get the rest via loans
						if(sim.bankDeposits[b]*(1-sim.rr)+sim.currency[b]>sim.bankLoansOut[b]) {
							if (sim.bankDeposits[b]*(1-sim.rr)+sim.currency[b]-sim.bankLoansOut[b] >= desiredCurrency[c]-sim.currency[sim.numBanks+c]) { 
								takeOutLoan(c,b,desiredCurrency[c]-sim.currency[sim.numBanks+c]);
								break;
							}
							else
								takeOutLoan(c,b,sim.bankDeposits[b]*(1-sim.rr)+sim.currency[b]-sim.bankLoansOut[b]);
						}
					}
				}
				/*if(desiredCurrency[c]>currency[numBanks+c]) 
					everFailed[numBanks+c]=true; */
			}
		}
	}
	
	public void depositInBank(int c, int b, double amt) {
		sim.deposits[c][b]+=amt;
		sim.currency[sim.numBanks+c]-=amt;
		sim.consumerDeposits[c]+=amt;
		sim.bankDeposits[b]+=amt;
	}
	
	public void withdrawFromBank(int c, int b, double amt) {
		sim.deposits[c][b]-=amt;
		sim.currency[sim.numBanks+c]+=amt;
		sim.consumerDeposits[c]-=amt;
		sim.bankDeposits[b]-=amt;
	}
	
	public void payBackLoan(int c, int b, double amt) {
		sim.loans[b][sim.numBanks+c]-=amt;
		sim.currency[sim.numBanks+c]-=amt;
		sim.bankLoansOut[b]-=amt;
		sim.consumerLoans[c]-=amt;
	}
	
	public void takeOutLoan(int c, int b, double amt) {
		sim.loans[b][sim.numBanks+c]+=amt;
		sim.currency[sim.numBanks+c]+=amt;
		sim.bankLoansOut[b]+=amt;
		sim.consumerLoans[c]+=amt;
	}
}
