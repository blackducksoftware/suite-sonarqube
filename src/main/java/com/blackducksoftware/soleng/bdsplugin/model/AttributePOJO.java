package com.blackducksoftware.soleng.bdsplugin.model;

public class AttributePOJO {

	private String question;
	private String answer;
	
	/**
	 * Creates a bean with the attribute question and answer
	 * @param ques - The description as seen on the details page of the CC application
	 * @param ans - The user inputted field for that question.
	 */
	public AttributePOJO(String ques, String ans)
	{
		this.question = ques;
		this.answer = ans;
	}
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	
}
