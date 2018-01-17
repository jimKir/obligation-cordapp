"use strict";

angular.module('demoAppModule').controller('CreateIOUModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, peers, works) {
    const createIOUModal = this;

    createIOUModal.peers = peers;
    createIOUModal.works = works;
    createIOUModal.providers = peers.filter(peer => peer.includes('Provider'));
    createIOUModal.form = {};
    createIOUModal.formError = false;

    /** Validate and create an IOU. */
    createIOUModal.create = () => {
        if (invalidFormInput()) {
            createIOUModal.formError = true;
        } else {
            createIOUModal.formError = false;

            const amount = createIOUModal.form.amount;
            const featureTitle = createIOUModal.form.featureTitle;
            const description = createIOUModal.form.description;
            const party = createIOUModal.form.counterparty;

            $uibModalInstance.close();

            // We define the IOU creation endpoint.
            const issueIOUEndpoint =
                apiBaseURL +
                `issue-obligation?amount=${amount}&featureTitle=${featureTitle}&party=${party}&description=${description}`;

            // We hit the endpoint to create the IOU and handle success/failure responses.
            $http.get(issueIOUEndpoint).then(
                (result) => createIOUModal.displayMessage(result),
                (result) => createIOUModal.displayMessage(result)
            );
        }
    };

    createIOUModal.populateForm = () => {
        let worksforSelectedProvider = createIOUModal.works.filter(work => work.state.data.borrower.includes(createIOUModal.form.counterparty))
        console.log(worksforSelectedProvider);

        createIOUModal.form.amount = worksforSelectedProvider[0].state.data.amount.replace(' CHF', '');
        createIOUModal.form.description = worksforSelectedProvider[0].state.data.description;
        createIOUModal.form.featureTitle = worksforSelectedProvider[0].state.data.featureTitle;
    };



    /** Displays the success/failure response from attempting to create an IOU. */
    createIOUModal.displayMessage = (message) => {
        const createIOUMsgModal = $uibModal.open({
            templateUrl: 'createIOUMsgModal.html',
            controller: 'createIOUMsgModalCtrl',
            controllerAs: 'createIOUMsgModal',
            resolve: {
                message: () => message
            }
        });

        // No behaviour on close / dismiss.
        createIOUMsgModal.result.then(() => {}, () => {});
    };

    /** Closes the IOU creation modal. */
    createIOUModal.cancel = () => $uibModalInstance.dismiss();

    // Validates the IOU.
    function invalidFormInput() {
        return isNaN(createIOUModal.form.amount) || (createIOUModal.form.counterparty === undefined);
    }
});






angular.module('demoAppModule').controller('CreateWorkModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, peers) {
    const createWorkModal = this;

    createWorkModal.peers = peers;
    createWorkModal.sposnsors = peers.filter(peer => peer.includes('Sponsor'));
    createWorkModal.form = {};
    createWorkModal.formError = false;


    /** Validate and create an Work. */
    createWorkModal.create = () => {
        if (invalidFormInput()) {
            createWorkModal.formError = true;
        } else {
            createWorkModal.formError = false;

            const amount = createWorkModal.form.amount;
            const featureTitle = createWorkModal.form.featureTitle;
            const description = createWorkModal.form.description;
            const party = createWorkModal.form.counterparty;

            $uibModalInstance.close();

            // We define the Work creation endpoint.
            const issueIOUEndpoint =
                apiBaseURL +
                `issue-work?amount=${amount}&featureTitle=${featureTitle}&party=${party}&description=${description}`;
            // We hit the endpoint to create the IOU and handle success/failure responses.
            $http.get(issueIOUEndpoint).then(
                (result) => createWorkModal.displayMessage(result),
                (result) => createWorkModal.displayMessage(result)
            );
        }
    };



    /** Displays the success/failure response from attempting to create an IOU. */
    createWorkModal.displayMessage = (message) => {
        const createWorkMsgModal = $uibModal.open({
            templateUrl: 'createWorkMsgModal.html',
            controller: 'createWorkMsgModalCtrl',
            controllerAs: 'createWorkMsgModal',
            resolve: {
                message: () => message
            }
        });

        // No behaviour on close / dismiss.
        createWorkMsgModal.result.then(() => {}, () => {});
    };

    /** Closes the Work creation modal. */
    createWorkModal.cancel = () => $uibModalInstance.dismiss();

    // Validates the Work.
    function invalidFormInput() {
        return isNaN(createWorkModal.form.amount) || (createWorkModal.form.counterparty === undefined);
    }
});

// Controller for the success/fail modal.
angular.module('demoAppModule').controller('createIOUMsgModalCtrl', function($uibModalInstance, message) {
    const createIOUMsgModal = this;
    createIOUMsgModal.message = message.data;
});


// Controller for the success/fail modal.
angular.module('demoAppModule').controller('createWorkMsgModalCtrl', function($uibModalInstance, message) {
    const createWorkMsgModal = this;
    createWorkMsgModal.message = message.data;
});