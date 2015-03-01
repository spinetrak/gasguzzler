/**
 * Created by spinetrak on 26/02/15.
 */

define(function (require) {
    var http = require('plugins/http'),
        app = require('durandal/app'),
        system = require('durandal/system'),
        shell = require('services/shell'),
        router = require('plugins/router'),
        ko = require('knockout');


    var loginStatusSubscription = app.on('loggedin').then(function (loggedin, response) {
        if (loggedin) {
            if (response) {
                sessionStorage.setItem("token", response.token);
                sessionStorage.setItem("userid", response.userid);
            }
        }
        else {
            sessionStorage.removeItem("token");
            sessionStorage.removeItem("userid");
        }
    });
        
    return {
        loginScreen: ko.observable(),
        registerScreen: ko.observable(),
        profileScreen: ko.observable(),
        
        activate: function () {

            if (sessionStorage.getItem("userid") && sessionStorage.getItem("token")) {
                this.profileScreen('user/profile');
                this.loginScreen('');
                this.registerScreen('');
            }
            else {
                this.loginScreen('user/login');
                this.registerScreen('user/register');
                this.profileScreen('');
            }
        }
    };
});