package alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hearst.PairCount;

public class Optimizer {

	private int k;
	private double threshold;
	private List<CandidateStat> list;

	public Optimizer(int k, double threshold, List<CandidateStat> list) {
		super();
		this.k = k;
		this.threshold = threshold;
		this.list = list;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public List<CandidateStat> getList() {
		return list;
	}

	public void setList(List<CandidateStat> list) {
		this.list = list;
	}

	public List<CandidateStat> generateOptimizedCandidates() {
		List<CandidateStat> outcome = new ArrayList<CandidateStat>();
		List<PairCount> sortList = new ArrayList<PairCount>();
		for (int i = 0; i < list.size(); i++) {
			CandidateStat c = list.get(i);
			sortList.add(new PairCount(c.getEntity() + "\t" + c.getCategory() + "\t" + i, c.getPosScore()));
		}
		Collections.sort(sortList);
		Collections.reverse(sortList);
		System.out.println("size: " + sortList.size());
		int currentCount = 0;
		double totalSum = 0;
		boolean found = true;
		boolean scanned = false;
		// Map<String, Integer> classCount=new HashMap<String, Integer>();
		while (currentCount < k && found && !scanned) {
			if (sortList.size() == 0)
				break;
			for (int i = 0; i < sortList.size(); i++) {
				if (i == sortList.size() - 1) {
					scanned = true;
				}
				PairCount p = sortList.get(i);
				String[] items = p.getPair().split("\t");
				int index = Integer.parseInt(items[2]);
				CandidateStat selected = list.get(index);
				if (totalSum + selected.getNegScore() < threshold) {
					totalSum += selected.getNegScore();
					outcome.add(selected);
					currentCount++;
					sortList.remove(i);
					found = true;
					System.out.println(totalSum);
					System.out.println("added: " + p.getPair());
					break;
				} else
					found = false;

			}
			if (!found)
				break;
		}
		return outcome;
	}

}
