package bankingsim;

public class Bank {
	
	private BankingSim sim;
	
	public Bank(BankingSim sim){
		this.sim = sim;
	}
	
	public void run(){
		for(int b=0;b<sim.numBanks;b++) {
			if(sim.bankLoansOut[b]<(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)) {// if the bank has excess reserves
				if (sim.fedLoans[b]>0) { // first it tries to repay fed
					sim.fedLoans[b]-=(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)-sim.bankLoansOut[b];
					if(sim.fedLoans[b]<0)
						sim.fedLoans[b]=0;
				}
				if(sim.bankLoansOut[b]<(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)) { // if it still has excess reserve
					if(sim.bankLoansIn[b]>0) { // then it tries to repay interbank loans
						for(int b2=0;b2<sim.numBanks;b2++) {
							if(sim.loans[b2][b]>0) {
								if(sim.loans[b2][b]>=(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)-sim.bankLoansOut[b]) {
									repayInterbankLoan(b,b2,(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)-sim.bankLoansOut[b]);
									break;
								} else {
									repayInterbankLoan(b,b2,sim.loans[b2][b]);
								}
							}
						}
					}
				}
			}
			else if(sim.bankLoansOut[b]>(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)) { //if the bank is currently trying to loan out more money than it can
				for(int b2=0;b2<sim.numBanks;b2++) { //first try to get loans from other banks
					if(sim.bankDeposits[b2]*(1-sim.rr)>sim.bankLoansOut[b2]) {
						if(sim.bankDeposits[b2]*(1-sim.rr)-sim.bankLoansOut[b2]>=sim.bankLoansOut[b]-(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)) {
							interbankLoan(b,b2,sim.bankLoansOut[b]-(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr));
							break;
						} else {
							interbankLoan(b,b2,sim.bankDeposits[b2]*(1-sim.rr)-sim.bankLoansOut[b2]);
						}
					}
				}
				if(sim.bankLoansOut[b]>(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr)) { //if the bank is still in need of money, it has failed, and it gets bailed out by the fed
					sim.everFailed[b]=true;
					sim.fedLoans[b]+=sim.bankLoansOut[b]-(sim.bankDeposits[b]+sim.bankLoansIn[b]+sim.fedLoans[b])*(1-sim.rr);
				}
			}
		}
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
}
