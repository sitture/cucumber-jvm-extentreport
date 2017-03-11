package com.sitture.definitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class StepDefinitions {
	@Given("^I have (\\d+) cukes in my belly$")
	public void iHaveCukesInMyBelly(int cukes) throws Throwable {
		System.out.format("Cukes: %d\n", cukes);
	}

	@Then("^I print out the results$")
	public void iPrintOutTheResults() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
	}

	@Given("^I have (\\d+) cukes in my bellies$")
	public void iHaveCukesInMyBellies(int cukes) throws Throwable {
		System.out.format("Cukes: %d\n", cukes);
	}

}
