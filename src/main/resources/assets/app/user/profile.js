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
        userid: ko.observable(),
        username: ko.observable(),
        email: ko.observable(),
        password: ko.observable(),

        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                this.setLoggedIn(false);
                app.showMessage("You don't appear to be already logged in.", "Not logged in", ["Ok"], true, {"class": "notice error"});
                router.navigate('user');
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
        }
    };
});