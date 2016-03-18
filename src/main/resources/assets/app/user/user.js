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
        shell = require('services/shell'),
        router = require('plugins/router'),
        ko = require('knockout');


    var loginStatusSubscription = app.on('loggedin').then(function (loggedin, response) {
        if (loggedin) {
            if (response) {
                sessionStorage.setItem("token", response.token);
                sessionStorage.setItem("userid", response.userid);
                sessionStorage.removeItem("forgotPassword");
            }
        }
        else {
            sessionStorage.removeItem("token");
            sessionStorage.removeItem("userid");
        }
    });

    var forgotPasswordSubscription = app.on('forgotPassword').then(function (forgotPassword) {
        if (forgotPassword) {
            sessionStorage.setItem("forgotPassword", "true");
            sessionStorage.removeItem("token");
            sessionStorage.removeItem("userid");
        }
        else {
            sessionStorage.removeItem("forgotPassword");
        }
    });


    return {
        loginScreen: ko.observable(),
        registerScreen: ko.observable(),
        profileScreen: ko.observable(),
        forgotPasswordScreen: ko.observable(),
        resetPasswordScreen: ko.observable(),
        getURLParams: function (name) {
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(window.location.hash) || [, ""])[1].replace(/\+/g, '%20')) || null
        },

        activate: function () {
            if (sessionStorage.getItem("userid") && sessionStorage.getItem("token")) {
                this.profileScreen('user/profile');
                this.loginScreen('');
                this.registerScreen('');
                this.forgotPasswordScreen('');
            }
            else {
                if (sessionStorage.getItem("forgotPassword")) {
                    this.forgotPasswordScreen('user/forgotPassword');
                    this.loginScreen('');
                    this.registerScreen('');
                    this.profileScreen('');
                }
                else {
                    var token = this.getURLParams('t');
                    var userid = this.getURLParams('i');
                    if (token && userid) {
                        var userModel = {
                            "userid": userid,
                            "token": token
                        };
                        app.trigger("loggedin", true, userModel);
                        document.location.href = "/#user";
                        window.location.reload(true);
                    }
                    else {
                        this.loginScreen('user/login');
                        this.registerScreen('user/register');
                        this.profileScreen('');
                        this.forgotPasswordScreen('');
                    }
                }
            }
        }
    };
});