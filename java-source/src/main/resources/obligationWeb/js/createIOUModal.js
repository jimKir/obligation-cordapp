"use strict";





angular.module('demoAppModule').controller('CreateCVPModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, peers, works) {
    const createCVPModal = this;

    createCVPModal.peers = peers;
    createCVPModal.works = works;
    createCVPModal.providers = peers.filter(peer => peer.includes('Provider'));
    createCVPModal.vendors = peers.filter(peer => peer.includes('PC'));
    createCVPModal.form = {};
    createCVPModal.formError = false;

    /** Validate and create an CVP. */
    createCVPModal.create = () => {
        if (invalidFormInput()) {
            createCVPModal.formError = true;
        } else {
            createCVPModal.formError = false;

            const totalAssetAmount = Math.trunc(createCVPModal.form.totalAssetAmount);
            const assetCount = createCVPModal.form.assetCount;
            const assetType = createCVPModal.form.assetType;
            const party = createCVPModal.form.counterparty;

            $uibModalInstance.close();

            // We define the CVP creation endpoint.
            const issueCVPEndpoint =
                apiBaseURL +
                // `issue-obligation?amount=${amount}&party=${party}&currency=CHF`;
                `issue-group-cto-capacity?totalAssetAmount=${totalAssetAmount}&assetCount=${assetCount}&party=${party}&assetType=${assetType}&currency=CHF`;

            // We hit the endpoint to create the CVP and handle success/failure responses.
            $http.get(issueCVPEndpoint).then(
                (result) => createCVPModal.displayMessage(result),
                (result) => createCVPModal.displayMessage(result)
            );
        }
    };

    createCVPModal.populateForm = () => {
        let worksforSelectedProvider = createCVPModal.works.filter(work => work.state.data.borrower.includes(createCVPModal.form.counterparty))
        console.log(worksforSelectedProvider);

        createCVPModal.form.amount = worksforSelectedProvider[0].state.data.amount.replace(' CHF', '');
        createCVPModal.form.description = worksforSelectedProvider[0].state.data.description;
        createCVPModal.form.featureTitle = worksforSelectedProvider[0].state.data.featureTitle;
    };



    /** Displays the success/failure response from attempting to create an IOU. */
    createCVPModal.displayMessage = (message) => {
        const createCVPMsgModal = $uibModal.open({
            templateUrl: 'createIOUMsgModal.html',
            controller: 'createIOUMsgModalCtrl',
            controllerAs: 'createIOUMsgModal',
            resolve: {
                message: () => message
            }
        });

        // No behaviour on close / dismiss.
        createCVPMsgModal.result.then(() => {}, () => {});
    };

    /** Closes the CVP creation modal. */
    createCVPModal.cancel = () => $uibModalInstance.dismiss();

    // Validates the CVP.
    function invalidFormInput() {
        return isNaN(createIOUModal.form.totalAssetAmount) || (createIOUModal.form.counterparty === undefined);
    }
});



angular.module('demoAppModule').controller('CreatePIModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, peers, works) {
    const createPIModal = this;

    createPIModal.peers = peers;
    createPIModal.works = works;
    createPIModal.providers = peers.filter(peer => peer.includes('Provider'));
    createPIModal.workList = [];
    createPIModal.form = {};
    createPIModal.formError = false;

    /** Validate and create an PI. */
    createPIModal.create = () => {
        if (invalidFormInput()) {
            createPIModal.formError = true;
        } else {
            createPIModal.formError = false;

            const amount = Math.trunc(createPIModal.form.amount);
            const featureTitle = createPIModal.form.featureTitle;
            const description = createPIModal.form.description;
            const party = createPIModal.form.counterparty;

            $uibModalInstance.close();

            // We define the PI creation endpoint.
            const issuePIEndpoint =
                apiBaseURL +
                // `issue-obligation?amount=${amount}&party=${party}&currency=CHF`;
                `issue-obligation?amount=${amount}&featureTitle=${featureTitle}&party=${party}&description=${description}&currency=CHF&linkingId=79d1308a-6bd2-4564-a158-29bcc3a5db1b`;

            // We hit the endpoint to create the PI and handle success/failure responses.
            $http.get(issuePIEndpoint).then(
                (result) => createPIModal.displayMessage(result),
                (result) => createPIModal.displayMessage(result)
            );
        }
    };

    createPIModal.populateForm = () => {
        let worksforSelectedProvider = createPIModal.works.filter(work => work.state.data.borrower.includes(createPIModal.form.counterparty))

        for(let i = 0; i < worksforSelectedProvider.length; i++) {
            createPIModal.workList.push(worksforSelectedProvider[i].state.data.featureTitle);
            console.log('added work: ' + createPIModal.workList);
        }
    };

    createPIModal.getWorkData = () => {
        let worksforSelectedProvider = createPIModal.works.filter(work => work.state.data.borrower.includes(createPIModal.form.counterparty))
        console.log('inside getworkdata');
        for(let i = 0; i < worksforSelectedProvider.length; i++) {
            if(worksforSelectedProvider[i].state.data.featureTitle === createPIModal.form.selectedWork) {
                console.log('||>>> YES!');
                createPIModal.form.amount = worksforSelectedProvider[i].state.data.amount.replace(' CHF', '');
                createPIModal.form.description = worksforSelectedProvider[i].state.data.description;
                createIOUModal.form.featureTitle = createIOUModal.form.selectedWork;
            }
        }
    };

    /** Displays the success/failure response from attempting to create an PI. */
    createPIModal.displayMessage = (message) => {
        const createPIMsgModal = $uibModal.open({
            templateUrl: 'createIOUMsgModal.html',
            controller: 'createIOUMsgModalCtrl',
            controllerAs: 'createIOUMsgModal',
            resolve: {
                message: () => message
            }
        });

        // No behaviour on close / dismiss.
        createPIMsgModal.result.then(() => {}, () => {});
    };

    /** Closes the PI creation modal. */
    createPIModal.cancel = () => $uibModalInstance.dismiss();

    // Validates the PI.
    function invalidFormInput() {
        return isNaN(createPIModal.form.amount) || (createPIModal.form.counterparty === undefined);
    }
});


angular.module('demoAppModule').controller('CreateIOUModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, peers, works) {
    const createIOUModal = this;

    createIOUModal.peers = peers;
    createIOUModal.works = works;
    createIOUModal.providers = peers.filter(peer => peer.includes('Provider'));
    createIOUModal.workList = [];
    createIOUModal.form = {};
    createIOUModal.formError = false;

    /** Validate and create an IOU. */
    createIOUModal.create = () => {
        if (invalidFormInput()) {
            createIOUModal.formError = true;
        } else {
            createIOUModal.formError = false;

            const amount = Math.trunc(createIOUModal.form.amount);
            const featureTitle = createIOUModal.form.featureTitle;
            const description = createIOUModal.form.description;
            const party = createIOUModal.form.counterparty;

            $uibModalInstance.close();

            // We define the IOU creation endpoint.
            const issueIOUEndpoint =
                apiBaseURL +
               // `issue-obligation?amount=${amount}&party=${party}&currency=CHF`;
               `issue-obligation?amount=${amount}&featureTitle=${featureTitle}&party=${party}&description=${description}&currency=CHF&linkingId=79d1308a-6bd2-4564-a158-29bcc3a5db1b`;

            // We hit the endpoint to create the IOU and handle success/failure responses.
            $http.get(issueIOUEndpoint).then(
                (result) => createIOUModal.displayMessage(result),
                (result) => createIOUModal.displayMessage(result)
            );
        }
    };

    createIOUModal.populateForm = () => {
        let worksforSelectedProvider = createIOUModal.works.filter(work => work.state.data.borrower.includes(createIOUModal.form.counterparty))

        for(let i = 0; i < worksforSelectedProvider.length; i++) {
            createIOUModal.workList.push(worksforSelectedProvider[i].state.data.featureTitle);
            console.log('added work: ' + createIOUModal.workList);
        }
    };

    createIOUModal.getWorkData = () => {
        let worksforSelectedProvider = createIOUModal.works.filter(work => work.state.data.borrower.includes(createIOUModal.form.counterparty))
        console.log('inside getworkdata');
        for(let i = 0; i < worksforSelectedProvider.length; i++) {
            if(worksforSelectedProvider[i].state.data.featureTitle === createIOUModal.form.selectedWork) {
                createIOUModal.form.amount = worksforSelectedProvider[i].state.data.amount.replace(' CHF', '');
                createIOUModal.form.description = worksforSelectedProvider[i].state.data.description;
                createIOUModal.form.featureTitle = createIOUModal.form.selectedWork;
            }
        }
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
                `issue-work?amount=${amount}&featureTitle=${featureTitle}&party=${party}&description=${description}&currency=CHF`;
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
            templateUrl: 'createIOUMsgModal.html',
            controller: 'createIOUMsgModalCtrl',
            controllerAs: 'createIOUMsgModal',
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