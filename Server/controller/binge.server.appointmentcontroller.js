/**
 * Created by chsra on 11/21/2016.
 */

var UserAppointments=require('../models/binge.server.appointmentschema.js');
var userController=require("../models/binge.server.userschema");
var notificationController=require("../models/binge.server.notificationschema");

var FCM=require('fcm-node');
var serverKey='AIzaSyBukQqY9x3Ti2KhTjGQWFfRUZ8JCbAqYsg';
var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();

exports.createAppointment=function(req,res) {
    var appointmentDetails = {_id: req.body.date + " " + req.body.time, time: req.body.time, date: req.body.date,notes:""};

    UserAppointments.findOne({_id: req.body.username}).exec(function (err, doc) {
        if (err) {
            console.log("Error: \n" + err);
            res.status(500).send({message: "Error: \n" + err});
        } else if (doc == null) {
            console.log("Creating appointment...");
            UserAppointments.update({_id: req.body.username}, {$addToSet: {supporterAppointments: {_id: req.body.user,userAppointments: appointmentDetails}}}, {upsert: true}, function (err, result) {
                if (err) {
                    console.log("Error: \n" + err);
                    res.status(500).send({message:"Error: \n" + err,resultCode:-1});
                }else if (result.nModified == 1 || result.upserted) {
                    console.log("Appointment Created");
                    userController.find({_id:req.body.username},{"details.fcmToken":1},function(err,tokenResult){
                        if(err){
                            console.log("Error");
                        }else if(tokenResult) {
                            console.log("Sending notification");
                            var fcmCli = new FCM(serverKey);
                            var content = "Appointment Created";
                            var payload = {
                                to: tokenResult[0]["details"]["fcmToken"],
                                priority: 'high',
                                notification: {
                                    title: 'Self Monitor',
                                    body: content
                                }
                            };
                            fcmCli.send(payload, function (err, deliveryResult) {
                                if (err) {
                                    console.log("Error while sending token for " + tokenResult[0]["_id"]);
                                } else if (deliveryResult) {
                                    console.log("Notification sent for " + tokenResult[0]["_id"]);
                                    notificationController.find({_id: tokenResult[0]["_id"]}, function (err, result) {
                                        if (err) {
                                            console.log("Error while saving notification.\n" + err);
                                        } else if (result) {
                                            notificationController.update({_id: tokenResult[0]["_id"]}, {
                                                $push: {
                                                    notifications: {
                                                        type: "Appointment",
                                                        content: content,
                                                        sentAt: today
                                                    }
                                                }
                                            }, {upsert: true}, function (err, result) {
                                                if (err) {
                                                    console.log("Error while saving notification.\n" + err);
                                                } else if (result) {
                                                    console.log("Notification saved");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                    res.status(200).send({message: "Appointment Created",resultCode:1});
                } else if (result.nModified == 0) {
                    console.log("Appointment not created");
                    res.status(409).send({message: "Appointment not created",resultCode:0});
                }
            });
        } else if (doc) {
            UserAppointments.findOne({
                _id: req.body.username,
                "supporterAppointments._id": req.body.user
            }).exec(function (err, result) {
                if (err) {
                    console.log("Error: \n" + err);
                    res.status(500).send({message: "Error: \n" + err,resultCode:-1});
                } else if (result) {
                    console.log("Updating doc...");
                    UserAppointments.update({_id: req.body.username, "supporterAppointments._id": req.body.user}, {$addToSet: {'supporterAppointments.$.userAppointments': appointmentDetails}}, {upsert: true}, function (err, result) {
                        if (err) {
                            console.log("Error: \n" + err);
                            return res.status(500).send({message:"Error: \n" + err,resultCode:-1});
                        } else if (result.nModified == 1 || result.upserted) {
                            console.log("Appointment Created");
                            return res.status(200).send({message: "Appointment Created",resultCode:1});
                        } else if (result.nModified == 0) {
                            console.log("Appointment not created");
                            return res.status(409).send({message: "Appointment not created",resultCode:0});
                        }
                    });
                } else if (!result) {
                    UserAppointments.update({_id: req.body.username}, {$addToSet: {supporterAppointments: {_id: req.body.user, userAppointments: appointmentDetails}}}, {upsert: true}, function (err, result) {
                        if (err) {
                            console.log("Error: \n" + err);
                            return res.status(500).send({message:"Error: \n" + err,resultCode:-1});
                        } else if (result.nModified == 1 || result.upserted) {
                            console.log("Appointment Created");
                            return res.status(200).send({message: "Appointment Created",resultCode:1});
                        } else if (result.nModified == 0) {
                            console.log("Appointment not created");
                            return res.status(409).send({message: "Appointment not created",resultCode:0});
                        }
                    });
                }
            });
        }
    });
};
exports.getAllAppointments=function(req,res){
    if(req.body.role=="supporter"){
        UserAppointments.find({_id:req.body.username},{_id:0,__v:0,"supporterAppointments.userAppointments._id":0},function(err,result){
            if(err){
                console.log("Error: \n"+ err);
                res.status(500).send({message:"Error: \n"+ err,resultCode:-1});
            }else if(result){
                console.log("Retrieved appointments");
                res.status(200).send({message:"Retrieved appointments",resultCode:1,result:result[0]});
            }
        });
    }else if(req.body.role=="user"){
        UserAppointments.find({"supporterAppointments._id":req.body.username},{"supporterAppointments.$.userAppointments":1},function(err,result){
            if(err){
                console.log("Error: \n"+ err);
                res.status(500).send({message:"Error: \n"+ err,resultCode:-1});
            }else if(result){
                console.log("Retrieved appointments");
                res.status(200).send({message:"Retrieved appointments",resultCode:1,result:result[0]});
            }
        });
    }
};

exports.editAppointment=function(req,res){
    if(req.body.role =="supporter"){
        console.log("Request received from supporter");
        UserAppointments.update({_id:req.body.username,"supporterAppointments._id":req.body.user,"supporterAppointments.userAppointments._id":req.body.date+" "+req.body.time},{$set:{'supporterAppointments.$.userAppointments.0.status':req.body.status}},function(err,result){
            if(err){
                console.log("Error: \n"+ err);
                res.status(500).send({message:"Error: \n"+ err,resultCode:-1});
            }else if(result.nModified == 1){
                console.log("Changes made to appointment");
                res.status(200).send({message:"Changes made to the appointment",resultCode:1});
            }else if(result.nModified == 0){
                console.log("No changes made to appointment");
                res.status(200).send({message:"No changes made to the appointment",resultCode:0});
            }
        });
    }else if(req.body.role =="user"){
        console.log("Request received from user");
        UserAppointments.update({"supporterAppointments._id":req.body.username,"supporterAppointments.userAppointments._id":req.body.date+" "+req.body.time},{$set:{'supporterAppointments.$.userAppointments.0.notes':req.body.notes}},function(err,result){
            if(err){
                console.log("Error: \n"+ err);
                res.status(500).send({message:"Error: \n"+ err,resultCode:-1});
            }else if(result.nModified == 1){
                console.log("Notes added to the appointment");
                res.status(200).send({message:"Notes added to the appointment",resultCode:1});
            }else if(result.nModified == 0){
                console.log("No changes made to the appointment");
                res.status(200).send({message:"No changes made to the appointment",resultCode:0});
            }
        });
    }
};

exports.deleteAppointment=function(req,res){
    UserAppointments.update({_id:req.body.username,"supporterAppointments._id":req.body.user,"supporterAppointments.userAppointments._id":req.body.date+" "+req.body.time},{$pull:{'supporterAppointments.$.userAppointments.0':{_id:req.body.date+" "+req.body.time}}},function(err,result){
        if(err){
            console.log("Error: \n"+ err);
            res.status(500).send({message:"Error: \n"+ err,resultCode:-1});
        }else if(result.nModified == 1){
            console.log("Appointment cancelled");
            res.status(200).send({message:"Appointment cancelled",resultCode:1});
        }else if(result.nModified==0){
            console.log("Unable to delete appointment");
            res.status(409).send({message:"Unable to delete appointment",resultCode:0});
        }
    });
};
