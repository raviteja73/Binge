/**
 * Created by chsra on 11/30/2016.
 */

var ChallengeController=require('../models/binge.server.challengeschema');
var userController=require("../models/binge.server.userschema");
var notificationController=require("../models/binge.server.notificationschema");

var FCM=require('fcm-node');
var serverKey='AIzaSyBukQqY9x3Ti2KhTjGQWFfRUZ8JCbAqYsg';
var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();

exports.createChallenge=function(req,res){

    var entry={
      challenge:req.body.challenge,
      status:"Created",
      challengeNumber:req.body.challengeNumber
    };
    ChallengeController.update({_id:req.body.username},{$addToSet:{challenges:entry}},{upsert:true},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(result.upserted || result.nModified==1){
            console.log("Challenge created");
            userController.find({_id:req.body.username},{"details.fcmToken":1},function(err,tokenResult){
                if(err){
                    console.log("Error");
                }else if(tokenResult) {
                    console.log("Sending notification");
                    var fcmCli = new FCM(serverKey);
                    var content = "Challenge created for you";
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
                                                type: "Challenge",
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
            res.status(201).send({message:" Challenge created",resultCode:1});
        }else if(result.nModified==0){
            console.log("Challenge exists");
            res.status(409).send({message:"Challenge exists",resultCode:0});
        }
    });
};

exports.editChallenge=function(req,res){
        ChallengeController.update({_id:req.body.username,"challenges.challengeNumber":req.body.challengeNumber},{$set:{'challenges.$.status':"Completed"}},function(err,result){
            if(err){
                console.log("Error.\n"+err);
                res.status(500).send({message:"Error.\n"+err,resultCode:-1});
            }else if(result.nModified==1){
                console.log("Challenge completed");
                res.status(200).send({message:"Challenge completed",resultCode:1});
            }else if(result.nModified==0){
                console.log("Challenge not updated");
                res.status(409).send({message:"Challenge not updated",resultCode:0});
            }
        });
};

exports.editUserChallenge=function(req,res){
        ChallengeController.update({_id:req.body.username,"challenges.challengeNumber":req.body.challengeNumber},{$set:{'challenges.$.challenge':req.body.challenge,'challenges.$.status':"Updated"}},function(err,result){
            if(err){
                console.log("Error.\n"+err);
                res.status(500).send({message:"Error.\n"+err,resultCode:-1});
            }else if(result.nModified==1){
                console.log("Challenge updated");
                res.status(200).send({message:"Challenge updated",resultCode:1});
            }else if(result.nModified==0){
                console.log("Challenge not updated");
                res.status(409).send({message:"Challenge not updated",resultCode:0});
            }
        });
};

exports.getChallenge=function(req,res){
  ChallengeController.find({_id:req.body.username},{_id:0,__v:0,"challenges._id":0},function(err,result){
      if(err){
          console.log("Error.\n"+err);
          res.status(500).send({message:"Error.\n"+err,resultCode:-1});
      }else if(!result){
          console.log("No challenges found");
          res.status(404).send({message:"No challenges found",resultCode:0});
      }else if(result){
          console.log("Challenges found");
          res.status(200).send({message:"Challenges retrieved",resultCode:1,result:result[0]});
      }
  });
};

exports.getUserChallenge=function(req,res){
    ChallengeController.find({_id:req.body.username},{_id:0,__v:0,"challenges._id":0},function(err,result){
        if(err){
            console.log("Error.\n"+err);
            res.status(500).send({message:"Error.\n"+err,resultCode:-1});
        }else if(!result){
            console.log("No challenges found");
            res.status(404).send({message:"No challenges found",resultCode:0});
        }else if(result){
            console.log("Challenges found");
            res.status(200).send({message:"Challenges retrieved",resultCode:1,result:result[0]});
        }
    });
};

exports.deleteChallenge=function(req,res){
    ChallengeController.update({_id:req.body.username},{$pull:{challenges:{challengeNumber:req.body.challengeNumber}}},function(err,result){
        if(err){
            console.log("Error.\n"+err);
            res.status(500).send({message:"Error.\n"+err,resultCode:-1});
        }else if(result.nModified==1){
            console.log("Challenge deleted");
            res.status(200).send({message:"Challenge deleted",resultCode:1});
        }else if(result.nModified==0){
            console.log("Unable to delete challenge");
            res.status(409).send({message:"Unable to delete challenge",resultCode:0});
        }
    });
};
