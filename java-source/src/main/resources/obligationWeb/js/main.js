"use strict";

// Define your backend here.
angular.module('demoAppModule', ['ui.bootstrap', 'highcharts-ng']).controller('DemoAppCtrl', function($http, $location, $uibModal, $scope, $timeout) {
    const demoApp = this;

    const apiBaseURL = "/api/obligation/";
    const apiBaseURLb = "/api/work/";
    const apiBaseURLc = "/api/capacity/";
    // Retrieves the identity of this and other nodes.
    let peers = [];
    let works = [];
    let workList = [];
    let capacities = [];
    $http.get(apiBaseURL + "me").then((response) => demoApp.thisNode = response.data.me);
    $http.get(apiBaseURL + "peers").then((response) => peers = response.data.peers);
    $http.get(apiBaseURLb + "works").then((response) => works = response.data);
    $http.get(apiBaseURLb + "capacities").then((response) => capacities = response.data);


    /** Displays the capacities creation modal. */
    demoApp.openCreateCPVModal = () => {
        console.log(works);
        const createIOUModal = $uibModal.open({
            templateUrl: 'createCPVModal.html',
            controller: 'CreateCPVModalCtrl',
            controllerAs: 'createCPVModal',
            resolve: {
                apiBaseURL: () => apiBaseURLc,
                peers: () => peers,
                works: () => capacities,
                capacities: () => capacities
            }
        });

        // Ignores the modal result events.
        createIOUModal.result.then(() => {}, () => {});
    };


    /** Displays the Pay invoice creation modal. */
    demoApp.openCreatePIModal = () => {
        console.log(works);
        const createIOUModal = $uibModal.open({
            templateUrl: 'createPIModal.html',
            controller: 'CreatePIModalCtrl',
            controllerAs: 'createPIModal',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                peers: () => peers,
                works: () => works,
                workList: () => workList
            }
        });

        // Ignores the modal result events.
        createIOUModal.result.then(() => {}, () => {});
    };


    /** Displays the IOU creation modal. */
    demoApp.openCreateIOUModal = () => {
        console.log(works);
        const createIOUModal = $uibModal.open({
            templateUrl: 'createIOUModal.html',
            controller: 'CreateIOUModalCtrl',
            controllerAs: 'createIOUModal',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                peers: () => peers,
                works: () => works,
                workList: () => workList
            }
        });

        // Ignores the modal result events.
        createIOUModal.result.then(() => {}, () => {});
    };

    demoApp.openCreateWorkModal = () => {
    console.log('try to pen it');
            const createWorkModal = $uibModal.open({
                templateUrl: 'createWorkModal.html',
                controller: 'CreateWorkModalCtrl',
                controllerAs: 'createWorkModal',
                resolve: {
                    apiBaseURL: () => apiBaseURLb,
                    peers: () => peers
                }
            });

            // Ignores the modal result events.
            createWorkModal.result.then(() => {console.log('open the work modal')}, () => {console.log('open the work modal outside')});
        };


    demoApp.openCreateVendorWorkModal = () => {
        console.log('try to pen it');
                const createWorkModal = $uibModal.open({
                    templateUrl: 'createVendorWorkModal.html',
                    controller: 'CreateWorkModalCtrl',
                    controllerAs: 'createWorkModal',
                    resolve: {
                        apiBaseURL: () => apiBaseURLb,
                        peers: () => peers
                    }
                });

                // Ignores the modal result events.
                createWorkModal.result.then(() => {console.log('open the work modal')}, () => {console.log('open the work modal outside')});
            };

    demoApp.openCreateINVModal = () => {
        console.log('try to pen it');
        const createINVModal = $uibModal.open({
            templateUrl: 'createINVModal.html',
            controller: 'CreateINVModalCtrl',
            controllerAs: 'createINVModal',
            resolve: {
                apiBaseURL: () => apiBaseURLb,
                peers: () => peers,
                works: () => works,
                capacities: () => capacities
            }
        });

        // Ignores the modal result events.
        createINVModal.result.then(() => {console.log('open the INV modal')}, () => {console.log('open the INV modal outside')});
    };

    demoApp.isSponsor = (typeText) => {
        console.log(typeText);
        console.log(typeText.includes('Sponsor'));
        return typeText.includes('Sponsor');
    }

    demoApp.isProvider = (typeText) => {
        console.log(typeText);
        console.log(typeText.includes('SolutionProvider'));
        return typeText.includes('SolutionProvider');
    }

    demoApp.isCTO = (typeText) => {
        console.log(typeText);
        console.log(typeText.includes('CTO'));
        return typeText.includes('CTO');
    }

    /** Displays the cash issuance modal. */
    demoApp.openIssueCashModal = () => {
        const issueCashModal = $uibModal.open({
            templateUrl: 'issueCashModal.html',
            controller: 'IssueCashModalCtrl',
            controllerAs: 'issueCashModal',
            resolve: {
                apiBaseURL: () => apiBaseURL
            }
        });

        issueCashModal.result.then(() => {}, () => {});
    };

    /** Displays the IOU transfer modal. */
    demoApp.openTransferModal = (id) => {
        const transferModal = $uibModal.open({
            templateUrl: 'transferModal.html',
            controller: 'TransferModalCtrl',
            controllerAs: 'transferModal',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                peers: () => peers,
                id: () => id
            }
        });

        transferModal.result.then(() => {}, () => {});
    };

    /** Displays the IOU settlement modal. */
    demoApp.openSettleModal = (id) => {
        const settleModal = $uibModal.open({
            templateUrl: 'settleModal.html',
            controller: 'SettleModalCtrl',
            controllerAs: 'settleModal',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                id: () => id
            }
        });

        settleModal.result.then(() => {}, () => {});
    };

    /** Refreshes the front-end. */
    setInterval(() => {
        // Update the list of IOUs.
        $http.get(apiBaseURL + "obligations").then((response) => demoApp.ious =
            Object.keys(response.data).map((key) => response.data[key].state.data));

        // Update the cash balances.
        $http.get(apiBaseURL + "cash-balances").then((response) => demoApp.cashBalances =
            response.data);

        console.log('>>>>>>>>>> CASH BALANCES:' + demoApp.cashBalances);
        // Update list of cash issues.
        $http.get(apiBaseURL + "cash").then((response) => demoApp.cash =
            response.data);
        /*if(demoApp.cash) {
            let issuedBudgetList = [];
            let totalAmount = 0;
            for(let i = 0; i < demoApp.cash.length; i++) {
                let val = parseInt(demoApp.cash[i].state.data.amount.split(' ').shift())
                issuedBudgetList.push(totalAmount + val);
                totalAmount += val;
            }
            $timeout(() => {
              $scope.graphConfig.series = [{
                  name: 'Issued budget',
                  data: issuedBudgetList
              }];
            });
        }*/
        if(demoApp.cashBalances) {
            let tempCashBalance = demoApp.cashBalances.CHF.split(' ').shift();
            if(tempCashBalance === '1000000' || tempCashBalance === '1000000.00') {
                // PIE
                $timeout(() => {
                  $scope.pieConfig.series = [ {
                     name: 'Budget allocation',
                     data: [
                     {
                         name: 'budget',
                         y: 1000000,
                         color: '#8AD5E7'
                     }
                     ],
                     id: 'pie-config'
                    }];
                });

                // LINE
                $timeout(() => {
                  $scope.graphConfig.series = [{
                      name: 'Issued budget',
                      data: [0, 1000000]
                  }];
                });
            } else if(tempCashBalance === '500000' || tempCashBalance === '500000.00'){
                // PIE
                $timeout(() => {
                  $scope.pieConfig.series = [ {
                     name: 'Budget allocation',
                     data: [
                     {
                         name: 'feature',
                         y: 500000,
                         color: '#E94B3B'
                     },
                     {
                          name: 'budget',
                          y: 500000,
                          color: '#8AD5E7'
                     }],
                     id: 'pie-config'
                    }];
                });

                // LINE
                $timeout(() => {
                  $scope.graphConfig.series = [{
                      name: 'Issued budget',
                      data: [0, 1000000, 500000]
                  }];
                });
            }
        }
    }, 2000);

    // Highcharts Pie Chart
    $scope.pieConfig = {
      chart: {
        type: 'pie'
      },
      tooltip: {
          pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
          pie: {
              allowPointSelect: true,
              cursor: 'pointer',
              dataLabels: {
                  enabled: false
              },
              showInLegend: true
          }
      },
      series: [{
        name: 'Budget allocation',
        data: [],
        id: 'pie-config'
      }],
      title: {
        text: 'Budget allocation'
      },
      responsive: {
        rules: [{

            condition: {
                maxWidth: 300
            },
            chartOptions: {
                legend: {
                    layout: 'horizontal',
                    align: 'center',
                    verticalAlign: 'bottom'
                }
            }
        }]
    }
    }

    // Highcharts line chart
    $scope.graphConfig = {
      chart: {
        type: 'line'
      },
      tooltip: {
          pointFormat: '{series.name}: <b>{point.y}</b>'
      },
      title: {
          text: 'Total budget'
      },
      xAxis: {
          title: {
              text: 'Time'
          },
          labels: {
            enabled: false
          }
      },
      yAxis: {
          title: {
              text: 'Amount'
          }
      },
      legend: {
          layout: 'vertical',
          align: 'right',
          verticalAlign: 'middle'
      },

      plotOptions: {
          series: {
              label: {
                  connectorAllowed: false
              }
          }
      },

      responsive: {
          rules: [{
              condition: {
                  maxWidth: 300
              },
              chartOptions: {
                  legend: {
                      layout: 'horizontal',
                      align: 'center',
                      verticalAlign: 'bottom'
                  }
              }
          }]
      }
    }
});

// Causes the webapp to ignore unhandled modal dismissals.
angular.module('demoAppModule').config(['$qProvider', function($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);