'use strict';

angular.module('alienUiApp').factory('cloudImageServices', ['$resource',
  function($resource) {

    var cloudImageRequirementFormDescriptor = {
      "_type": "complex",
      "_order": [ "numCPUs", "diskSize", "memSize"],
      "_propertyType": {
        "numCPUs": {
          "_label": "CLOUD_IMAGES.NUM_CPUS",
          "_type": "number",
          "_notNull": false,
          "_step": 1,
          "_constraints": [
            {
              "greaterOrEqual": 1
            }
          ]
        },
        "diskSize": {
          "_label": "CLOUD_IMAGES.DISK_SIZE",
          "_type": "number",
          "_notNull": false,
          "_step": 0.01,
          "_unit": "GB",
          "_multiplier": 1024 * 1024 * 1024,
          "_constraints": [
            {
              "greaterThan": 0
            }
          ]
        },
        "memSize": {
          "_label": "CLOUD_IMAGES.MEM_SIZE",
          "_type": "number",
          "_notNull": false,
          "_step": 1,
          "_unit": "MB",
          "_multiplier": 1024 * 1024,
          "_constraints": [
            {
              "greaterOrEqual": 1
            }
          ]
        }
      }
    };

    var cloudImageFormDescriptorFactory = function() {
      return {
        "_type": "complex",
        "_order": [ "name", "osType", "osArch", "osDistribution", "osVersion"],
        "_propertyType": {
          "name": {
            "_label": "CLOUD_IMAGES.NAME",
            "_type": "string",
            "_notNull": true
          },
          "osDistribution": {
            "_label": "CLOUD_IMAGES.OS_DISTRIBUTION",
            "_type": "string",
            "_notNull": true
          },
          "osType": {
            "_label": "CLOUD_IMAGES.OS_TYPE",
            "_type": "string",
            "_notNull": true,
            "_validValues": [
              "linux",
              "aix",
              "mac os",
              "windows"
            ]
          },
          "osVersion": {
            "_label": "CLOUD_IMAGES.OS_VERSION",
            "_type": "string",
            "_notNull": true
          },
          "osArch": {
            "_label": "CLOUD_IMAGES.OS_ARCH",
            "_type": "string",
            "_notNull": true,
            "_validValues": [
              "x86_64",
              "x86_32"
            ]
          }
        }
      };
    };

    var getFormDescriptor = function() {
      return cloudImageFormDescriptorFactory();
    };

    var crudCloudImage = $resource('rest/cloud-images/:id', {}, {
      'update': {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8'
        }
      }
    });

    return {
      'create': crudCloudImage.save,
      'get': crudCloudImage.get,
      'getFormDescriptor': getFormDescriptor,
      'requirementDescriptor': cloudImageRequirementFormDescriptor,
      'update': crudCloudImage.update,
      'remove': crudCloudImage.remove
    };
  }
]);