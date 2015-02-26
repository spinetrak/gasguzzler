define(function (require) {
    var router = require('plugins/router');

    var routeArr = [
        {route: '', title: 'Home', moduleId: 'home/home', nav: true},
        {route: 'recentstats', title: 'Stats', moduleId: 'site/recentstats', nav: true},
        {route: 'user', title: 'You', moduleId: 'user/user', nav: true},
        {route: 'login', title: 'Login/Register', moduleId: 'user/login', nav: true},
        {route: 'profile', title: 'Profile', moduleId: 'user/profile', nav: true}
    ];

    return {
        router: router,
        activate: function () {
            router.map(routeArr).buildNavigationModel();
            return router.activate();
        }
    };
});