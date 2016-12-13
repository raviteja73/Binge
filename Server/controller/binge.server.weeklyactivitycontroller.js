/**
 * Created by chsra on 11/10/2016.
 */

var WeeklyActivityLog=require('../models/binge.server.weeklyactivityschema');
var UserController=require('../models/binge.server.userschema');
var notificationController=require("../models/binge.server.notificationschema");

var FCM=require('fcm-node');
var serverKey='AIzaSyBukQqY9x3Ti2KhTjGQWFfRUZ8JCbAqYsg';

var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();
var date1,date2,timeDiff,diffDays;

exports.weeklyActivityLog=function(req,res){
    UserController.find({_id:req.body.username},{_id:0,loggedAt:1},function(err,result){
        if(err){
            console.log("Error. \n"+err);
            res.status(500).send({message:"Error. \n"+err});
        }else if(result){
           var today=result[0]["loggedAt"];
           today.setDate(today.getDate()+(Number(req.body.weekNumber)-1)*7);
           var startDate=today.getMonth()+1+"/"+today.getDate()+"/"+today.getFullYear();
           today.setDate(today.getDate()+6);
           var endDate=today.getMonth()+1+"/"+today.getDate()+"/"+today.getFullYear();

           var weeklyLogEntries={
                _id:req.body.weekNumber,
                weekNumber:req.body.weekNumber,
                binges:req.body.binges,
                weightControlUsage:req.body.weightControlUsage,
                goodDays:req.body.goodDays,
                weight:req.body.weight,
                fruitVegetableCount:req.body.fruitVegetableCount,
                physicallyActiveDays:req.body.physicallyActiveDays,
                events:req.body.events,
                startDate:startDate,
                endDate:endDate,
            };

            WeeklyActivityLog.update({_id:req.body.username},{$addToSet:{weeklyLog:weeklyLogEntries}},{upsert:true},function(err,result){
                if(err){
                    console.log("Error. \n"+err);
                    res.status(500).send({message:"Error. \n"+err,resultCode:-1});
                }else if(result.upserted || result.nModified){
                    console.log("Weekly activity created");
                    res.status(201).send({message:"Weekly activity created",resultCode:1});
                }else if(result.nModified==0){
                    console.log("No changes made to weekly activity");
                    res.status(409).send({message:"No changes made to weekly activity",resultCode:0});
                }
            });
        }
    });
};

exports.editWeeklyActivityLog=function(req,res){
    WeeklyActivityLog.update({_id:req.body.username,"weeklyLog.weekNumber":req.body.weekNumber}, {$set:{'weeklyLog.$.binges':req.body.binges,'weeklyLog.$.weightControlUsage':req.body.weightControlUsage,'weeklyLog.$.goodDays':req.body.goodDays,'weeklyLog.$.weight':req.body.weight,'weeklyLog.$.fruitVegetableCount':req.body.fruitVegetableCount,'weeklyLog.$.physicallyActiveDays':req.body.physicallyActiveDays,'weeklyLog.$.events':req.body.events}},function(err,result){
        if(err){
            console.log("Error. \n"+err);
            res.status(500).send({message:"Error. \n"+err,resultCode:-1});
        }else if(result.nModified==1){
            console.log("Weekly activity updated");
            res.status(200).send({message:"Weekly activity updated",resultCode:1});
        }else if(result.nModified==0){
            console.log("No changes made to weekly activity");
            res.status(409).send({message:"No changes made to weekly activity",resultCode:0});
        }
    });
};

exports.getWeeklyActivityLog=function(req,res){
  WeeklyActivityLog.find({_id:req.body.username},{_id:0,__v:0,"weeklyLog._id":0},function(err,result){
      if(err){
          console.log("Error. \n"+err);
          res.status(500).send({message:"Error. \n"+err,resultCode:-1});
      }else if(!result){
          console.log("Weekly activity not found");
          res.status(404).send({message:"Weekly activity not found",resultCode:0});
      }else if(result){
          console.log("Weekly activities retrieved");
          res.status(200).send({message:"Weekly activities retrieved",resultCode:1,result:result[0]});
      }
  });
};

exports.getUserWeeklyActivityLog=function(req,res){
    WeeklyActivityLog.find({_id:req.body.username},{_id:0,__v:0,"weeklyLog._id":0},function(err,result){
        if(err){
            console.log("Error. \n"+err);
            res.status(500).send({message:"Error. \n"+err,resultCode:-1});
        }else if(!result){
            console.log("Weekly activity not found");
            res.status(404).send({message:"Weekly activity not found",resultCode:0});
        }else if(result){
            console.log("Weekly activities retrieved");
            res.status(200).send({message:"Weekly activities retrieved",resultCode:1,result:result[0]});
        }
    });
};

exports.deleteWeeklyActivityLog=function(req,res){
    WeeklyActivityLog.update({_id:req.body.username},{$pull:{weeklyLog:{weekNumber:req.body.weekNumber}}},function(err,result){
        if(err){
            console.log("Error. \n"+err);
            res.status(500).send({message:"Error. \n"+err,resultCode:-1});
        }else if(result.nModified==0){
            console.log("Weekly activity not deleted");
            res.status(404).send({message:"Weekly activity not deleted",resultCode:0});
        }else if(result.nModified==1){
            console.log("Weekly activity deleted");
            res.status(200).send({message:"Weekly activity deleted",resultCode:1});
        }
    });
};
exports.weeklyStatusCheck=function(){
    UserController.find({role:"user"},function(err,result){
        if(err){
            console.log("Error. \n"+err);
            res.status(500).send({message:"Error. \n"+err,resultCode:-1});
        }else if(result){
            for(var i=0;i<result.length;i++){
                console.log(result[i]["_id"]);
                UserController.find({_id:result[i]["_id"]},{"details.startDate":1,"details.fcmToken":1},function(err,result){
                    if(err){
                        console.log("Error. \n"+err);
                        res.status(500).send({message:"Error. \n"+err,resultCode:-1});
                    }else if(result){
                        date1 = new Date(today);
                        date2 = new Date(result[0]["details"]["startDate"]);
                        timeDiff = Math.abs(date2.getTime() - date1.getTime());
                        diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));
                        if(date1!=date2 && diffDays%7==0){
                            console.log("Sending notification");
                            var fcmCli = new FCM(serverKey);
                            var content="Reminder to monitor your weekly activities";
                            console.log(result[0]["details"]["fcmToken"]);
                            var payload = {
                                to :result[0]["details"]["fcmToken"],
                                priority : 'high',
                                notification: {
                                    title : 'Self Monitor',
                                    body :content
                                }
                            };
                            fcmCli.send(payload, function (err, deliveryResult) {
                                if(err){
                                    console.log("Error while sending token for "+result[0]["_id"]);
                                }else if(deliveryResult){
                                    console.log("Notification sent for "+result[0]["_id"]);
                                    notificationController.update({_id:result[0]["_id"]},{$push:{notifications:{type:"Weekly",content:content,sentAt:today}}},{upsert:true},function(err,result){
                                        if(err){
                                            console.log("Error while saving notification.\n"+err);
                                        }else if(result){
                                            console.log("Notification saved");
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }
    });
};
