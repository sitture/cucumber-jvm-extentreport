package com.sitture.definitions;

import com.sitture.Belly;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;

public class StepDefinitions {

    private Belly belly;

    @Then("^I have (\\d+) cukes in my belly$")
    public void iHaveCukesInMyBellies(int cukes) throws Throwable {
        assertEquals(cukes, belly.getCukes());
    }

    @When("^I eat (\\d+) cukes$")
    public void iEatCukes(int cukes) throws Throwable {
        belly.eat(cukes);
    }

    @Given("^I have a belly$")
    public void iHaveABelly() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        belly = new Belly();
    }

    @And("^I print the results$")
    public void iPrintTheResults() throws Throwable {
        System.out.println(belly.getCukes());
    }
}
