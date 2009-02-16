package bankingsim;

import java.util.Random;


public class BankingSim{
	
	//BankingSim private constants
	private final Fed fed;
	private final Bank bank;
	private final Consumer consumer;
	private final int numIterations;
	
	//global constants
	protected final int numBanks;
	protected final int numConsumers;
	protected final int initBase;
	protected final double initRR;
	protected final double CR;
	protected final double avgConsumerDeposits;
	
	//global variables
	protected double RR;
	//protected double CR; //believed to be constant
	protected double currency[]; // currency held by each bank or consumer. banks precede consumers in the array.
	protected double loans[][]; // first dimension is loan-provider (banks), second is loan-recipient (banks or consumers). banks precede consumers in the dimension of recipients.
	protected double deposits[][]; // first dimension is depositors (consumers), second is deposit holders (banks).
	protected double fedLoans[]; // federal loans to each bank
	protected boolean everFailed[]; // whether each bank and consumer has ever failed or not.  banks precede consumers in the array.
	
	
	
	BankingSim(){
		Random rng = new Random();
		
		fed = new Fed();
		bank = new Bank();
		consumer = new Consumer();
		numIterations = 100;
		
		numBanks = 5;
		numConsumers = 1000;
		initBase = 10000000;
		avgConsumerDeposits = initBase/numConsumers*(.75+.2*(rng.nextDouble()-.5));
		initRR = 0.1;
		CR = .5+.2*(rng.nextDouble()-.5);
		
		RR=initRR;
		currency = new double[numBanks+numConsumers];
		loans = new double[numBanks][numBanks + numConsumers];
		deposits = new double [numConsumers][numBanks];
		fedLoans = new double [numBanks];
		everFailed = new boolean [numBanks+numConsumers];
	
		run();
		
	}
	
	private void printState(int i){
		System.out.println("================================");
		System.out.println("Iteration " + i);
		System.out.println("Money Supply: " + "[We don't know how to count this yet]");
		System.out.println("Money Base: " + "[We don't know how to count this yet]");
		System.out.println("rr: " + RR);
	}
	
	private void run() {
		System.out.println("SIMULATION CONSTANTS:");
		System.out.println(numBanks+" Banks and "+numConsumers+" Consumers.");
		System.out.println("Initial money base: $" + initBase + ". Average deposits per consumer: $" + avgConsumerDeposits  + ".");
		System.out.println("Initial Reserve Ratio: " + initRR + ".  Currency-Deposit Ratio: " + CR + ".");
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
