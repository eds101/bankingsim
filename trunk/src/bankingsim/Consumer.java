package bankingsim;

public class Consumer extends BankingSim{	
	public float d[][];
	public float d_prev[][];
	public float c[];
	public float c_prev[];
	public boolean state_failc[];
	protected float lc[];   //lc[k]: the total loans to consumer k;
	protected float dc[];   //lc[k]: the total deposit from consumer k;
	
	public Consumer(){
				
		for(int i = 0; i < numBanks; i++){
			for(int j = 0; j < numConsumers; j++){
				d[i][j] = 0;
				d_prev[i][j] = 0;
			}
		}
		
		for(int i = 0; i < numConsumers; i++){
			c[i] = 0;
			c_prev[i] = 0;
			state_failc[i] = false;
			lc[i] = 0;
			dc[i] = 0;
		}
	}
	
	public void run(){
		float C = 0;	//total currency in consumers' hand
		
		//backup old d[][] and c[]
		
		
		//check if there are any consumers bankrupted
		countLoans();
		countDeposits();
		for(int i = 0; i < numConsumers; i++){
			c[i] = lc[i] - dc[i];
			if(c[i] < 0)
				if(bankrupt(i))	continue;
			C += c[i];	
		}
		
		
		//generate new deposits
		float D = fed.cr * C;
		for(int i = 0; i < numBanks; i++){
			for(int j = 0; j < numConsumers; j++){
				//d[i][j]
			}
		}
		
	}
	
	protected void countLoans(){
		for(int i = 0; i < numConsumers; i++){
			lc[i] = 0;
			for(int j = 0; j < numBanks; j++){
				lc[i] += bank.l[j][i];
			}
		}
	}
	
	protected void countDeposits(){
		for(int i = 0; i < numConsumers; i++){
			dc[i] = 0;
			for(int j = 0; j < numBanks; j++){
				dc[i] += d[j][i];
			}
		}
	}
	
	//try to call back deposits before bankrupt consumer i
	//if fail to call back deposits, actually bankrupt consumer, return true
	//if succeed, don't bankrupt, return false
	protected boolean bankrupt(int i){
		return false;
	}
}
