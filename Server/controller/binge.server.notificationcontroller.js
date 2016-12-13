/**
 * Created by chsra on 12/12/2016.
 */
var notificationController=require('../models/binge.server.notificationschema');
var motivationalMessageController=require("../models/binge.server.motivationalmessageschema");
var UserController=require('../models/binge.server.userschema');

var FCM=require('fcm-node');
var serverKey='AIzaSyBukQqY9x3Ti2KhTjGQWFfRUZ8JCbAqYsg';
var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();
var token="",id="";

exports.getNotifications=function(req,res){
    notificationController.find({_id:req.body.username,"notifications.type":{$in:['Daily','Weekly','Appointment','Challenge','Steps']}},{_id:0,__v:0,"notifications.type":0,"notifications._id":0},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(!result.length){
            console.log("No results");
            res.status(200).send({message:"No notifications",resultCode:0});
        }else if(result.length){
            console.log("Notifications retrieved");
            res.status(200).send({message:"Notifications retrieved",result:result,resultCode:1});
        }
    }) ;
};

exports.motivationalMessages=function(){
    var options = {limit:1};
    var fields={_id:0,message:1}
    var filter="";
    UserController.find({role:"user"},function(err,userResult){
        if(err){
            console.log("Error: \n"+err);
        }else if(userResult==null) {
            console.log("No users found");
        }else if(userResult.length){
            for(var i=0;i<userResult.length;i++){
                filter = { step: { $in: ["step2"] } };
                UserController.find({_id:userResult[i]["_id"]},{"details.fcmToken":1,"details.currentStep":1},function(err,tokenResult){
                    if(err){
                        console.log("Error");
                    }else if(tokenResult){
                        motivationalMessageController.findRandom(filter,fields, options, function(err, messageResult) {
                            if (err){
                                console.log(err);
                            }else if(messageResult){
                                console.log(messageResult[0]["message"]);
                                var fcmCli = new FCM(serverKey);
                                var content = "Motivation message: "+messageResult[0]["message"];
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
                                    }else if(deliveryResult) {
                                        console.log("Notification sent for " + tokenResult[0]["_id"]);
                                        notificationController.update({_id: tokenResult[0]["_id"]}, {$push: {notifications: {type: "Message", content: content, sentAt: today}}}, {upsert: true}, function (err, result) {
                                            if (err){
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
            }
        }
    });
};
exports.getMotivationalMessages=function(req,res){
    notificationController.find({_id:req.body.username,"notifications.type":"Message"},{_id:0,__v:0,"notifications.type":0,"notifications._id":0},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(!result.length){
            console.log("No results");
            res.status(200).send({message:"No notifications",resultCode:0});
        }else if(result.length){
            console.log("Motivational Messages retrieved");
            res.status(200).send({message:"Motivational messages retrieved",result:result,resultCode:1});
        }
    }) ;
}