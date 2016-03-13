/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 spinetrak
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
        userid: ko.observable(),
        username: ko.observable(),
        email: ko.observable(),
        password: ko.observable(),
        logoutScreen: ko.observable('user/logout'),

        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "Authorization" : "Bearer " +  sessionStorage.getItem("token"),
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                app.trigger("loggedin", false);
                document.location.href = "/#user";
                window.location.reload(true);
            }

            var url = this.urlRoot + '/api/user/' + userModel.userid;

            var that = this;

            return http.get(url, '', userModel).then(function (response) {
                    that.userid(response.userid);
                    that.username(response.username);
                    that.email(response.email);
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    app.trigger("loggedin", false);
                    document.location.href = "/#user";
                    window.location.reload(true);
                });
        },


        doChangeProfile: function () {

            var mypassword = this.password();
            var myemail = this.email();
            var myusername = this.username();

            var dataModel = {
                "username": myusername,
                "email": myemail,
                "password": CryptoJS.SHA256(myusername + "|" + mypassword).toString(),
                "userid": sessionStorage.getItem("userid")
            };
            var headerModel = {
                "Authorization" : "Bearer " +  sessionStorage.getItem("token"),
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!myusername || myusername.length < 3 || !mypassword || mypassword.length < 6 || !myemail || myemail.length < 5 || !this.doValidateEmail(myemail)) {
                app.showMessage("Please make sure that you have entered a valid username, email, and password", "Error!", ["Ok"], true, {"class": "notice error"});
                return;
            }


            var url = this.urlRoot + '/api/user/' + dataModel.userid;

            var that = this;
            return http.put(url, dataModel, headerModel).then(
                function (response) {
                    app.showMessage("Profile updated for " + that.username() + " (" + that.email() + ")!", "Profile updated!", ["Ok"], true, {"class": "notice success"});
                },
                function (error) {
                    app.showMessage(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"}).then(function () {
                        app.trigger("loggedin", false);
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