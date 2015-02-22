define(function (require) {
    var http = require('plugins/http'),
        app = require('durandal/app'),
        system = require('durandal/system'),
        shell = require('services/shell'),
        ko = require('knockout');

    return {
        userid: ko.observable(),
        username: ko.observable(),
        email: ko.observable(),

        activate: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };
            var that = this;
            var myuserid = sessionStorage.getItem("userid");
            var urlRoot = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
            var url = urlRoot + '/api/user/' + myuserid;

            return http.get(url, '', userModel).then(function (response) {
                that.userid = (response.userid);
                that.username = (response.username);
                that.email = (response.email);
            });
        },

        doChangeUsername: function () {

        },
        doChangeEmail: function () {

        },
        doChangePassword: function () {

        },

        doLogout: function () {

            var logoutModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var urlRoot = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
            var url = urlRoot + '/api/session';

            return http.remove(url, '', logoutModel).then(
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

            var urlRoot = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
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