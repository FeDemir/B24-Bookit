Feature: Hello World
  Scenario: Hello world GET api Test
    Given user sends get request to hello world api
    Then  status code is 200
    And response body contains "Hello World!"

