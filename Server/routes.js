/**
 * Created by chsra on 11/1/2016.
 */
var userController = require('./controller/binge.server.usercontroller');
var dailyActivityController = require('./controller/binge.server.dailyactivitycontroller');
var weeklyActivityController=require('./controller/binge.server.weeklyactivitycontroller');
var appointmentController=require('./controller/binge.server.appointmentcontroller');
var stepController=require('./controller/binge.server.stepscontroller');
var challengeController=require('./controller/binge.server.challengecontroller');

var jwt = require('jsonwebtoken');
var cryptojs = require('crypto-js');
var cron=require('node-schedule');


module.exports = {
    configure: function (app) {
        //var date = new Date(2016, 10, 28, 22, 00, 0);
        cron.scheduleJob('47 16 * * *', function(){
            console.log("Cron Job");
            dailyActivityController.statusCheck();
        });

        //create user route
        app.post('/user/createUser',verifySession, function (req, res) {
            userController.createUser(req, res);
        });
        //validate user route
        app.post('/user/validateUser',verifySession, function (req, res) {
            userController.validateUser(req, res);
        });

        app.get('/user/validateUser', function (req, res) {
            res.render('/');
        });

        app.get('/redirectAdminPage',verifySession,function (req,res) {
            res.render('adminhome.ejs',{message:null});
        });

        app.get('/redirectSupporterPage',verifySession,function (req,res) {
            res.render('supporterhome.ejs',{message:req.query.name});
        });

        //get all supporter users route
        app.post('/user/getAllSupporterUsers',verifySession, function (req, res) {
            userController.getAllSupporterUsers(req, res);
        });

        app.get('/user/getAllSupporters',verifySession,function (req, res) {
            userController.getAllSupporters(req, res);
        });

        app.get('/user/getAllUsers',verifySession, function (req, res) {
            userController.getAllUsers(req, res);
        });

        app.post('/user/deleteUser',verifySession,function(req,res){
            console.log("delete request received");
            userController.deleteUser(req,res);
        });

        app.post('/user/changeSupporter',verifySession,function(req,res){
            console.log("update request received");
            userController.changeSupporter(req,res);
        });

        app.post('/user/editPassword',verifySession,function(req,res){
            userController.editPassword(req,res);
        });

        app.get('/supporter/getUser',verifySession, function (req, res) {
            res.render('userDetails.ejs');
        });


        //Daily activity routes
        app.post('/activity/dailyActivityLog',requireAuthentication, function (req, res) {
            dailyActivityController.dailyActivityLog(req, res);
        });

        app.post('/activity/getDailyActivityLog',requireAuthentication,function(req,res){
            dailyActivityController.getDailyActivityLog(req, res);
        });

        app.post('/activity/editDailyActivityLog',requireAuthentication,function(req,res){
            dailyActivityController.editDailyActivityLog(req, res);
        });

        app.post('/activity/deleteDailyActivityLog',requireAuthentication,function(req,res){
            dailyActivityController.deleteDailyActivityLog(req, res);
        });

        //weekly activity routes
        app.post('/activity/weeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.weeklyActivityLog(req,res);
        });

        app.post('/activity/getWeeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.getWeeklyActivityLog(req,res);
        });

        app.post('/activity/editWeeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.editWeeklyActivityLog(req,res);
        });

        app.post('/activity/deleteWeeklyActivityLog',requireAuthentication,function(req,res){
            weeklyActivityController.deleteWeeklyActivityLog(req,res);
        });

        //appointment routes
        app.post('/appointment/createAppointment',verifySession,function(req,res){
            appointmentController.createAppointment(req,res);
        });

        app.post('/appointment/deleteAppointment',verifySession,function(req,res){
            appointmentController.deleteAppointment(req,res);
        });

        app.post('/appointment/getAllAppointments',function(req,res){
            appointmentController.getAllAppointments(req,res);
        });

        app.post('/appointment/editAppointment',function(req,res){
            appointmentController.editAppointment(req,res);
        });

        //steps routes
        app.post('/steps/assignStep',verifySession,function(req,res){
            console.log("Request received to create step");
            stepController.assignStep(req,res);
        });

        app.post('/steps/getSteps',function(req,res){
            console.log("Request received to get step");
            stepController.getSteps(req,res);
        });

        app.post('/steps/editStep',function(req,res){
            console.log("Request received to edit step");
            stepController.editStep(req,res);
        });

        app.post('/steps/deleteStep',function(req,res){
            console.log("Request received to delete step");
            stepController.deleteStep(req,res);
        });

        //challenge routes
        app.post('/challenge/createChallenge',function(req,res){
            console.log("Request received to create challenge");
            challengeController.createChallenge(req,res);
        });

        app.post('/challenge/getChallenge',function(req,res){
            console.log("Request received to get challenge");
            challengeController.getChallenge(req,res);
        });

        app.post('/challenge/editChallenge',function(req,res){
            console.log("Request received to edit challenge");
            challengeController.editChallenge(req,res);
        });

        app.post('/challenge/deleteChallenge',function(req,res){
            console.log("Request received to create challenge");
            challengeController.deleteChallenge(req,res);
        });

        //logout route
        app.get('/user/logout', function (req, res) {
            req.session.destroy();
            res.redirect('/');
        });

        //session management route
        app.get('/', function (req, res) {
            if (req.session.username && req.session.role == "supporter") {
                res.render('supporterhome.ejs',{message:null})
            } else if (req.session.username && req.session.role == "admin") {
                res.render('adminhome.ejs',{message:null});
            } else {
                req.session.username = req.body.username;
                res.render('index.ejs');
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