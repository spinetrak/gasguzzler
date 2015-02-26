/**
 * Created by spinetrak on 26/02/15.
 */

define(function (require) {
    var http = require('plugins/http'),
        app = require('durandal/app'),
        router = require('plugins/router'),
        system = require('durandal/system'),
        shell = require('services/shell'),
        ko = require('knockout');

    return {
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                this.setLoggedIn(false);
                app.showMessage("You don't appear to be already logged in.", "Not logged in", ["Ok"], true, {"class": "notice error"});
                router.navigate('user/user');
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
                    //
                    app.showMessage("See you next time!", "You are logged out!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(false);
                    router.navigate('user/user');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(false);
                    router.navigate('user/user');
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

                    app.showMessage("Your account has been deleted!", "Bye bye!", ["Ok"], true, {"class": "notice success"});
                    that.setLoggedIn(false);
                    router.navigate('user/user');
                },
                function (error) {
                    app.showMessage(error.statusText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    that.setLoggedIn(false);
                    router.navigate('user/user');
                });
        }
    };
});