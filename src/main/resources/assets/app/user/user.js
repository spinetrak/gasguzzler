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

        showLoginForm: ko.observable(true),
        showRegisterForm: ko.observable(true),
        showProfileForm: ko.observable(false),

        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                this.setLoggedIn(false);
                return;
            }
            var url = this.urlRoot + '/api/user/' + userModel.userid;

            var that = this;
            
            return http.get(url, '', userModel).then(function (response) {
                    that.userid = (response.userid);
                    that.username = (response.username);
                    that.email = (response.email);
                    that.setLoggedIn(true);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(false);
                });
        },

        setLoggedIn: function (loggedIn, response) {
            if (loggedIn) {
                this.showLoginForm(false);
                this.showRegisterForm(false);
                this.showProfileForm(true);
                if (response) {
                    sessionStorage.setItem("token", response.token);
                    sessionStorage.setItem("userid", response.userid);
                }
            }
            else {
                this.showLoginForm(true);
                this.showRegisterForm(true);
                this.showProfileForm(false);
                sessionStorage.removeItem("token");
                sessionStorage.removeItem("userid");
            }
        },

        doLogin: function () {
            var userModel = {
                "username": this.username,
                "password": CryptoJS.SHA256(this.username + "|" + this.password()).toString()
            };

            var url = this.urlRoot + '/api/session';

            var that = this;

            return http.post(url, userModel).then(
                function (response) {
                    app.showMessage("Welcome back, " + that.username() + "!", "Welcome!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(true, response);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(false);
                });
        },

        doRegister: function () {

            var userModel = {
                "username": this.username,
                "password": CryptoJS.SHA256(this.username + "|" + this.password()).toString(),
                "email": this.email
            };
            var url = this.urlRoot + '/api/registration';

            var that = this;
            return http.post(url, userModel).then(
                function (response) {
                    app.showMessage("Welcome, " + that.username() + " (" + that.email() + ")!", "Welcome!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(true, response);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(false);
                });
        },

        doChangeProfile: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token"),
                "username": this.username,
                "email": this.email,
                "password": CryptoJS.SHA256(this.username + "|" + this.password()).toString()
            };

            var url = this.urlRoot + '/api/user/' + userModel.userid;

            var that = this;
            return http.put(url, userModel, userModel).then(
                function (response) {
                    app.showMessage("Profile updated for " + that.username() + " (" + that.email() + ")!", "Profile updated!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(true);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        },


        doLogout: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var url = this.urlRoot + '/api/session';

            var that = this;

            return http.remove(url, '', userModel).then(
                function (response) {
                    app.showMessage("See you next time, " + that.username() + "!", "You are logged out!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(false);
                    that.showRegisterForm(false);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(true);
                });
        },

        doDelete: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var url = this.urlRoot + '/api/registration';

            var that = this;
            return http.remove(url, '', userModel).then(
                function (response) {

                    app.showMessage("Your account has been deleted!", "Bye bye, " + that.username() + "!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(false);
                },
                function (error) {
                    app.showMessage(error.statusText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(true);
                });
        }
    };
});