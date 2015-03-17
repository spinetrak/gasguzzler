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
        heapMaxDps: [],
        heapCommittedDps: [],
        heapUsedDps: [],
        nonHeapMaxDps: [],
        nonHeapCommittedDps: [],
        nonHeapUsedDps: [],
        urlRoot: location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : ''),
        startMyJVM: [],
        updateChart: function () {

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

            http.get(that.urlRoot + '/api/metrics/jvm.memory.heap.max/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.heapMaxDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/jvm.memory.heap.committed/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.heapCommittedDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/jvm.memory.heap.used/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.heapUsedDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/jvm.memory.non-heap.max/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.nonHeapMaxDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/jvm.memory.non-heap.committed/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.nonHeapCommittedDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            http.get(that.urlRoot + '/api/metrics/jvm.memory.non-heap.max/counts', '', userModel).then(function (response) {
                    that.updateSeries(that.nonHeapUsedDps, response);
                },
                function (error) {
                    app.showMessage("Service currently unavailable.", "Error!", ["Ok"], true, {"class": "notice error"});
                });
            window.clearInterval(startMyJVM);
        },


        updateSeries: function (theArray, theData) {
            Object.deepExtend(theArray, theData);
            this.chart().render();
        },

        activate: function () {
            myJVMMetrics = this;
            startMyJVM = setInterval(function () {
                myJVMMetrics.updateChart()
            }, 1000);
            setInterval(function () {
                myJVMMetrics.updateChart()
            }, 60000);
        },

        chart: function () {
            return new CanvasJS.Chart("jvmChart", {
                zoomEnabled: true,
                panEnabled: true,
                title: {
                    text: "JVM Stats"
                },
                axisX: {
                    title: "Time",
                    valueFormatString: "YYYY-MM-DD HH:mm:ss"
                },
                axisY: {
                    title: "Size",
                    gridColor: "red",
                    minimum: 0
                },
                data: [{
                    type: "line",
                    showInLegend: true,
                    name: "heap max",
                    xValueType: "dateTime",
                    dataPoints: this.heapMaxDps
                }, {
                    type: "line",
                    showInLegend: true,
                    name: "heap committed",
                    xValueType: "dateTime",
                    dataPoints: this.heapCommittedDps
                }, {
                    type: "line",
                    showInLegend: true,
                    name: "heap used",
                    xValueType: "dateTime",
                    dataPoints: this.heapUsedDps
                }, {
                    type: "line",
                    showInLegend: true,
                    name: "non-heap max",
                    xValueType: "dateTime",
                    dataPoints: this.nonHeapMaxDps
                }, {
                    type: "line",
                    showInLegend: true,
                    name: "non-heap committed",
                    xValueType: "dateTime",
                    dataPoints: this.nonHeapCommittedDps
                }, {
                    type: "line",
                    showInLegend: true,
                    name: "non-heap used",
                    xValueType: "dateTime",
                    dataPoints: this.nonHeapUsedDps
                }]
            });
        }
    }
});