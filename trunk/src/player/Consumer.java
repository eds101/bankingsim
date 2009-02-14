package player;

public class Consumer {	
	public float d[][];
	public float d_prev[][];
	public float c[];
	public float c_prev[];
	public boolean state_failc[];
	public float ave_d;
	public Bank bank;
	public Fed fed;
	protected int N1;
	protected int N2;
	protected float lc[];   //lc[k]: the total loans to consumer k;
	protected float dc[];   //lc[k]: the total deposit from consumer k;
	
	public Consumer(int n1, int n2){
		N1 = n1;
		N2 = n2;
		d = new float[N1][N2];
		d_prev = new float[N1][N2];
		c = new float[N2];
		c_prev = new float[N2];
		state_failc = new boolean[N2];
		ave_d = 0;
		lc = new float[N2];
		dc = new float[N2];
		
		for(int i = 0; i < N1; i++){
			for(int j = 0; j < N2; j++){
				d[i][j] = 0;
				d_prev[i][j] = 0;
			}
		}
		
		for(int i = 0; i < N2; i++){
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
		for(int i = 0; i < N2; i++){
			c[i] = lc[i] - dc[i];
			if(c[i] < 0)
				if(bankrupt(i))	continue;
			C += c[i];	
		}
		
		
		//generate new deposits
		float D = fed.cr * C;
		for(int i = 0; i < N1; i++){
			for(int j = 0; j < N2; j++){
				//d[i][j]
			}
		}
		
		
		//calculate average deposit
		ave_d = 0;
		for(int i = 0; i < N1; i++){
			for(int j = 0; j < N2; j++){
				ave_d += d[i][j];
				d_prev[i][j] = d[i][j];
			}
		}
		ave_d /= N2;
	}
	
	protected void countLoans(){
		for(int i = 0; i < N2; i++){
			lc[i] = 0;
			for(int j = 0; j < N1; j++){
				lc[i] += bank.l[j][i];
			}
		}
	}
	
	protected void countDeposits(){
		for(int i = 0; i < N2; i++){
			dc[i] = 0;
			for(int j = 0; j < N1; j++){
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
