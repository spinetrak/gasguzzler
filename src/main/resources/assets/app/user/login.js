/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 spinetrak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

            var mypassword = this.password();
            var myusername = this.username();
            
            var userModel = {
                "username": myusername,
                "password": CryptoJS.SHA256(myusername + "|" + mypassword).toString()
            };

            if (!myusername || myusername.length < 3 || !mypassword || mypassword.length < 6) {
                app.showMessage("Please make sure that you have entered a valid username and password", "Error!", ["Ok"], true, {"class": "notice error"});
                return;
            }

            var url = this.urlRoot + '/api/login';

            return http.post(url, userModel).then(
                function (response) {
                    app.showMessage("Welcome back!", "Hello!", ["Ok"], true, {"class": "notice success"}).then(function () {
                        app.trigger("loggedin", true, response);
                        document.location.href = "/#user";
                        window.location.reload(true);
                    });
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"}).then(function () {
                        app.trigger("loggedin", false);
                    });
                });
        },
        doForgotPassword: function () {
            app.trigger("forgotPassword", true);
            document.location.href = "/#user";
            window.location.reload(true);
        }
    };
});