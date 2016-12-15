/**
 * Created by chsra on 11/25/2016.
 */

var Steps=require("../models/binge.server.stepschema");
var Tasks=require('../models/binge.server.taskschema');
var UserController=require('../models/binge.server.userschema');
var notificationController=require("../models/binge.server.notificationschema");

var FCM=require('fcm-node');
var serverKey='AIzaSyBukQqY9x3Ti2KhTjGQWFfRUZ8JCbAqYsg';
var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();

exports.assignStep=function(req,res){
    console.log(req.body);
    Tasks.find({_id:req.body.step},{_id:0},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(result){
            console.log(result[0]['tasks']);
            Steps.update({_id:req.body.username},{$addToSet:{steps:{tasks:result[0]['tasks'],name:req.body.step}}},{upsert:true},function(err,result){
                if(err){
                    console.log("Error: \n"+err);
                    res.status(500).send({message:"Error: \n"+err,resultCode:-1});
                }else if(result.upserted || result.nModified==1){
                    console.log("Step Assigned");
                    console.log("Sending notification");
                    UserController.find({_id:req.body.username},{"details.fcmToken":1},function(err,result){
                       if(result){
                           var fcmCli = new FCM(serverKey);
                           var content="Step assigned to you";
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
                                   notificationController.update({_id:result[0]["_id"]},{$push:{notifications:{type:"Step",content:content,sentAt:today}}},{upsert:true},function(err,result){
                                       if(err){
                                           console.log("Error while saving notification.\n"+err);
                                       }else if(result){
                                           console.log("Notification saved");
                                       }
                                   });
                               }
                           });
                       }
                    });
                    UserController.update({_id:req.body.username},{$set:{"details.currentStep":req.body.step}},function(err,result){
                        console.log(result);
                        if(err){
                            console.log("Error: \n"+err);
                            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
                        }else if(result.ok==1) {
                            console.log("Step details updated in user record");
                            res.status(200).send({message:"Step Assigned and user record updated",resultCode:1});
                        }else if(result.ok==0){
                            console.log("Step details not updated in user record");
                            res.status(200).send({message:"Step assigned but details not updated in user record",resultCode:0});
                        }
                    });
                }else if(result.nModified==0){
                    console.log("Step not assigned");
                    res.status(409).send({message:"Step not assigned",resultCode:0});
                }
            });
        }
    });
};

exports.getSteps=function(req,res){
  Steps.find({_id:req.body.username},{_id:0,__v:0,"steps._id":0},function(err,result){
      if(err){
          console.log("Error: \n"+err);
          res.status(500).send({message:"Error: \n"+err,resultCode:-1});
      }else if(!result){
          console.log("Steps not created for this user")
          res.status(200).send({message:"Steps not created for this user",resultCode:0});
      }else if(result){
          console.log("Steps Retrieved")
          res.status(200).send({message:"Steps Retrieved",resultCode:1,result:result});
      }
  });
};

exports.getUserSteps=function(req,res){
    Steps.find({_id:req.body.username},{_id:0,__v:0,"steps._id":0},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(!result){
            console.log("Steps not created for this user")
            res.status(200).send({message:"Steps not created for this user",resultCode:0});
        }else if(result){
            console.log("Steps Retrieved")
            res.status(200).send({message:"Steps Retrieved",resultCode:1,result:result});
        }
    });
};

exports.editStep=function(req,res){
  var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();
  Steps.update({_id:req.body.username,"steps.name":req.body.name},{$set:{'steps.$.status':'Waiting'}},function(err,result){
     if(err){
         console.log("Error: \n"+err);
         res.status(500).send({message:"Error: \n"+err,resultCode:-1});
     }else if(result.nModified==1){
         console.log("Step updated");
         res.status(200).send({message:"Step updated",resultCode:1});
     }else if(result.nModified==0){
         console.log("Step not updated!");
         res.status(409).send({message:"Step not updated",resultCode:0});
     }
  });
};

exports.editUserStep=function(req,res){
    var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();
    Steps.update({_id:req.body.username,"steps.name":req.body.name},{$set:{'steps.$.status':req.body.status}},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err,resultCode:-1});
        }else if(result.nModified==1){
            console.log("Step updated");
            res.status(200).send({message:"Step updated",resultCode:1});
        }else if(result.nModified==0){
            console.log("Step not updated!");
            res.status(409).send({message:"Step not updated",resultCode:0});
        }
    });
};

exports.deleteStep=function(req,res){
  Steps.update({_id:req.body.username},{$pull:{steps:{name:req.body.name}}},function(err,result){
      if(err){
          console.log("Error: \n"+err);
          res.status(500).send({message:"Error: \n"+err,resultCode:-1});
      }else if(result.nModified == 1){
          console.log("Step deleted");
          res.status(200).send({message:"Step deleted",resultCode:1});
      }else if(result.nModified==0){
          console.log("Step not deleted");
          res.status(409).send({message:"Step not deleted",resultCode:0});
      }
  });
};


