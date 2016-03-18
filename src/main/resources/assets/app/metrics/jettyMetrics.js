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
        twoxxCountDps: [],
        fourxxCountDps: [],
        fivexxCountDps: [],
        twoxxRateDps: [],
        fourxxRateDps: [],
        fivexxRateDps: [],
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),
        startMyJetty: [],
        updateChart: function () {

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

            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.2xx-responses/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.twoxxCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.4xx-responses/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.fourxxCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.4xx-responses/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.fivexxCountDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.2xx-responses/rates', '', userModel).then(function (response) {
                    that.updateSeries(that.twoxxRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.5xx-responses/rates', '', userModel).then(function (response) {
                    that.updateSeries(that.fourxxRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            http.get(that.urlRoot + '/api/metrics/io.dropwizard.jetty.MutableServletContextHandler.5xx-responses/rates', '', userModel).then(function (response) {
                    that.updateSeries(that.fivexxRateDps, response);
                },
                function (error) {
                    that.handleError();
                });
            window.clearInterval(startMyJetty);
        },

        handleError: function () {
            app.trigger("loggedin", false);
            document.location.href = "/#user";
            window.location.reload(true);
        },

        updateSeries: function (theArray, theData) {
            Object.deepExtend(theArray, theData);
            this.chart().render();
        },

        activate: function () {
            myJettyMetrics = this;
            startMyJetty = setInterval(function () {
                myJettyMetrics.updateChart()
            }, 1000);
            setInterval(function () {
                myJettyMetrics.updateChart()
            }, 60000);
        },

        chart: function () {
            return new CanvasJS.Chart("jettyChart", {
                zoomEnabled: true,
                panEnabled: true,
                title: {
                    text: "Jetty Stats"
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
                    name: "2xx (count)",
                    xValueType: "dateTime",
                    dataPoints: this.twoxxCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "4xx (count)",
                    xValueType: "dateTime",
                    dataPoints: this.fourxxCountDps
                }, {
                    type: "stackedColumn",
                    showInLegend: true,
                    name: "5xx (count)",
                    xValueType: "dateTime",
                    dataPoints: this.fivexxCountDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "2xx (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.twoxxRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "4xx (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.fourxxRateDps
                }, {
                    type: "line",
                    axisYType: "secondary",
                    showInLegend: true,
                    name: "5xx (rate)",
                    xValueType: "dateTime",
                    dataPoints: this.fivexxRateDps
                }]
            });
        }
    }
});