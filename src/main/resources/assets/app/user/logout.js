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
        router = require('plugins/router'),
        system = require('durandal/system'),
        shell = require('services/shell');

    return {
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),


        activate: function () {
            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                app.trigger("loggedin", false);
                document.location.href = "/#user";
                window.location.reload(true);
            }
        },

        doLogout: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var url = this.urlRoot + '/api/session';

            return http.remove(url, '', userModel).then(
                function (response) {
                    app.trigger("loggedin", false);
                    document.location.href = "/#user";
                    window.location.reload(true);
                },
                function (error) {
                    app.showDialog(error.responseText, error.statusText, ["Ok"], true, {"class": "notice error"});
                    app.trigger("loggedin", false);
                    document.location.href = "/#user";
                    window.location.reload(true);
                });
        },

        doDelete: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            var url = this.urlRoot + '/api/user/' + userModel.userid;

            return http.remove(url, '', userModel).then(
                function (response) {
                    app.trigger("loggedin", false);
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