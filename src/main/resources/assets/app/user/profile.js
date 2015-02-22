define(function (require) {
    var http = require('plugins/http'),
        app = require('durandal/app'),
        system = require('durandal/system'),
        shell = require('services/shell');


    return {
    
        doLogout: function () {

            var logoutModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var urlRoot = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
            var url = urlRoot + '/api/session';

            return http.remove(url,'',logoutModel).then(
                function (response) {
                    app.showMessage("Have a nice day and please come back soon!", "Good Bye!", ["Ok"], true, {"class": "notice success"});

                    sessionStorage.removeItem("token");
                    sessionStorage.removeItem("userid");
                    
                    shell.reconfigure();

                    shell.router.navigate('login');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        },

        doDelete: function () {

            var deleteModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var urlRoot = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
            var url = urlRoot + '/api/registration';

            return http.remove(url, '', deleteModel).then(
                function (response) {
                    app.showMessage("Sorry to see you go! Have a nice day!!", "Good Bye!", ["Ok"], true, {"class": "notice success"});

                    sessionStorage.removeItem("token");
                    sessionStorage.removeItem("userid");
                    
                    shell.reconfigure();

                    shell.router.navigate('login');
                },
                function (error) {
                    app.showMessage("Delete: " + error.statusText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        }
    };
});