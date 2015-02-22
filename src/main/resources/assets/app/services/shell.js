define(function (require) {
	var router = require('plugins/router');
 
	var routeArr = [
         { route: '', title:'Home', moduleId: 'home/home', nav: true },
		 { route:'recentstats', title:'Recent Stats', moduleId:'site/recentstats', nav:true },
		 { route:'login', title:'Login/Register', moduleId:'user/login', nav:!sessionStorage.getItem("token") },
		 { route:'profile', title:'Profile', moduleId:'user/profile', nav:sessionStorage.getItem("token") }
       ];
	
  return {
     router: router,
     activate: function () {
       router.map(routeArr).buildNavigationModel();
       return router.activate();
     },
	 
	 reconfigure: function() {
		 		   router.deactivate();
		   
		   		   	var myroutes = [
         { route: '', title:'Home', moduleId: 'home', nav: true },
		 { route:'recentstats', title:'Recent Stats', moduleId:'recentstats', nav:true },
		 { route:'login', title:'Login', moduleId:'login', nav:!sessionStorage.getItem("token") },
		 { route:'profile', title:'Profile', moduleId:'profile', nav:sessionStorage.getItem("token") }
       ];
	   
			router.routes = [];
			router.map(myroutes).buildNavigationModel();
			router.activate();
	 }
   };
});