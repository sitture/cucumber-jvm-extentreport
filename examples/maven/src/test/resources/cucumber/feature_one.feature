@feature_one
Feature: My First Feature

  @scenario_one
  Scenario Outline: My First Scenario
    Given I have <test> cukes in my belly
    Then I print out the results

    Examples:
    | test |
    | 1    |
    | 2    |
	
  @scenario_two
  Scenario: My Second Scenario
    Given I have 7 cukes in my bellies
    Then I print out the results