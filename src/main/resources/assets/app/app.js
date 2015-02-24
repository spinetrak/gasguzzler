requirejs.config({
    paths: {
        'text': '../lib/require/text',
        'durandal': '../lib/durandal/js',
        'plugins': '../lib/durandal/js/plugins',
        'transitions': '../lib/durandal/js/transitions',
        'knockout': '../lib/knockout/knockout-3.1.0',
        'jquery': '../lib/jquery/jquery-1.9.1',
        'services': 'services'
    }
});

define(function (require) {
    var system = require('durandal/system'),
        app = require('durandal/app'),
        dialog = require('plugins/dialog');

    system.debug(true);

    app.title = 'spinetrak::gasguzzler';

    app.configurePlugins({
        router: true,
        dialog: true
    });

    app.start().then(function () {
        dialog.MessageBox.setDefaults({class: "notice success", buttonClass: "small green float-right"});
        app.setRoot('services/shell');
    });
});