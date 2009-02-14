package player;

public class Fed {
	public float B;
	public float M;
	public float rr;
	public float rr_prev;
	public float cr;
	public float cr_prev;
	public float C;
	public float R;
	public Bank bank;
	public float l[];	
	public float r[];
	protected int N1;
	protected int N2;
	
	public Fed(int n1, int n2, float b){
		N1 = n1;
		N2 = n2;
		B = b;
		rr = 0;
		rr_prev = 0;
		cr = 2;
		M = 0;
		C = 0;
		R = 0;
		l = new float[N1];
		r = new float[N1];
		for(int i = 0; i < N1; i++){
			l[i] = 0;
			r[i] = 0;
		}
	}
	
	public void run(){
		//calculate B, M, C, R etc.
		
		//backup l[], rr and cr
		
		//regenerate l[]
		
		//regenerate rr, cr
	}

}
