package bankingsim;

public class Consumer {
	
	private BankingSim sim;
	
	public Consumer(BankingSim sim) {
		this.sim = sim;
	}
	
	public void run() {
		
		//Calculate desired currency for each consumer
		double desiredCurrency[] = new double[sim.numConsumers];
		double totalCur = 0;
		for (int c= 0;c<sim.numConsumers;c++ ) {
			desiredCurrency[c]=sim.initBase/sim.numConsumers*Math.pow(Math.E,sim.rng.nextGaussian());
			totalCur+=desiredCurrency[c];
		}
		for(int c=0;c<sim.numConsumers;c++) {
			desiredCurrency[c] /= totalCur/(sim.initBase-sim.numConsumers*sim.avgConsumerDeposits);
		}
		
		//given desired currency, consumer either wants to withdraw/borrow or save/pay back loans
		for(int c =0;c<sim.numConsumers;c++) {
			if(desiredCurrency[c]<sim.currency[sim.numBanks+c]) { // consumer desires less currency
				if (sim.consumerLoans[c]>0) { // if consumer has some loans taken out
					for (int b=0;b<sim.numBanks;b++) { // then first try to decrease currency by paying back loans
						if (sim.loans[b][sim.numBanks+c]>=sim.currency[sim.numBanks+c]-desiredCurrency[c]) {
							payBackLoan(c,b,sim.currency[sim.numBanks+c]-desiredCurrency[c]);
							break;
						} else {
							payBackLoan(c,b,sim.loans[b][sim.numBanks+c]);
						}
					}
				}
				if(desiredCurrency[c]<sim.currency[sim.numBanks+c]) //if still desire less currency
					depositInBank(c,sim.rng.nextInt(sim.numBanks),sim.currency[sim.numBanks+c]-desiredCurrency[c]); // then deposit the difference at some random bank
			}
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
						if(sim.bankDeposits[b]*(1-sim.rr)>sim.bankLoansOut[b]) {
							if (sim.bankDeposits[b]*(1-sim.rr)-sim.bankLoansOut[b] >= desiredCurrency[c]-sim.currency[sim.numBanks+c]) { 
								takeOutLoan(c,b,desiredCurrency[c]-sim.currency[sim.numBanks+c]);
								break;
							}
							else
								takeOutLoan(c,b,sim.bankDeposits[b]*(1-sim.rr)-sim.bankLoansOut[b]);
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
