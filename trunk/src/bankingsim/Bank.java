package bankingsim;

public class Bank extends BankingSim{
	
	public Bank(){
		
	}
	
	public void run(){
		for(int b=0;b<numBanks;b++) {
			if(bankLoansOut[b]>bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]) { //if the bank is currently trying to loan out more money than it can
				for(int b2=0;b2<numBanks;b2++) { //first try to get loans from other banks
					if(bankDeposits[b2]*(1-rr)>bankLoansOut[b2]) {
						if(bankDeposits[b2]*(1-rr)-bankLoansOut[b2]>=bankLoansOut[b]-(bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b])) {
							interbankLoan(b,b2,bankLoansOut[b]-(bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]));
							break;
						} else {
							interbankLoan(b,b2,bankDeposits[b2]*(1-rr)-bankLoansOut[b2]);
						}
					}
				}
				if(bankLoansOut[b]>bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]) { //if the bank is still in need of money, it has failed, and it gets bailed out by the fed
					everFailed[b]=true;
					fedLoans[b]+=bankLoansOut[b]-(bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]);
				}
			}
			
			else if(bankLoansOut[b]<bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]) {// if the bank has excess reserves
				if (fedLoans[b]>0) { // first it tries to repay fed
					fedLoans[b]-=bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]-bankLoansOut[b];
					if(fedLoans[b]<0)
						fedLoans[b]=0;
				}
				if(bankLoansOut[b]<bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]) { // if it still has excess reserve
					if(bankLoansIn[b]>0) { // then it tries to repay interbank loans
						for(int b2=0;b2<numBanks;b2++) {
							if(loans[b2][b]>0) {
								if(loans[b2][b]>=bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]-bankLoansOut[b]) {
									repayInterbankLoan(b,b2,bankDeposits[b]*(1-rr)+bankLoansIn[b]+fedLoans[b]-bankLoansOut[b]);
									break;
								} else {
									repayInterbankLoan(b,b2,loans[b2][b]);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void interbankLoan(int borrower, int lender, double amt) {
		loans[lender][borrower]+=amt;
		bankLoansIn[borrower]+=amt;
		bankLoansOut[lender]+=amt;
	}
	
	public void repayInterbankLoan(int borrower, int lender, double amt) {
		loans[lender][borrower]-=amt;
		bankLoansIn[borrower]-=amt;
		bankLoansIn[lender]-=amt;
	}
}
