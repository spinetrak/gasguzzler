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
                app.trigger("loggedin", false);
                document.location.href = "/#user";
                window.location.reload(true);
            }
        },


        doRegister: function () {

            var userModel = {
                "username": this.username(),
                "password": CryptoJS.SHA256(this.username() + "|" + this.password()).toString(),
                "email": this.email()
            };
            var url = this.urlRoot + '/api/user';

            return http.post(url, userModel).then(
                function (response) {
                    app.trigger("loggedin", true, response);
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
