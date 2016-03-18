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
        canvasjs = require('canvasjs');

    Object.deepExtend = function (destination, source) {
        for (var property in source) {
            if (typeof source[property] === "object" &&
                source[property] !== null) {
                destination[property] = destination[property] || {};
                arguments.callee(destination[property], source[property]);
            } else {
                destination[property] = source[property];
            }
        }
        return destination;
    };


    return {
        selectCountDps: [],
        updateCountDps: [],
        insertCountDps: [],
        deleteCountDps: [],
        selectRateDps: [],
        updateRateDps: [],
        insertRateDps: [],
        deleteRateDps: [],
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),
        startMyUserDAO: [],
        updateUserDAOChart: function () {

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

            var that = this;

            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.select/counts', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.selectCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.update/counts', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.updateCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.insert/counts', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.insertCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.delete/counts', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.deleteCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.select/rates', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.selectRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.update/rates', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.updateRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.insert/rates', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.insertRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/net.spinetrak.gasguzzler.dao.UserDAO.delete/rates', '', userModel).then(function (response) {
                    that.updateUserDAOSeries(that.deleteRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            window.clearInterval(startMyUserDAO);
        },

        handleError: function () {
            app.trigger("loggedin", false);
            document.location.href = "/#user";
            window.location.reload(true);
        },

        updateUserDAOSeries: function (theArray, theData) {
            Object.deepExtend(theArray, theData);
            this.userDAOChart().render();
        },

        activate: function () {
            myUserDAOMetrics = this;
            startMyUserDAO = setInterval(function () {
                myUserDAOMetrics.updateUserDAOChart()
            }, 1000);
            setInterval(function () {
                myUserDAOMetrics.updateUserDAOChart()
            }, 60000);
        },

        userDAOChart: function () {
            return new CanvasJS.Chart("userDAOChart", {
                zoomEnabled: true,
                panEnabled: true,
                title: {
                    text: "UserDAO Stats"
                },
                axisX: {
                    title: "Time",
                    valueFormatString: "YYYY-MM-DD HH:mm:ss"
                },
                axisY: {
                    title: "Accumulative Request Count",
                    gridColor: "red",
                    minimum: 0
                },
                axisY2: {
                    title: "Count/Second",
                    minimum: 0
                },
                data: [{
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "select (count)",
                    xValueType: "dateTime",
                    dataPoints: this.selectCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "update (count)",
                    xValueType: "dateTime",
                    dataPoints: this.updateCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "insert (count)",
                    xValueType: "dateTime",
                    dataPoints: this.insertCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "delete (count)",
                    xValueType: "dateTime",
                    dataPoints: this.deleteCountDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "select (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.selectRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "update (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.updateRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "insert (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.insertRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "delete (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.deleteRateDps
                }]
            });
        }
    }
});