package hearst;

public class IsAPair {
	
	private String subConcept;
	private String superConcept;
	
	public IsAPair(String subConcept, String superConcept) {
		super();
		this.subConcept = subConcept;
		this.superConcept = superConcept;
	}

	public String getSubConcept() {
		return subConcept;
	}

	public void setSubConcept(String subConcept) {
		this.subConcept = subConcept;
	}

	public String getSuperConcept() {
		return superConcept;
	}

	public void setSuperConcept(String superConcept) {
		this.superConcept = superConcept;
	}
	
	@Override
	public String toString() {
		return subConcept+"\t"+superConcept;
	}

}
