package player;

public class Bank {
	public float l[][];
	public float l_prev[][];
	public float c[];
	public float c_prev[];
	public boolean state_failb[];
	public Consumer consumer;
	public Fed fed;
	protected int N1;
	protected int N2;
	protected float l_in[];		//total loans it gets from other banks
	protected float l_out[];	//total loans it lends to other banks
	protected float d[];	//total deposits it gets from consumers
	
	public Bank(int n1, int n2){
		N1 = n1;
		N2 = n2;
		
		l = new float[N1][N2+N1];
		l_prev = new float[N1][N2+N1];
		c = new float[N1];
		c_prev = new float[N1];
		state_failb = new boolean[N1];
		l_in = new float[N1];
		l_out = new float[N1];
		d = new float[N1];
		
		for(int i = 0; i < N1; i++){
			c[i] = 0;
			c_prev[i] = 0;
			state_failb[i] = false;
			l_in[i] = 0;
			l_out[i] = 0;
			d[i] = 0;
			for(int j = 0; j < N2+N1; j++){
				l[i][j] = 0;
				l_prev[i][j] = 0;
			}
		}
	}
	
	public void run(){
		//backup old l[][]
		for(int i = 0; i < N1; i++){
			for(int j = 0; j < N2+N1; j++){
				l_prev[i][j] = l[i][j];
			}
		}
			
		
		//generate new l[][]
		
		//check if there are any banks bankrupted
		countLoans();
		countDeposits();
		for(int i = 0; i < N1; i++){
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
		for(int i = 0; i < N1; i++){
			l_in[i] = 0;
			l_out[i] = 0;
			for(int j = 0; j < N1; j++){
				l_in[i] += l[j][i];
			}
			for(int j = 0; j < N2; j++){
				l_out[i] += l[i][j];
			}
		}
	}
	
	protected void countDeposits(){
		for(int i = 0; i < N1; i++){
			d[i] = 0;
			for(int j = 0; j < N2; j++){
				d[i] += consumer.d[i][j];
			}
		}
	}
}
