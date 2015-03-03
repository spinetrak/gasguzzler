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
        logoutScreen: ko.observable('user/logout'),
        
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                app.trigger("loggedin", false);
                document.location.href = "/#user";
                window.location.reload(true);
            }

            var url = this.urlRoot + '/api/user/' + userModel.userid;

            var that = this;

            return http.get(url, '', userModel).then(function (response) {
                    that.userid(response.userid);
                    that.username(response.username);
                    that.email(response.email);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    app.trigger("loggedin", false);
                });
        },


        doChangeProfile: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token"),
                "username": this.username(),
                "email": this.email(),
                "password": CryptoJS.SHA256(this.username() + "|" + this.password()).toString()
            };

            var url = this.urlRoot + '/api/user/' + userModel.userid;

            var that = this;
            return http.put(url, userModel, userModel).then(
                function (response) {
                    app.showMessage("Profile updated for " + that.username() + " (" + that.email() + ")!", "Profile updated!", ["Ok"], true, {"class": "notice success"});
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        }
    };
});