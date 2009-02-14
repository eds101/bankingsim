package simulation;

import player.*;

public class Bankingsim {
	static Fed fed;
	static Bank bank;
	static Consumer consumer;
	static int N1;
	static int N2;
	
	Bankingsim(int n1, int n2, float b){
		fed = new Fed(n1, n2, b);
		bank = new Bank(n1, n2);
		consumer = new Consumer(n1, n2);
		fed.bank = bank;
		bank.fed = fed;
		bank.consumer = consumer;
		consumer.bank = bank;
		consumer.fed = fed;
	}
	
	static void printState(int i){
		System.out.println("================================");
		System.out.println("Iteration "+i);
		System.out.println("Money Supply: "+fed.M);
		System.out.println("Money Base: "+fed.B);
		System.out.println("Average deposit: "+consumer.ave_d);
		System.out.println("rr: "+fed.rr_prev);
		System.out.println("cr: "+fed.cr_prev);
	}
	
	static void run(float B){
		Bankingsim sim = new Bankingsim(N1,N2,B);
		System.out.println(N1+" Banks and "+N2+" Consumers");
		for(int i = 0; i < 100; i++){
			consumer.run();
			bank.run();
			fed.run();
			printState(i+1);
		}
	}
	public static void main(String args[]){
		N1 = 10;
		N2 = 20;
		float B = 10000;
		run(B);
		
	}
	
	
}
