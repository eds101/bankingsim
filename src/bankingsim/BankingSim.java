/*
 * Financial Technology, Assignment 1
 * Eric Schmidt & Yunling Wang
 */

package bankingsim;


import java.text.DecimalFormat;
import java.util.Random;

public class BankingSim {
	
	//global constants
	protected final Fed fed;
	protected final Bank bank;
	protected final Consumer consumer;
	protected final int numBanks;
	protected final int numConsumers;
	protected final int initBase; // initial money base
	protected final double avgConsumerDeposits; // average deposits of each consumer
	protected final Random rng; //global randon number generator
	
	//global variables
	protected double rr; //reserve ratio
	protected double gdp; // gross domestic product -- the sum of the monetary values of all final goods produced by the economy
	protected double gdpGrowthRate; //rate of growth of gdp. fluctuates uniformly between -3% and +10%
	protected double currency[]; // currency held by each consumer and each bank. banks precede consumers in the array. currently, banks can't hold currency; rather, they hold deposits=bankDeposits[b]+bankLoansIn[b]+fedLoans[b]; but I have left the currency array like this in case our model ever include banks having some sort of currency 
	protected double loans[][]; // first dimension is loan-provider (banks), second is loan-recipient (banks or consumers). banks precede consumers in the dimension of recipients.
	protected double deposits[][]; // first dimension is depositors (consumers), second is deposit holders (banks).
	protected double fedLoans[]; // federal loans to each bank
	protected boolean everFailed[]; // whether each bank and consumer has ever failed or not.  Banks precede consumers in the array.  Failure occurs for a bank when a consumer wants to withdraw his deposits from the bank, but the bank doesn't have enough reserves to satisfy that request.  Failure occurs for a consumer when the consumer desires to take a loan of x dollars from the banking system, but the banking system cannot satisfy him.
	
	//convenience global variables
	protected double consumerDeposits[]; // deposits held by consumer i
	protected double consumerLoans[]; // loans held by consumer i
	protected double bankLoansOut[]; // loans from bank i to consumers
	protected double bankDeposits[]; // consumer deposits at bank i
	protected double bankLoansIn[]; // loans from other banks to bank i
	
	//derivative variables are only calculated at the end of each iteration for the purpose of printing them out
	protected double totalCurrency; // C = Summation(currency[])
	protected double totalRequiredReserves; // R = Summation(bankLoansOut[])*rr/(1-rr)
	protected double totalDeposits; // D = Summation(bankDeposits[]+bankLoansIn[]+fedLoans[]), over all i
	protected double cr; // Currency-Deposit ratio = C/D = Summation(currency[])/Summation(bankDeposits[]+bankLoansIn[]+fedLoans[]), over all i
	protected double moneyBase; // B = C+R
	protected double moneySupply; // M = C+D
	protected double moneySupplyGrowth; // growth of money supply between the past iteration and the current iteration
	
	//BankingSim private constants
	private final int numIterations;
	private final DecimalFormat twoDecimalFormat;

	public BankingSim(){
		rng = new Random();
		
		numBanks = 5;
		numConsumers = 1000;
		initBase = 10000000;
		avgConsumerDeposits = initBase/numConsumers*(.2+.2*(rng.nextDouble()-.5)); // savings rate is uniform random var between .1 and .3
		
		rr= 0.01+(rng.nextDouble()-.5)*.01;
		gdp = initBase;
		gdpGrowthRate = (rng.nextDouble()*13-3)/100;
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
		
		totalCurrency = initBase;
		totalRequiredReserves = 0;
		totalDeposits = 0; 
		cr = 0;
		moneyBase = initBase;
		moneySupply = initBase; 
		moneySupplyGrowth = 0; // not meaningfully set yet.
		
		fed = new Fed(this);
		bank = new Bank(this);
		consumer = new Consumer(this);
		numIterations = 100;
		twoDecimalFormat = new DecimalFormat("#0.00");
	
		//Spread initial money base among consumers as currency in order to kick-off the simulation.  as though the banking system is invented at t=0.
		double totalCur = 0;
		for(int i =0;i<numConsumers;i++) {
			currency[numBanks+i]=initBase/numConsumers*Math.pow(Math.E,rng.nextGaussian());
			totalCur+=currency[numBanks+i];
		}
		for(int i =0;i<numConsumers;i++) {
			currency[numBanks+i] /= (totalCur/initBase);
		}
		
		run();
		
	}
	
	public static void main (String [] args) {
		BankingSim sim = new BankingSim();
	}
	
	private void run() {
		System.out.println("SIMULATION CONSTANTS:");
		System.out.println(numBanks+" Banks and "+numConsumers+" Consumers.  Initial rr: " + twoDecimalFormat.format(rr*100) + "%.");
		System.out.println("Initial money base: $" + initBase + ".  Average deposits per consumer: $" + avgConsumerDeposits  + ".");
		System.out.println("Number of iterations: " + numIterations + ".");
		System.out.println("SIMULATION START:");
		for(int i = 0; i < numIterations; i++){
			consumer.run();
			bank.run();
			fed.run();
			calcDerivativeVars();
			printState(i+1);
		}
		printFinal();
	}
	
	private void calcDerivativeVars() {
		totalCurrency=0;
		totalDeposits=0;
		totalRequiredReserves=0;
		for (int i=0;i<currency.length;i++) {
			totalCurrency += currency[i];	
		}
		for(int i =0;i<numBanks;i++) {
			totalDeposits+=bankDeposits[i]+fedLoans[i]+bankLoansIn[i];
			totalRequiredReserves+=bankLoansOut[i];
		}
		totalRequiredReserves *= rr/(1-rr);
		cr = totalCurrency/totalDeposits;
		moneyBase = totalCurrency+totalRequiredReserves;
		double oldMoneySupply = moneySupply;
		moneySupply = totalCurrency+totalDeposits;
		moneySupplyGrowth = moneySupply/oldMoneySupply-1;
	}
	
	private void printState(int i){
		System.out.println("================================");
		System.out.println("Iteration " + i + ":");
		System.out.println("Reserve Ratio (rr -- assigned randomly by fed): " + twoDecimalFormat.format(rr*100) + "%");
		System.out.println("Currency (C): $" + twoDecimalFormat.format(totalCurrency));
		System.out.println("Deposits (D): $" + twoDecimalFormat.format(totalDeposits));
		System.out.println("Required Reserves (R): $" +twoDecimalFormat.format( totalRequiredReserves));
		System.out.println("Currency-Deposit Ratio (cr=C/D) " + twoDecimalFormat.format(cr));
		System.out.println("GDP Growth Rate: " + twoDecimalFormat.format(gdpGrowthRate*100) + "%");
		System.out.println("GDP: " + twoDecimalFormat.format(gdp));
		System.out.println("Money Base (B=C+R): $" + twoDecimalFormat.format(moneyBase));
		System.out.println("Money Supply (M=C+D): $" + twoDecimalFormat.format(moneySupply));
		System.out.println("Money Supply Growth: " + twoDecimalFormat.format(moneySupplyGrowth*100) + "%");
		
	}
	
	private void printFinal() {
		System.out.println();
		
		/*
		System.out.println("currency[]:");
		System.out.println("loans[][]:");
		System.out.println("deposits[][]:");
		System.out.println("fedLoans[]:");
		System.out.println("everFailed[]:");
		System.out.println("consumerDeposits[]:");
		System.out.println("consumerLoans[]:");
		System.out.println("bankLoansOut[]:");
		System.out.println("bankDeposits[]:");
		System.out.println("bankLoansIn[]:");*/
		
		System.out.print("everFailed[]: ");
		int numFailed = 0;
		for(int i=0;i<numBanks;i++) {
			int failedVal = 0;
			if(everFailed[i]) {
				failedVal = 1;
			}
			System.out.print(failedVal + " ");
			numFailed+=failedVal;
		}
		System.out.println("\nNumber of banks that ever failed: " + numFailed);
		
		
	}
	
}
