/**
 * Created by chsra on 11/1/2016.
 */
var BingeUser = require("../models/binge.server.userschema");
var UserAppointments= require("../models/binge.server.appointmentschema");
var jwt = require('jsonwebtoken');
var cryptoJS = require('crypto-js');
var passwordHash = require('password-hash');
var date,today,tomorrow,time;

exports.createUser = function (req, res) {
    if (req.body.role == "supporter") {
        BingeUser.find({username: req.body.username}, function (err, result) {
            if (err) {
                console.log("Error: \n"+err);
                res.status(500).send({message:"Error: \n"+err,resultCode:-1});
            } else if (result.length) {
                console.log("Cannot create this user, as his record already exist");
                res.status(409).render('adminhome',{message:{'value':"User already exist", 'role':"supporter"}});
            } else {
                console.log("creating user.....");
                var entry = new BingeUser({
                    _id: req.body.username,
                    username: req.body.username,
                    password: passwordHash.generate(req.body.password),
                    role: req.body.role,
                    details: {
                        name: req.body.name,
                        mobilenumber: req.body.mobilenumber
                    }
                });
                entry.save(function(err,result){
                    if(err){
                        if(err.name=="ValidationError"){
                            console.log("Error: \n"+err);
                            res.status(400).redirect('/');
                        }
                    }else if(result){
                        console.log("Supporter created");
                        res.status(201).redirect("/redirectAdminPage");
                    }
                });
            }
        });
    } else if (req.body.role == "user") {
        BingeUser.find({username: req.body.username}, function (err, result) {
            if (err) {
                console.log("Error: \n"+err);
                res.status(500).send({message:"Error: \n"+err,resultCode:-1});
            } else if (result.length) {
                console.log("Cannot create this user, as his record already exist");
                res.status(409).render('adminhome.ejs',{message:{'value':"User already exist", 'role':"user"}});
            } else {
                console.log("creating user.....");

                //setting start and end dates
                var today=new Date();
                var startDate = today.getMonth()+1+"/"+today.getDate()+"/"+today.getFullYear();
                today.setDate(new Date().getDate()+84);
                var endDate=today.getMonth()+1+"/"+today.getDate()+"/"+today.getFullYear();

                var entry = new BingeUser({
                    _id: req.body.username,
                    username: req.body.username,
                    password: passwordHash.generate(req.body.password),
                    role: req.body.role,
                    details: {
                        startDate: startDate,
                        endDate:endDate,
                        supporter: req.body.supporter,
                    }
                });
                entry.save(function(err,result){
                    if(err){
                        if(err.name=="ValidationError"){
                            console.log("Error: \n"+err);
                            res.status(400).redirect('/');
                        }
                    }else if(result){
                        console.log("User created");
                        res.status(201).redirect("/redirectAdminPage");
                        //res.status(201).render("adminhome.ejs",{message:null,resultCode:1});
                    }
                });
            }
        });
    }
};

exports.validateUser = function (req, res) {
    BingeUser.find({username: req.body.username}, {role: 1, username: 1, password: 1, details: 1}, function (err, result) {
        if (err) {
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err});
        } else if (result.length && passwordHash.verify(req.body.password, result[0]['password'])) {
            req.session.username = result[0]['username'];
            req.session.role = result[0]['role'];
            if (result[0]['role'] == "admin") {
                console.log("Admin authorized to login");
                res.redirect('/redirectAdminPage');
            }else if (result[0]['role'] == "supporter") {
                console.log("Supporter authorized to login");
                res.redirect('/redirectSupporterPage?name='+result[0].details.name);
            }else {
                BingeUser.update({_id:req.body.username},{$set:{"details.deviceId":req.body.deviceId,"details.fcmToken":req.body.fcmToken}},function(err,result){
                 if(err){
                     console.log("Error Occurred: \n"+err);
                     res.status(500).send({message:"Error: \n"+err,resultCode:-1});
                 }else if(result){
                     console.log("deviceId updated");
                     var userData = JSON.stringify(req.body);
                     var encryptedData = cryptoJS.AES.encrypt(userData, 'ut@clt_NC@28262_usa').toString();
                     var token = jwt.sign({
                         token: encryptedData
                     }, 'jwtHS256');
                     if (token) {
                         console.log("User Authorized to login");
                         res.header('Auth', token).status(200).send({
                             'Status': 'Login Successful',
                             'token': token,
                             'username': req.body.username,
                             'resultCode':1
                         });
                     } else {
                         res.status(401).send({message:"Unauthorized",resultCode:-1});
                     }
                 }
                });
            }
        }else {
            console.log("User doesn't exist.Unauthorized to login");
            req.session.credsVerify = false;
            res.status(401).redirect('/invalidLogin');
            //res.status(401).send({message:"User doesn't exist",resultCode:-1});
        }
    });
};
exports.getAllUsers = function (req, res) {
    BingeUser.find({role: "user"}, {
        username: 1,
        "details.supporter": 1,
        "details.startDate": 1
    }, function (err, result) {
        if(err){
            if(err.name=="ValidationError"){
                console.log("Error: \n"+err);
                res.status(400).redirect('/');
            }
        } else if(!result){
            console.log("Users doesn't exist");
            res.status(200).send({message:"Users doesn't exist",resultCode:0,result:result});
        } else if (result) {
            console.log("User details retrieved");
            res.status(200).send({message:"User details retrieved",resultCode:1,result:result});
        }
    });
};
exports.getAllSupporters = function (req, res) {
    BingeUser.find({role: "supporter"}, {
        "details.name": 1,
        username: 1,
        "details.mobilenumber": 1
    }, function (err, result) {
        if(err){
            if(err.name=="ValidationError"){
                console.log("Error: \n"+err);
                res.status(400).redirect('/');
            }
        }else if(!result){
            console.log("Supporters doesn't exist");
            res.status(200).send({message:"Supporters doesn't exist",resultCode:0,result:result});
        }else if (result) {
            console.log("Supporter details retrieved");
            res.status(200).send({message:"Supporter details retrieved",resultCode:1,result:result});
        }
    });
};
exports.getAllSupporterUsers = function (req, res) {
    date=new Date();
    today=date.getMonth()+1+"/"+date.getDate()+"/"+date.getFullYear();
    date.setDate(date.getDate()+1);
    tomorrow=date.getMonth()+1+"/"+date.getDate()+"/"+date.getFullYear();
    BingeUser.find({"details.supporter": req.body.username, role: "user"}, {
        username: 1,
        "details.startDate": 1
    }, function (err, usersResult) {
        if(err){
            if(err.name=="ValidationError"){
                console.log("Error: \n"+err);
                res.status(400).redirect('/');
            }
        }else if (usersResult) {
            UserAppointments.find({$or:[{date:today},{date:tomorrow}],_id:req.body.username},{"appointments.date":1,"appointments.time":1,"appointments.user":1,"appointments.status":1},function(err,appointmentResult){
                    if(err){
                        console.log("Error: \n"+err);
                        res.status(500).send({message:"Error Occurred./n Error: "+err,resultCode:-1});
                    }else if(appointmentResult){
                    console.log("appointment details retrieved");
                    res.status(200).send(usersResult);
                }
            });
        }
    });
};
exports.deleteUser=function(req,res){
    BingeUser.remove({_id:req.body.username},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(result){
            console.log("User deleted");
            res.status(200).send({message:"User deleted",resultCode:0});
        }
    });
};
exports.changeSupporter=function(req,res){
    BingeUser.update({_id:req.body.username},{$set:{"details.supporter":req.body.supporter,"details.updatedOn":new Date()}},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(result.nModified==1){
            console.log("User details updated");
            res.status(200).redirect("/redirectAdminPage");
            //res.status(200).send({message:"User details updated",resultCode:1});
        }else if(result.nModified==0){
            console.log("User details not updated");
            res.status(200).send({message:"User details not updated",resultCode:0});
        }
    });
};

exports.editPassword=function(req,res){
    BingeUser.update({_id:req.body.username,role:req.body.role},{$set:{password:passwordHash.generate(req.body.password)}},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(result.nModified==1){
            console.log("Password updated");
            res.status(202).send({message:"Password updated",resultCode:1});
        }else if(result.nModified==0){
            console.log("No changes made");
            res.status(202).send({message:"No changes made",resultCode:0});
        }
    });
}