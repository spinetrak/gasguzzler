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
        infoCountDps: [],
        warnCountDps: [],
        errorCountDps: [],
        infoRateDps: [],
        warnRateDps: [],
        errorRateDps: [],
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),
        startMyLog: [],
        updateLogChart: function () {

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

            http.get(that.urlRoot + '/api/metrics/ch.qos.logback.core.Appender.info/counts', '', userModel).then(function (response) {
                    that.updateLogSeries(that.infoCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/ch.qos.logback.core.Appender.warn/counts', '', userModel).then(function (response) {
                    that.updateLogSeries(that.warnCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/ch.qos.logback.core.Appender.error/counts', '', userModel).then(function (response) {
                    that.updateLogSeries(that.errorCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/ch.qos.logback.core.Appender.info/rates', '', userModel).then(function (response) {
                    that.updateLogSeries(that.infoRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/ch.qos.logback.core.Appender.warn/rates', '', userModel).then(function (response) {
                    that.updateLogSeries(that.warnRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/ch.qos.logback.core.Appender.error/rates', '', userModel).then(function (response) {
                    that.updateLogSeries(that.errorRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            window.clearInterval(startMyLog);
        },

        handleError: function () {
            app.trigger("loggedin", false);
            document.location.href = "/#user";
            window.location.reload(true);
        },

        updateLogSeries: function (theArray, theData) {
            Object.deepExtend(theArray, theData);
            this.logChart().render();
        },

        activate: function () {
            myLogMetrics = this;
            startMyLog = setInterval(function () {
                myLogMetrics.updateLogChart()
            }, 1000);
            setInterval(function () {
                myLogMetrics.updateLogChart()
            }, 60000);
        },

        logChart: function () {
            return new CanvasJS.Chart("logChart", {
                zoomEnabled: true,
                panEnabled: true,
                title: {
                    text: "Log Stats"
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
                    name: "info (count)",
                    xValueType: "dateTime",
                    dataPoints: this.infoCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "warn (count)",
                    xValueType: "dateTime",
                    dataPoints: this.warnCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "error (count)",
                    xValueType: "dateTime",
                    dataPoints: this.errorCountDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "info (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.infoRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "warn (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.warnRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "error (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.errorRateDps
                }]
            });
        }
    }
});