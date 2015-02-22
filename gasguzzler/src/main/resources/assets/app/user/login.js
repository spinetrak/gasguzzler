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
            var that = this;
            var loginModel = {
                "username": this.l_username,
                "password": CryptoJS.SHA256(this.r_username+"|"+this.r_email+"|"+this.r_password).toString()
            };

            var url = 'https://localhost:8443/api/session';

            return http.post(url, loginModel).then(
                function (response) {
                    app.showMessage("Hello, and welcome back!", "Hi!", ["Ok"], true, {"class": "notice success"});

                    sessionStorage.setItem("token", response.token);
                    sessionStorage.setItem("userid", response.userid);

                    system.log(sessionStorage.getItem("userid"));
                    
                    shell.reconfigure();

                    shell.router.navigate('profile');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});

                });
        },

        doRegister: function () {
            var that = this;
            var registerModel = {
                "username": this.r_username,
                "password": CryptoJS.SHA256(this.r_username+"|"+this.r_email+"|"+this.r_password).toString(),
                "email": this.r_email
            };
            var url = 'https://localhost:8443/api/registration';

            return http.post(url, registerModel).then(
                function (response) {
                    app.showMessage("Hello, and have a great day!", "Welcome!", ["Ok"], true, {"class": "notice success"});

                    sessionStorage.setItem("token", response.token);
                    sessionStorage.setItem("userid", response.userid);

                    system.log(sessionStorage.getItem("userid"));

                    shell.reconfigure();

                    shell.router.navigate('profile');
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                });
        }
    };
});