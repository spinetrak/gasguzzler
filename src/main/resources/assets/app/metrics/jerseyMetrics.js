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
        getCountDps: [],
        headCountDps: [],
        postCountDps: [],
        putCountDps: [],
        getRateDps: [],
        headRateDps: [],
        postRateDps: [],
        putRateDps: [],
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),
        startMyJersey: [],
        updateJerseyChart: function () {

            var userModel = {
                "userid": sessionStorage.getItem("userid"),
                "token": sessionStorage.getItem("token")
            };

            if (!(userModel.userid && userModel.token)) {
                app.trigger("loggedin", false);
                document.location.href = "/#user";
                window.location.reload(true);
            }

            var that = this;

            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.get-requests/counts', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.getCountDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.head-requests/counts', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.headCountDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.post-requests/counts', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.postCountDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.put-requests/counts', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.putCountDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.get-requests/rates', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.getRateDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.head-requests/rates', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.headRateDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.post-requests/rates', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.postRateDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.put-requests/rates', '', userModel).then(function (response) {
                    that.updateJerseySeries(that.putRateDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            window.clearInterval(startMyJersey);
        },


        updateJerseySeries: function (theArray, theData) {
            Object.deepExtend(theArray, theData);
            this.jerseyChart().render();
        },

        activate: function () {
            myJerseyMetrics = this;
            startMyJersey = setInterval(function () {
                myJerseyMetrics.updateJerseyChart()
            }, 1000);
            setInterval(function () {
                myJerseyMetrics.updateJerseyChart()
            }, 60000);
        },

        jerseyChart: function () {
            return new CanvasJS.Chart("jerseyChart", {
                zoomEnabled: true,
                panEnabled: true,
                title: {
                    text: "Jersey Stats"
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
                    name: "get (count)",
                    xValueType: "dateTime",
                    dataPoints: this.getCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "head (count)",
                    xValueType: "dateTime",
                    dataPoints: this.headCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "post (count)",
                    xValueType: "dateTime",
                    dataPoints: this.postCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "put (count)",
                    xValueType: "dateTime",
                    dataPoints: this.putCountDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "get (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.getRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "head (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.headRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "post (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.postRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "put (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.putRateDps
                }]
            });
        }
    }
});