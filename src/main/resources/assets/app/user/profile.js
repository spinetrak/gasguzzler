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
        password: ko.observable(),
     
        activate: function () {

            var userid = sessionStorage.getItem("userid");
            var token = sessionStorage.getItem("token");

            var userModel = {
                "userid": userid,
                "token": token
            };
            var that = this;

            var urlRoot = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
            var url = urlRoot + '/api/user/' + userid;

            return http.get(url, '', userModel).then(function (response) {
                    that.userid = (response.userid);
                    that.username = (response.username);
                    that.email = (response.email);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        },

        doChangeProfile: function () {

            var username = this.username;
            var email = this.email;
            var password = this.password();
            password = CryptoJS.SHA256(username + "|" + password).toString();

            var userid = sessionStorage.getItem("userid");
            var token = sessionStorage.getItem("token");

            var userModel = {
                "userid": userid,
                "token": token,
                "username": username,
                "email": email,
                "password": password
            };

            var urlRoot = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
            var url = urlRoot + '/api/user/' + userid;

            return http.put(url, userModel, userModel).then(
                function (response) {
                    app.showMessage("Profile updated!", "gasguzzler", ["Ok"], true, {"class": "notice success"});
                    shell.router.navigate('profile');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
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

                    sessionStorage.removeItem("token");
                    sessionStorage.removeItem("userid");

                    shell.reconfigure();

                    shell.router.navigate('');
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

                    sessionStorage.removeItem("token");
                    sessionStorage.removeItem("userid");

                    shell.reconfigure();

                    shell.router.navigate('');
                },
                function (error) {
                    app.showMessage(error.statusText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        }
    };
});