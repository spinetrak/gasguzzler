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
        system = require('durandal/system'),
        router = require('plugins/router'),
        shell = require('services/shell'),
        ko = require('knockout');

    return {
        username: ko.observable(),
        email: ko.observable(),
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "Authorization" : "Bearer " +  sessionStorage.getItem("token"),
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (userModel.userid && userModel.token) {
                app.trigger("loggedin", false);
                document.location.href = "/#user";
                window.location.reload(true);
            }
        },


        doRequestPasswordReset: function () {

            var myemail = this.email();

            var userModel = {
                "email": myemail
            };

            if ((!myemail) || (myemail && (myemail.length < 5 || !this.doValidateEmail(myemail)))) {
                app.showMessage("Please make sure that you have entered a valid email address", "Error!", ["Ok"], true, {"class": "notice error"});
                return;
            }

            var url = this.urlRoot + '/api/user/pwreset';

            http.post(url, userModel).then(
                function (response) {
                    app.showMessage("Further instructions will be sent via e-mail", "Thank you!", ["Ok"], false, {"class": "notice success"}).then(function () {
                        app.trigger("forgotPassword", false);
                        document.location.href = "/#user";
                        window.location.reload(true);
                    });
                },
                function (error) {
                    console.log(error);
                    app.showMessage(error.statusText, "Error!", ["Ok"], false, {"class": "notice error"}).then(function () {
                        app.trigger("forgotPassword", false);
                        document.location.href = "/#user";
                        window.location.reload(true);
                    });
                });
        },

        doValidateEmail: function (email) {
            var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }
    };
});
