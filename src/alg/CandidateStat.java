package alg;

public class CandidateStat {

	private String entity;
	private String category;
	private double posScore;
	private double negScore;

	public CandidateStat(String entity, String category, double posScore, double negScore) {
		super();
		this.entity = entity;
		this.category = category;
		this.posScore = posScore;
		this.negScore = negScore;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPosScore() {
		return posScore;
	}

	public void setPosScore(double posScore) {
		this.posScore = posScore;
	}

	public double getNegScore() {
		return negScore;
	}

	public void setNegScore(double negScore) {
		this.negScore = negScore;
	}

}
