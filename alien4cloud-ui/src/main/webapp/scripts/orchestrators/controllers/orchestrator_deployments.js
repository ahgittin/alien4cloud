define(function (require) {
  'use strict';

  var modules = require('modules');
  var states = require('states');
  var angular = require('angular');
  var _ = require('lodash');
  
  require('scripts/deployment/services/deployment_services');

  states.state('admin.orchestrators.details.deployments', {
    url: '/deployments',
    templateUrl: 'views/orchestrators/orchestrator_deployments.html',
    controller: 'OrchestratorDeploymentsCtrl',
    menu: {
      id: 'menu.orchestrators.deployments',
      state: 'admin.orchestrators.details.deployments',
      key: 'ORCHESTRATORS.NAV.DEPLOYMENTS',
      icon: 'fa fa-rocket',
      priority: 200
    }
  });

  modules.get('a4c-orchestrators').controller('OrchestratorDeploymentsCtrl',
    ['$scope', '$modal', '$state', 'deploymentServices', 'orchestrator',
    function($scope, $modal, $state, deploymentServices, orchestrator) {
      $scope.orchestrator = orchestrator;
      //get all deployments for this cloud
      deploymentServices.get({
        orchestratorId: $scope.orchestrator.id,
        includeSourceSummary: true
      }, function(result) {
        $scope.deployments = result.data;
      });
    }
  ]); // controller
}); // define
