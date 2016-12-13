/**
 * Created by chsra on 11/1/2016.
 */
var userController = require('./controller/binge.server.usercontroller');
var dailyActivityController = require('./controller/binge.server.dailyactivitycontroller');
var weeklyActivityController=require('./controller/binge.server.weeklyactivitycontroller');
var appointmentController=require('./controller/binge.server.appointmentcontroller');
var stepController=require('./controller/binge.server.stepscontroller');
var challengeController=require('./controller/binge.server.challengecontroller');
var notificationController=require('./controller/binge.server.notificationcontroller');
var progressController=require('./controller/binge.server.progresscontroller');

var jwt = require('jsonwebtoken');
var cryptojs = require('crypto-js');
var cron=require('node-schedule');

module.exports = {
    configure: function (app) {

        //scheduler to monitor daily and weekly activities
        cron.scheduleJob('25 00 * * *', function(){
            console.log("Cron Job");
            dailyActivityController.dailyStatusCheck();
            weeklyActivityController.weeklyStatusCheck();
        });

        //scheduler to send ,motivational messages
        cron.scheduleJob('18 00 * * *',function(){
            console.log("Cron Job");
            notificationController.motivationalMessages();
        });

        //create user route
        app.post('/user/createUser',verifySession, function (req, res) {
            userController.createUser(req, res);
        });
        //validate user route
        app.post('/user/validateUser', function (req, res) {
            userController.validateUser(req, res);
        });

        //validate user route
        app.get('/user/validateUser', function (req, res) {
            res.render('/');
        });

        //redirect to admin route
        app.get('/redirectAdminPage',function (req,res) {
            res.render('adminhome.ejs',{message:null});
        });

        //redirect to supporter route
        app.get('/redirectSupporterPage',function (req,res) {
            res.render('supporterhome.ejs',{message:req.query.name});
        });

        //get all users under supporter route
        app.post('/user/getAllSupporterUsers',verifySession, function (req, res) {
            userController.getAllSupporterUsers(req, res);
        });

        //get all supporters route
        app.get('/user/getAllSupporters',verifySession,function (req, res) {
            userController.getAllSupporters(req, res);
        });

        //get all users route
        app.get('/user/getAllUsers',verifySession, function (req, res) {
            userController.getAllUsers(req, res);
        });

        //delete all users route
        app.post('/user/deleteUser',verifySession,function(req,res){
            console.log("delete request received");
            userController.deleteUser(req,res);
        });

        //change supporter route
        app.post('/user/changeSupporter',verifySession,function(req,res){
            console.log("update request received");
            userController.changeSupporter(req,res);
        });

        //edit password route
        app.post('/user/editPassword',verifySession,function(req,res){
            userController.editPassword(req,res);
        });

        //get a particular user route
        app.get('/supporter/getUser',verifySession, function (req, res) {
            res.render('userDetails.ejs');
        });


        //adding daily activity route
        app.post('/activity/dailyActivityLog',requireAuthentication, function (req, res) {
            dailyActivityController.dailyActivityLog(req, res);
        });

        //get daily activity route
        app.post('/activity/getDailyActivityLog',requireAuthentication,function(req,res){
            dailyActivityController.getDailyActivityLog(req, res);
        });

        //supporter get user daily activity route
        app.post('/activity/getUserDailyActivityLog',verifySession,function(req,res){
            dailyActivityController.getUserDailyActivityLog(req, res);
        });

        //edit daily activity route
        app.post('/activity/editDailyActivityLog',requireAuthentication,function(req,res){
            dailyActivityController.editDailyActivityLog(req, res);
        });

        //delete daily activity route
        app.post('/activity/deleteDailyActivityLog',requireAuthentication,function(req,res){
            dailyActivityController.deleteDailyActivityLog(req, res);
        });

        //weekly activity routes
        app.post('/activity/weeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.weeklyActivityLog(req,res);
        });

        //get weekly activity route
        app.post('/activity/getWeeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.getWeeklyActivityLog(req,res);
        });

        //supporter get user weekly activity route
        app.post('/activity/getUserWeeklyActivityLog',verifySession,function(req,res){
            weeklyActivityController.getUserWeeklyActivityLog(req,res);
        });

        //edit weekly activity route
        app.post('/activity/editWeeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.editWeeklyActivityLog(req,res);
        });

        //delete weekly activity route
        app.post('/activity/deleteWeeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.deleteWeeklyActivityLog(req,res);
        });

        //create appointment route
        app.post('/appointment/createAppointment',verifySession,function(req,res){
            appointmentController.createAppointment(req,res);
        });

        //delete appointment route
        app.post('/appointment/deleteAppointment',verifySession,function(req,res){
            appointmentController.deleteAppointment(req,res);
        });

        //get all appointments route
        app.post('/appointment/getAllAppointments',requireAuthentication,function(req,res){
            appointmentController.getAllAppointments(req,res);
        });

        //supporter get all appointments route
        app.post('/appointment/getAllUserAppointments',verifySession,function(req,res){
            appointmentController.getAllUserAppointments(req,res);
        });

        //supporter get all appointments route
        app.post('/appointment/getUserAppointments',verifySession,function(req,res){
            appointmentController.getAllAppointments(req,res);
        });

        //user edit appointments route
        app.post('/appointment/editAppointment',requireAuthentication,function(req,res){
            appointmentController.editAppointment(req,res);
        });

        //supporter edit appointments route
        app.post('/appointment/editUserAppointment',verifySession,function(req,res){
            appointmentController.editUserAppointment(req,res);
        });

        //assign step route
        app.post('/steps/assignStep',function(req,res){
            console.log("Request received to create step");
            stepController.assignStep(req,res);
        });

        //get step route
        app.post('/steps/getSteps',requireAuthentication,function(req,res){
            console.log("Request received to get step");
            stepController.getSteps(req,res);
        });

        //supporter get step route
        app.post('/steps/getUserSteps',verifySession,function(req,res){
            console.log("Request received to get step");
            stepController.getUserSteps(req,res);
        });

        //edit step route
        app.post('/steps/editStep',requireAuthentication,function(req,res){
            console.log("Request received to edit step");
            stepController.editStep(req,res);
        });

        //supporter edit step route
        app.post('/steps/editUserStep',verifySession,function(req,res){
            console.log("Request received to edit step");
            stepController.editUserStep(req,res);
        });

        //delete steps route
        app.post('/steps/deleteStep',verifySession,function(req,res){
            console.log("Request received to delete step");
            stepController.deleteStep(req,res);
        });

        //create challenge route
        app.post('/challenge/createChallenge',verifySession,function(req,res){
            console.log("Request received to create challenge");
            challengeController.createChallenge(req,res);
        });

        //get challenge route
        app.post('/challenge/getChallenge',requireAuthentication,function(req,res){
            console.log("Request received to get challenge");
            challengeController.getChallenge(req,res);
        });

        //supporter get challenge route
        app.post('/challenge/getUserChallenge',verifySession,function(req,res){
            console.log("Request received to get challenge");
            challengeController.getUserChallenge(req,res);
        });

        //edit challenge route
        app.post('/challenge/editChallenge',requireAuthentication,function(req,res){
            console.log("Request received to edit challenge");
            challengeController.editChallenge(req,res);
        });

        //supporter edit challenge route
        app.post('/challenge/editUserChallenge',verifySession,function(req,res){
            console.log("Request received to edit challenge");
            challengeController.editUserChallenge(req,res);
        });

        //delete challenge route
        app.post('/challenge/deleteChallenge',verifySession,function(req,res){
            console.log("Request received to create challenge");
            challengeController.deleteChallenge(req,res);
        });

        //route to get notifications
        app.post('/notifications/getNotifications',requireAuthentication,function(req,res){
            console.log("Request received to get notifications");
            notificationController.getNotifications(req,res);
        });

        //route to get progress
        app.post('/progress/getProgress',requireAuthentication,function(req,res){
            console.log("Request received to get progress");
            progressController.getProgress(req,res);
        });

        //logout route
        app.get('/user/logout', function (req, res) {
            req.session.destroy();
            res.redirect('/');
        });

        app.get('/invalidLogin', function (req, res) {
            if(req.session.credsVerify==false){
                req.session.credsVerify = true;
                res.render('index.ejs',{message:"User does not exist",resultCode:-1});
            }
            else {
                res.redirect('/');
            }

        });

        //session management route
        app.get('/', function (req, res) {
            if (req.session.username && req.session.role == "supporter") {
                res.render('supporterhome.ejs',{message:null})
            } else if (req.session.username && req.session.role == "admin") {
                res.render('adminhome.ejs',{message:null});
            } else {
                req.session.username = req.body.username;
                res.render('index.ejs',{message:null});
            }
        });
    }
};

function verifySession(req, res, next) {
    var session = req.session;
    if (req.session.username) {
        console.log("session validated");
        next();
    }else {
        console.log("session invalidated");
        res.redirect('/');
    }
}
function requireAuthentication(req, res, next) {
    var token = req.get('Auth');
    Authenticate(token).then(function (tokenData) {
        res.locals.user = tokenData.UserId;
        next();
    }, function () {
        console.log(err);
        res.status(401).send();
    });
}
function Authenticate(token) {
    return new Promise(function (resolve, reject) {
        try {
            var decodedJWT = jwt.verify(token, 'jwtHS256');
            var bytes = cryptojs.AES.decrypt(decodedJWT.token, 'ut@clt_NC@28262_usa');
            var tokenData = JSON.parse(bytes.toString(cryptojs.enc.Utf8));
            resolve(tokenData);
        } catch (err) {
            reject();
        }
    });
}