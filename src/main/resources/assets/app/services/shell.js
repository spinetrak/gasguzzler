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
    var router = require('plugins/router');

    var adminArr = [
        {route: '', title: 'Home', moduleId: 'home/home', nav: true},
        {route: 'stats', title: 'Stats', moduleId: 'site/stats', nav: true},
        {route: 'user', title: 'You', moduleId: 'user/user', nav: true},
        {route: 'user', title: 'Profile', moduleId: 'user/profile', nav: false},
        {route: 'user', title: 'Your Account', moduleId: 'user/account', nav: false},
        {route: 'user', title: 'Admin Area', moduleId: 'user/admin', nav: false},
        {route: 'metrics', title: 'Metrics', moduleId: 'metrics/metrics', nav: true}
    ];

    var userArr = [
        {route: '', title: 'Home', moduleId: 'home/home', nav: true},
        {route: 'stats', title: 'Stats', moduleId: 'site/stats', nav: true},
        {route: 'user', title: 'You', moduleId: 'user/user', nav: true},
        {route: 'user', title: 'Profile', moduleId: 'user/profile', nav: false},
        {route: 'user', title: 'Your Account', moduleId: 'user/account', nav: false}
    ];


    return {
        router: router,
        activate: function () {

            var userid = sessionStorage.getItem("userid");
            var token = sessionStorage.getItem("token");

            if(userid && token && token != "") {
                router.map(adminArr).buildNavigationModel();
            }
            else
            {
                router.map(userArr).buildNavigationModel();
            }
            return router.activate();
        }
    };
});