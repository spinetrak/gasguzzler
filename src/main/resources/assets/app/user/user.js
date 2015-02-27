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