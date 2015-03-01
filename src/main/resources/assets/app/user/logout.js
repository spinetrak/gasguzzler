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
                app.trigger("loggedin", false);
                document.location.href = "/#user";
                window.location.reload(true);
            }
        },

        doLogout: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var url = this.urlRoot + '/api/session';

            return http.remove(url, '', userModel).then(
                function (response) {
                    app.trigger("loggedin", false);
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

            return http.remove(url, '', userModel).then(
                function (response) {
                    app.trigger("loggedin", false);
                    document.location.href = "/#user";
                    window.location.reload(true);
                },
                function (error) {
                    console.log(error);
                    app.trigger("loggedin", false);
                    document.location.href = "/#user";
                    window.location.reload(true);
                });
        }
    };
});