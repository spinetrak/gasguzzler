define(function (require) {
    var http = require('plugins/http'),
        app = require('durandal/app'),
        system = require('durandal/system'),
        ko = require('knockout'),
        shell = require('services/shell');


    return {
        l_username: ko.observable(),
        l_password: ko.observable(),
        r_username: ko.observable(),
        r_password: ko.observable(),
        r_email: ko.observable(),


        doLogin: function () {

            var username = this.l_username();
            var password = this.l_password();
            var sha256Password = CryptoJS.SHA256(username + "|" + password).toString();

            system.log(sha256Password);
            
            var loginModel = {
                "username": username,
                "password": sha256Password
            };
            
            var urlRoot = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
            var url = urlRoot + '/api/session';

            return http.post(url, loginModel).then(
                function (response) {
                    sessionStorage.setItem("token", response.token);
                    sessionStorage.setItem("userid", response.userid);

                    system.log(sessionStorage.getItem("userid"));
                    
                    shell.reconfigure();

                    shell.router.navigate('');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        },

        doRegister: function () {

            var username = this.r_username();
            var password = this.r_password();
            var email = this.r_email();
            var sha256Password = CryptoJS.SHA256(username + "|" + password).toString();
            
            var registerModel = {
                "username": username,
                "password": sha256Password,
                "email": email
            };
            var urlRoot = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
            var url = urlRoot + '/api/registration';

            return http.post(url, registerModel).then(
                function (response) {

                    sessionStorage.setItem("token", response.token);
                    sessionStorage.setItem("userid", response.userid);

                    system.log(sessionStorage.getItem("userid"));

                    shell.reconfigure();

                    shell.router.navigate('');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        }
    };
});