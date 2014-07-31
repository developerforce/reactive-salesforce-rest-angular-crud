var app = angular.module("ngApp", ["ngGrid", "ngResource"]);

app.controller('Contacts', function($scope, $resource) {

  $scope.initialized = false;

  $scope.loggedIn = false;

  $scope.message = undefined;

  $scope.selectedContact = undefined;

  $scope.selectedContacts = [];

  var ContactsService = $resource("/contacts/:id", { id: "@Id" }, { update: { method: 'PUT' } });

  $scope.contacts = ContactsService.query(function() {
    // logged in
    $scope.initialized = true;
    $scope.loggedIn = true;
  }, function() {
    // error
    $scope.initialized = true;
  });

  $scope.gridOptions = {
    data: 'contacts',
    multiSelect: false,
    selectedItems: $scope.selectedContacts,
    primaryKey: 'Id',
    columnDefs: [
      {field: 'FirstName', displayName: 'First', enableCellEdit: true},
      {field: 'LastName', displayName: 'Last', enableCellEdit: true}
    ]
  };

  $scope.$watchCollection(function() {
      return $scope.selectedContacts;
    },
    function (value) {
      if ((value !== undefined) && (value.length > 0)) {
        $scope.selectedContact = value[0];
      }
    }
  );

  $scope.newContact = function() {
    $scope.selectedContact = {};
  };

  $scope.saveContact = function() {
    if ($scope.selectedContact instanceof ContactsService) {
      // existing
      $scope.selectedContact.$update(function() {
        $scope.message = "Contact Updated";
        $scope.selectedContact = undefined;
        $scope.gridOptions.selectAll(false);
      },
      function(error) {
        console.error(error);
      });
    }
    else {
      // new
      var contact = new ContactsService({FirstName: $scope.selectedContact.FirstName, LastName: $scope.selectedContact.LastName});
      contact.$save(function() {
        $scope.message = "Contact Saved";
        $scope.selectedContact = undefined;
        $scope.contacts = ContactsService.query();
      },
      function(error) {
        console.error(error);
      });
    }

  };

  $scope.deleteContact = function() {
    $scope.selectedContact.$delete(function() {
      $scope.message = "Contact Deleted";
      $scope.contacts = ContactsService.query();
      $scope.selectedContact = undefined;
    },
    function(error) {
      console.error(error);
    });
  };

});