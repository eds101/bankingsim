package bankingsim;

public class Bank {
	
	private BankingSim sim;
	
	
	public Bank(BankingSim sim){
		this.sim = sim;
	}
	
	public void run(){
		for(int b=0;b<sim.numBanks;b++) {
			//each bank randomly makes or loses money by a rate between -GDP and +GDP.
			changeCurrency(b,(sim.bankLoansOut[b]-sim.fedLoans[b]-sim.bankLoansIn[b])*(sim.rng.nextDouble()-.5)*2*sim.gdpGrowthRate);
			
			
			//repay interbank loans with interest of prevFedInterestRate
			if(sim.bankLoansIn[b]>0) {
				for(int b2=0;b2<sim.numBanks;b2++) {
						repayInterbankLoan(b,b2,sim.loans[b2][b]*sim.prevFedInterestRate);
				}
			}
			
			
			if(sim.bankLoansOut[b]<(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr) + sim.currency[b]) {// if the bank has excess reserves
				if (sim.fedLoans[b]>0) { // try to repay fed
					sim.fedLoans[b]-=(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)+ sim.currency[b]-sim.bankLoansOut[b];
					if(sim.fedLoans[b]<0)
						sim.fedLoans[b]=0;
				}
			}
			else if(sim.bankLoansOut[b]>(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)+ sim.currency[b]) { //if the bank is currently trying to loan out more money than it can
				for(int b2=0;b2<sim.numBanks;b2++) { //first try to get loans from other banks
					if(sim.bankDeposits[b2]*(1-sim.rr)+ sim.currency[b2]>sim.bankLoansOut[b2]) {
						if(sim.bankDeposits[b2]*(1-sim.rr)+ sim.currency[b2]-sim.bankLoansOut[b2]>=sim.bankLoansOut[b]-(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)- sim.currency[b]) {
							interbankLoan(b,b2,sim.bankLoansOut[b]-(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)- sim.currency[b]);
							break;
						} else {
							interbankLoan(b,b2,sim.bankDeposits[b2]*(1-sim.rr)+ sim.currency[b2]-sim.bankLoansOut[b2]);
						}
					}
				}
				if(sim.bankLoansOut[b]>(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)+ sim.currency[b]) { //if the bank is still in need of money, it immediately gets bailed out by the fed, and so never fails
					//sim.everFailed[b]=true;
					sim.fed.askForLoan(b,sim.bankLoansOut[b]-(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr));
				}
			}
		}
	
		sim.prevBidAskRate=sim.bidAskRate;
		
		sim.bidAskRate = .01+sim.rng.nextDouble()*.01;
	}
	
	public void interbankLoan(int borrower, int lender, double amt) {
		sim.loans[lender][borrower]+=amt;
		sim.bankLoansIn[borrower]+=amt;
		sim.bankLoansOut[lender]+=amt;
	}
	
	public void repayInterbankLoan(int borrower, int lender, double amt) {
		sim.loans[lender][borrower]-=amt;
		sim.bankLoansIn[borrower]-=amt;
		sim.bankLoansIn[lender]-=amt;
	}
	
	public void changeCurrency(int bank, double amt) {
		sim.currency[bank]+=amt;
		
	}
}
