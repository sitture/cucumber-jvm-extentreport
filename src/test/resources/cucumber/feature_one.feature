@successful
@feature_one
Feature: My First Feature

  This is my very first feature and it will show you how awesome this formatter is.

  @scenario_one
  Scenario Outline: Eating cukes results in cukes in my belly
    This is a description for my scenario outline.

    Given I have a belly
    When I eat <test> cukes
    Then I have <result> cukes in my belly

    Examples:
    | test | result |
    | 1    | 1      |
    | 2    | 2      |
	
  @scenario_two
  Scenario: My Second Scenario
    Given I have a belly
    When I eat 3 cukes
    Then I have 3 cukes in my belly
