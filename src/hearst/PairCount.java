package hearst;

public class PairCount implements Comparable<PairCount> {
	
	private String pair;
	private double count;
	
	public PairCount(String pair, double count) {
		super();
		this.pair = pair;
		this.count = count;
	}

	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	public double getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(PairCount o) {
		if (count>o.count)
			return 1;
		else if (count<o.count)
			return -1;
		else
			return 0;
	}
	
	

}
