package oraclehomeworkautograder;

public class AnswerModel {
	
	private String submissionID;
	private String nameCode;
	private String username;
	private int nomorSoal;
	private String timeSubmitted;
	private String query;
	private int verdict;
	
	
	
	
	/**
	 * @param submissionID
	 * @param nameCode
	 * @param nomorSoal
	 * @param timeSubmitted
	 * @param query
	 */
	public AnswerModel(String submissionID, String nameCode, String username, int nomorSoal, String timeSubmitted, String query) {
		this.submissionID = submissionID;
		this.nameCode = nameCode;
		this.username = username;
		this.nomorSoal = nomorSoal;
		this.timeSubmitted = timeSubmitted;
		this.query = query;
	}
	
	
	
	
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}





	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}





	/**
	 * @return the verdict
	 */
	public int getVerdict() {
		return verdict;
	}



	/**
	 * @param verdict the verdict to set
	 */
	public void setVerdict(int verdict) {
		this.verdict = verdict;
	}



	/**
	 * @return the submissionID
	 */
	public String getSubmissionID() {
		return submissionID;
	}
	/**
	 * @param submissionID the submissionID to set
	 */
	public void setSubmissionID(String submissionID) {
		this.submissionID = submissionID;
	}
	/**
	 * @return the nameCode
	 */
	public String getNameCode() {
		return nameCode;
	}
	/**
	 * @param nameCode the nameCode to set
	 */
	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	/**
	 * @return the nomorSoal
	 */
	public int getNomorSoal() {
		return nomorSoal;
	}
	/**
	 * @param nomorSoal the nomorSoal to set
	 */
	public void setNomorSoal(int nomorSoal) {
		this.nomorSoal = nomorSoal;
	}
	/**
	 * @return the timeSubmitted
	 */
	public String getTimeSubmitted() {
		return timeSubmitted;
	}
	/**
	 * @param timeSubmitted the timeSubmitted to set
	 */
	public void setTimeSubmitted(String timeSubmitted) {
		this.timeSubmitted = timeSubmitted;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	
	
	
	
	
	

}
