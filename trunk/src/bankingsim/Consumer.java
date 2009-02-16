package bankingsim;

public class Consumer extends BankingSim{
	
	public Consumer() {
	
	}
	
	public void run() {
		
		//Calculate desired currency for each consumer
		double desiredCurrency[] = new double[numConsumers];
		double totalCur = 0;
		for (int c= 0;c<numConsumers;c++ ) {
			desiredCurrency[c]=initBase/numConsumers*Math.pow(Math.E,rng.nextGaussian());
			totalCur+=desiredCurrency[c];
		}
		for(int c=0;c<numConsumers;c++) {
			desiredCurrency[c] /= totalCur/initBase; //multiply by cr+1? or by 1/cr+1 or divide by either of these?
		}
		
		//given desired currency, consumer either wants to withdraw/borrow or save/pay back loans
		for(int c =numBanks;c<numBanks+numConsumers;c++) {
			if(desiredCurrency[c]<currency[numBanks+c]) { // consumer desires less currency
				if (consumerLoans[c]>0) { // if consumer has some loans taken out
					for (int b=0;b<numBanks;b++) { // then first try to decrease currency by paying back loans
						if (loans[b][numBanks+c]>=currency[numBanks+c]-desiredCurrency[c]) {
							payBackLoan(c,b,currency[numBanks+c]-desiredCurrency[c]);
							break;
						} else {
							payBackLoan(c,b,loans[b][numBanks+c]);
						}
					}
				}
				if(desiredCurrency[c]<currency[numBanks+c]) //if still desire less currency
					depositInBank(c,rng.nextInt(numBanks),currency[numBanks+c]-desiredCurrency[c]); // then deposit the difference at some random bank
			}
		}
		for(int c =numBanks;c<numBanks+numConsumers;c++) {
			if(desiredCurrency[c]>currency[numBanks+c]) { // consumer desires more currency
				if(consumerDeposits[c] > 0) { //if consumer has some deposits in some bank
					for(int b=0;b<numBanks;b++) { //then first try to increase currency by withdrawing deposits
						if (deposits[c][b]>=desiredCurrency[c]-currency[numBanks+c]) {
							withdrawFromBank(c,b,desiredCurrency[c]-currency[numBanks+c]);
							break;
						} else 
							withdrawFromBank(c,b,deposits[c][b]);
					}
				}
				if(desiredCurrency[c]>currency[numBanks+c]) { //if still want more currency
					for(int b=0;b<numBanks;b++) {  //then try to get the rest via loans
						if(bankDeposits[b]*(1-rr)>bankLoansOut[b]) {
							if (bankDeposits[b]*(1-rr)-bankLoansOut[b] >= desiredCurrency[c]-currency[numBanks+c]) { 
								takeOutLoan(c,b,desiredCurrency[c]-currency[numBanks+c]);
								break;
							}
							else
								takeOutLoan(c,b,bankDeposits[b]*(1-rr)-bankLoansOut[b]);
						}
					}
				}
				if(desiredCurrency[c]>currency[numBanks+c]) //if STILL want more currency
					everFailed[numBanks+c]=true; // consumer has failed in a sense
			}
		}
	}
	
	public void depositInBank(int c, int b, double amt) {
		deposits[c][b]+=amt;
		currency[numBanks+c]-=amt;
		consumerDeposits[c]+=amt;
		bankDeposits[b]+=amt;
	}
	
	public void withdrawFromBank(int c, int b, double amt) {
		deposits[c][b]-=amt;
		currency[numBanks+c]+=amt;
		consumerDeposits[c]-=amt;
		bankDeposits[b]-=amt;
	}
	
	public void payBackLoan(int c, int b, double amt) {
		loans[b][numBanks+c]-=amt;
		currency[numBanks+c]-=amt;
		bankLoansOut[b]-=amt;
		consumerLoans[c]-=amt;
	}
	
	public void takeOutLoan(int c, int b, double amt) {
		loans[b][numBanks+c]+=amt;
		currency[numBanks+c]+=amt;
		bankLoansOut[b]+=amt;
		consumerLoans[c]+=amt;
	}
}
