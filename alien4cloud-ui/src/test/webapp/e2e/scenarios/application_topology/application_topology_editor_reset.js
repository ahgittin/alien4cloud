/* global element, by */
'use strict';

var path = require('path');
var navigation = require('../../common/navigation');
var common = require('../../common/common');
var authentication = require('../../authentication/authentication');
var topologyEditorCommon = require('../../topology/topology_editor_common');
var componentData = require('../../topology/component_data');

describe('Nodetemplate properties and artifact reset to default value', function() {

  beforeEach(function() {
    topologyEditorCommon.beforeTopologyTest();
  });

  // After each spec in the tests suite(s)
  afterEach(function() {
    // Logout action
    authentication.logout();
  });

  var switchDeploymentTopopology = function switchDeploymentTopopology() {
    // switch to reload the topology
    navigation.go('applications', 'deployment');
    navigation.go('applications', 'topology');
  };

  it('should be able to change a property for a node and reset to the default value', function() {
    console.log('################# should be able to change a property for a node and reset to the default value');

    topologyEditorCommon.addNodeTemplatesCenterAndZoom({
      compute: componentData.toscaBaseTypes.compute()
    });

    // change properties on node compute
    var nodeCompute = element(by.id('rect_Compute'));
    browser.actions().click(nodeCompute).perform();

    var memSizeId = 'mem_size';
    common.expectValueFromXEditable('p_' + memSizeId, '2048');
    topologyEditorCommon.editNodeProperty('Compute', memSizeId, '16000', 'pro', 'GB');

    // reload the
    switchDeploymentTopopology();

    // reset this mem property
    browser.actions().click(nodeCompute).perform();
    common.expectValueFromXEditable('p_' + memSizeId, '16000');

    var resetMemSizeButton = element(by.id('reset-property-' + memSizeId));
    browser.actions().click(resetMemSizeButton).perform();
    switchDeploymentTopopology();
    // check the supposed reseted mem_size
    browser.actions().click(nodeCompute).perform();
    common.expectValueFromXEditable('p_' + memSizeId, '2048');

  });

  it('should be able to change deployment artifact for a node and reset to the default value', function() {
    console.log('################# should be able to change deployment artifact for a node and reset to the default value');

    topologyEditorCommon.addNodeTemplatesCenterAndZoom({
      apacheLB: componentData.apacheTypes.apacheLBGroovy()
    });

    topologyEditorCommon.selectNodeAndGoToDetailBloc('apacheLBGroovy', topologyEditorCommon.nodeDetailsBlocsIds['art']);
    element.all(by.repeater('(artifactId, artifact) in selectedNodeTemplate.artifacts')).then(function(artifacts) {
      expect(artifacts.length).toEqual(1);
      var myScript = artifacts[0];
      expect(myScript.element(by.binding('artifactId')).getText()).toEqual('scripts');
      expect(myScript.element(by.binding('artifact.artifactType')).getText()).toEqual('fastconnect.artifacts.ResourceDirectory');
      expect(myScript.element(by.binding('artifact.artifactName')).getText()).toEqual('scripts');
      var myScriptUpdateButton = browser.element(by.css('input[type="file"]'));
      myScriptUpdateButton.sendKeys(path.resolve(__dirname,
        '../../../../../../../alien4cloud-rest-it/src/test/resources/data/artifacts/myWar.war'));
      browser.waitForAngular();
      myScript.element(by.binding('artifact.artifactName')).getText().then(function(text) {
        expect(text.length).toBeGreaterThan(0);
        expect(text).toEqual('myWar.war');
      });

      // reset check the artifact name is back
      var resetScriptArtifactButton = element(by.id('reset-artifact-scripts'));
      browser.actions().click(resetScriptArtifactButton).perform();
      expect(myScript.element(by.binding('artifactId')).getText()).toEqual('scripts');
      expect(myScript.element(by.binding('artifact.artifactType')).getText()).toEqual('fastconnect.artifacts.ResourceDirectory');
      expect(myScript.element(by.binding('artifact.artifactName')).getText()).toEqual('scripts');

    });

  });

});
