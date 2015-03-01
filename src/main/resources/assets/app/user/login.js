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
        username: ko.observable(),
        password: ko.observable(),
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (userModel.userid && userModel.token) {
                document.location.href = "/#user";
                window.location.reload(true);
            }
        },

        doLogin: function () {
            var userModel = {
                "username": this.username(),
                "password": CryptoJS.SHA256(this.username() + "|" + this.password()).toString()
            };

            var url = this.urlRoot + '/api/session';

            return http.post(url, userModel).then(
                function (response) {
                    app.trigger("loggedin", true, response);
                    document.location.href = "/#user";
                    window.location.reload(true);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    app.trigger("loggedin", false);
                });
        }
    };
});