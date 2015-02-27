/**
 * Created by spinetrak on 26/02/15.
 */

define(function (require) {
    var http = require('plugins/http'),
        app = require('durandal/app'),
        router = require('plugins/router'),
        system = require('durandal/system'),
        shell = require('services/shell');

    return {
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                this.setLoggedIn(false);
                document.location.href = "/#user";
                window.location.reload(true);
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

        doLogout: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var url = this.urlRoot + '/api/session';

            var that = this;

            return http.remove(url, '', userModel).then(
                function (response) {
                    that.setLoggedIn(false);
                    document.location.href = "/user";
                    window.location.reload(true);
                },
                function (error) {
                    app.showDialog(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
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
                    that.setLoggedIn(false);
                    document.location.href = "/#user";
                    window.location.reload(true);
                },
                function (error) {
                    console.log(error);
                    that.setLoggedIn(false);
                    document.location.href = "/#user";
                    window.location.reload(true);
                });
        }
    };
});