/**
 * Created by chsra on 11/10/2016.
 */

var WeeklyActivityLog=require('../models/binge.server.weeklyactivityschema');
var UserController=require('../models/binge.server.userschema');

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
                    res.status(500).send({message:"Error. \n"+err});
                }else if(result.upserted){
                    console.log("Document Created");
                    res.status(201).send({message:"Document Created"});
                }else if(result.nModified==1){
                    console.log("Document Updated");
                    res.status(200).send({message:"Document Updated"});
                }else if(result.nModified==0){
                    console.log("No changes made");
                    res.status(409).send({message:"No changes made"});
                }
            });
        }
    });
};

exports.editWeeklyActivityLog=function(req,res){
    WeeklyActivityLog.update({_id:req.body.username,"weeklyLog.weekNumber":req.body.weekNumber}, {$set:{'weeklyLog.$.binges':req.body.binges,'weeklyLog.$.weightControlUsage':req.body.weightControlUsage,'weeklyLog.$.goodDays':req.body.goodDays,'weeklyLog.$.weight':req.body.weight,'weeklyLog.$.fruitVegetableCount':req.body.fruitVegetableCount,'weeklyLog.$.physicallyActiveDays':req.body.physicallyActiveDays,'weeklyLog.$.events':req.body.events}},function(err,result){
        if(err){
            console.log("Error. \n"+err);
            res.status(500).send({message:"Error. \n"+err});
        }else if(result.nModified==1){
            console.log("Document Updated");
            res.status(200).send({message:"Document Updated"});
        }else if(result.nModified==0){
            console.log("No changes made");
            res.status(409).send({message:"No changes made"});
        }
    });
};

exports.getWeeklyActivityLog=function(req,res){
  WeeklyActivityLog.find({_id:req.body.username},{_id:0,__v:0,"weeklyLog._id":0},function(err,result){
      if(err){
          console.log("Error. \n"+err);
          res.status(500).send({message:"Error. \n"+err});
      }else if(!result){
          console.log("No document found");
          res.status(404).send({message:"No document found"});
      }else if(result){
          console.log("Weekly log found");
          res.status(200).send({message:"Results retrieved.",result:result[0]});
      }
  });
};

exports.deleteWeeklyActivityLog=function(req,res){
    WeeklyActivityLog.update({_id:req.body.username},{$pull:{weeklyLog:{weekNumber:req.body.weekNumber}}},function(err,result){
        if(err){
            console.log("Error Occurred.Error: " + err);
            res.status(500).send("Error Occurred.Error: " + err);
        }else if(result.nModified == 1){
            console.log("Weekly log deleted");
            res.status(200).send({message:"Weekly log deleted"});
        }else if(result.nModified==0){
            console.log("Log doesn't exist");
            res.status(409).send({message:"Log doesn't exist"});
        }
    });
};
