Feature: CSAR snapshot tests

  Background:
    Given I am authenticated with "COMPONENTS_MANAGER" role
    And I have a CSAR folder that is "containing base types"
    And I upload it
    And I have a CSAR folder that is "containing java types"
    And I upload it
    Given I am authenticated with "ADMIN" role
    And There are these users in the system
      | sangoku |
    And I add a role "COMPONENTS_MANAGER" to user "sangoku"
    And I upload a plugin
    And I create a cloud with name "MockCloudTest" and plugin id "alien4cloud-mock-paas-provider:1.0" and bean name "mock-paas-provider"
    And I add a role "CLOUD_DEPLOYER" to user "sangoku" on the resource type "CLOUD" named "MockCloudTest"
    And I am authenticated with user named "sangoku"

  Scenario: Run test on a valid snapshot CSAR
    Given I have a CSAR folder that is "valid-csar-with-test"
    And I have CSAR name "topology-test" and version "2.0-SNAPSHOT"
    When I upload it
    And I run the test for this snapshot CSAR on cloud "MockCloudTest"
    Then I should receive a RestResponse with an error code 370
    And I should not have active deployment for this CSAR
    When I am authenticated with "ADMIN" role
    And I enable the cloud "MockCloudTest"
    And I am authenticated with user named "sangoku"
    And I run the test for this snapshot CSAR on cloud "MockCloudTest"
    Then I should receive a RestResponse with no error
    And I should have active deployment for this CSAR
    And The deployment must succeed

  Scenario: Run test should fail if user has no right on the cloud
    Given I am authenticated with "ADMIN" role
    And I enable the cloud "MockCloudTest"
    And I remove a role "CLOUD_DEPLOYER" to user "sangoku" on the resource type "CLOUD" named "MockCloudTest"
    And I am authenticated with user named "sangoku"
    And I have a CSAR folder that is "valid-csar-with-test"
    And I have CSAR name "topology-test" and version "2.0-SNAPSHOT"
    When I upload it
    And I run the test for this snapshot CSAR on cloud "MockCloudTest"
    Then I should receive a RestResponse with an error code 102

  Scenario: Run test on a  snapshot CSAR without topology test file raises an error
    Given I have a CSAR folder that is "csar-test-no-topology"
    And I have CSAR name "csar-test-no-topology" and version "1.0-SNAPSHOT"
    When I upload it
    And I enable the cloud "MockCloudTest"
    And I run the test for this snapshot CSAR on cloud "MockCloudTest"
    Then I should receive a RestResponse with an error code 504
    And I should not have active deployment for this CSAR

  Scenario: Undeploy a topology after a snapshot CSAR test
    Given I have a CSAR folder that is "valid-csar-with-test"
    And I have CSAR name "topology-test" and version "2.0-SNAPSHOT"
    When I upload it
    And I am authenticated with "ADMIN" role
    And I enable the cloud "MockCloudTest"
    And I am authenticated with user named "sangoku"
    And I run the test for this snapshot CSAR on cloud "MockCloudTest"
    Then I should receive a RestResponse with no error
    And The deployment must succeed
    When I undeploy the topology from its deployment id "null"
    Then I should receive a RestResponse with no error

  Scenario: Undeploy a topology with a bad deployment id
    When I undeploy the topology from its deployment id "xxx-ttyyy-bad-id"
    Then I should receive a RestResponse with an error code 504