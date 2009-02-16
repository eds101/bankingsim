package bankingsim;

public class Bank extends BankingSim{
	public float l[][];
	public float l_prev[][];
	public float c[];
	public float c_prev[];
	public boolean state_failb[];
	protected float l_in[];		//total loans it gets from other banks
	protected float l_out[];	//total loans it lends to other banks
	protected float d[];	//total deposits it gets from consumers
	
	public Bank(){
		
		for(int i = 0; i < numBanks; i++){
			c[i] = 0;
			c_prev[i] = 0;
			state_failb[i] = false;
			l_in[i] = 0;
			l_out[i] = 0;
			d[i] = 0;
			for(int j = 0; j < numConsumers+numBanks; j++){
				l[i][j] = 0;
				l_prev[i][j] = 0;
			}
		}
	}
	
	public void run(){
		//backup old l[][]
		for(int i = 0; i < numBanks; i++){
			for(int j = 0; j < numConsumers+numBanks; j++){
				l_prev[i][j] = l[i][j];
			}
		}
			
		
		//generate new l[][]
		
		//check if there are any banks bankrupted
		countLoans();
		countDeposits();
		for(int i = 0; i < numBanks; i++){
			float deposit = fed.l[i]+l_in[i]+d[i];
			c[i] = deposit*(1-fed.rr)-l_out[i];
			fed.r[i] = deposit*fed.rr; 
			if(c[i] < 0)
				bankrupt(i);
		}
		
	}
	
	//try to call back loans and reserves before bankrupt bank i
	//if fail to call back things, actually bankrupt bank, return true
	//if succeed, don't bankrupt, return false
	protected boolean bankrupt(int i){
		return false;
	}
	
	protected void countLoans(){
		for(int i = 0; i < numBanks; i++){
			l_in[i] = 0;
			l_out[i] = 0;
			for(int j = 0; j < numBanks; j++){
				l_in[i] += l[j][i];
			}
			for(int j = 0; j < numConsumers; j++){
				l_out[i] += l[i][j];
			}
		}
	}
	
	protected void countDeposits(){
		for(int i = 0; i < numBanks; i++){
			d[i] = 0;
			for(int j = 0; j < numConsumers; j++){
				d[i] += consumer.d[i][j];
			}
		}
	}
}
