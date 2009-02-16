package bankingsim;

import java.util.Random;


public class BankingSim{
	
	protected final Random rng;
	
	//BankingSim private constants
	private final Fed fed;
	private final Bank bank;
	private final Consumer consumer;
	private final int numIterations;
	
	//global constants
	protected final int numBanks;
	protected final int numConsumers;
	protected final int initBase;
	protected final double avgConsumerDeposits;
	protected final double initRR;
	protected final double cr;
	
	//global variables
	protected double rr;
	//protected double CR; //believed to be constant
	protected double currency[]; // currency held by each bank or consumer. banks precede consumers in the array.
	protected double loans[][]; // first dimension is loan-provider (banks), second is loan-recipient (banks or consumers). banks precede consumers in the dimension of recipients.
	protected double deposits[][]; // first dimension is depositors (consumers), second is deposit holders (banks).
	protected double fedLoans[]; // federal loans to each bank
	protected boolean everFailed[]; // whether each bank and consumer has ever failed or not.  Banks precede consumers in the array.  Failure occurs for a bank when a consumer wants to withdraw his deposits from the bank, but the bank doesn't have enough reserves to satisfy that request.  Failure occurs for a consumer when the consumer desires to take a loan of x dollars from the banking system, but the banking system cannot satisfy him.  
	
	//convenience global variables
	protected double consumerDeposits[];
	protected double consumerLoans[];
	protected double bankLoansOut[]; // loans from bank i to consumers
	protected double bankDeposits[]; // deposits at bank i
	protected double bankLoansIn[]; // loans from other banks to bank i
	/*protected double moneyBase;
	protected double moneySupply;
	protected double totalCurrency;
	protected double totalReserves;
	protected double totalDeposits;*/
	
	
	
	
	BankingSim(){
		rng = new Random();
		
		fed = new Fed();
		bank = new Bank();
		consumer = new Consumer();
		numIterations = 100;
		
		numBanks = 5;
		numConsumers = 1000;
		initBase = 10000000;
		avgConsumerDeposits = initBase/numConsumers*(.75+.2*(rng.nextDouble()-.5));
		initRR = 0.01*rng.nextInt(11);
		cr = .5+.2*(rng.nextDouble()-.5);
		
		rr=initRR;
		currency = new double[numBanks+numConsumers];
		deposits = new double [numConsumers][numBanks];
		loans = new double[numBanks][numBanks + numConsumers];
		fedLoans = new double [numBanks];
		everFailed = new boolean [numBanks+numConsumers];
		
		consumerDeposits = new double[numConsumers];
		consumerLoans = new double[numConsumers];
		bankLoansOut  = new double [numBanks];
		bankDeposits = new double [numBanks];
		bankLoansIn = new double [numBanks];
		
	
	
		//Spread initial money base among consumers as currency in order to kick-off the simulation.  as though the banking system is invented at t=0.
		double totalCur = 0;
		for(int i =numBanks;i<numBanks+numConsumers;i++) {
			currency[numBanks+i]=initBase/numConsumers*Math.pow(Math.E,rng.nextGaussian());
			totalCur+=currency[numBanks+i];
		}
		for(int i =numBanks;i<numBanks+numConsumers;i++) {
			currency[numBanks+i] /= (totalCur/initBase);
		}
		
		run();
		
	}
	
	private void printState(int i){
		System.out.println("================================");
		System.out.println("Iteration " + i);
		System.out.println("Money Supply: " + "[We don't know how to count this yet]");
		System.out.println("Money Base: " + "[We don't know how to count this yet]");
		System.out.println("rr: " + rr);
	}
	
	private void run() {
		System.out.println("SIMULATION CONSTANTS:");
		System.out.println(numBanks+" Banks and "+numConsumers+" Consumers.");
		System.out.println("Initial money base: $" + initBase + ". Average deposits per consumer: $" + avgConsumerDeposits  + ".");
		System.out.println("Initial Reserve Ratio: " + initRR + ".  Currency-Deposit Ratio: " + cr + ".");
		System.out.println("Num iterations: " + numIterations + ".");
		System.out.println("SIMULATION START:");
		for(int i = 0; i < numIterations; i++){
			consumer.run();
			bank.run();
			fed.run();
			printState(i+1);
		}
	}
	
	public static void main(String args[]){
		BankingSim sim = new BankingSim();
	}
	
}
