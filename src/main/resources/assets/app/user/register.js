/**
 * Created by spinetrak on 26/02/15.
 */

define(function (require) {
    var http = require('plugins/http'),
        app = require('durandal/app'),
        system = require('durandal/system'),
        router = require('plugins/router'),
        shell = require('services/shell'),
        ko = require('knockout');

    return {
        username: ko.observable(),
        email: ko.observable(),
        password: ko.observable(),
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (userModel.userid && userModel.token) {
                this.setLoggedIn(false);
                app.showMessage("You appear to be already registered and logged in.", "Already registered", ["Ok"], true, {"class": "notice error"});
                router.navigate('user/profile');
            }
        },

        setLoggedIn: function (loggedIn, response) {
            if (loggedIn) {
                if (response) {
                    sessionStorage.setItem("token", response.token);
                    sessionStorage.setItem("userid", response.userid);
                }
            }
            else {
                sessionStorage.removeItem("token");
                sessionStorage.removeItem("userid");
            }
        },


        doRegister: function () {

            var userModel = {
                "username": this.username,
                "password": CryptoJS.SHA256(this.username + "|" + this.password).toString(),
                "email": this.email
            };
            var url = this.urlRoot + '/api/registration';

            var that = this;
            return http.post(url, userModel).then(
                function (response) {
                    app.showMessage("Welcome, " + that.username() + " (" + that.email() + ")!", "Welcome!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(true, response);
                    router.navigate('user/profile');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(false);
                });
        }
    };
});
