/**
 * Created by chsra on 11/3/2016.
 */
var DailyActivityLog=require("../models/binge.server.dailyactivityschema");
var userController=require("../models/binge.server.userschema");

exports.dailyActivityLog=function(req,res){
    //unique id for sub-documents
    var id=req.body.time+" "+req.body.date;

    //sub-document for food log
    var hourlyFoodLog={
        _id:id,time:req.body.time,
        date:req.body.date,
        place:req.body.place,
        food:req.body.food,
        drinks:req.body.drinks,
        binge:req.body.binge,
        vomiting:req.body.vomiting,
        laxating:req.body.laxating,
        image:req.body.image,
        notes:req.body.notes
    };

    //sub-document for physical log
    var hourlyPhysicalLog={
        _id:id,time:req.body.time,
        date:req.body.date,
        activity:req.body.activity,
        duration:req.body.duration
    };

    if(req.body.activityType=="food"){
        DailyActivityLog.update({_id:req.body.username},{$addToSet:{foodActivityLog:hourlyFoodLog}},{upsert:true},function(err,result){
            if(err){
                console.log("Error: \n"+err);
                res.status(500).send({message:"Error: \n"+err})
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
    }else if(req.body.activityType=="physical"){
        DailyActivityLog.update({_id:req.body.username},{$addToSet:{physicalActivityLog:hourlyPhysicalLog}},{upsert:true},function(err,result){
            if(err){
                console.log("Error: \n"+err);
                res.status(500).send({message:"Error: \n"+err})
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
};

exports.getDailyActivityLog=function(req,res){
    DailyActivityLog.find({_id:req.body.username},{_id:0,__v:0},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err});
        }else if(!result){
            console.log("No document found");
            res.status(404).send({message:"No document found"});
        }else if(result){
            console.log("Daily activity document retrieved");
            res.status(200).send({message:"Document retrieved",result:result[0]});
        }
    });
};

exports.editDailyActivityLog=function(req,res){
    if(req.body.activityType=="food"){
        DailyActivityLog.update({_id:req.body.username,"foodActivityLog.date":req.body.date,"foodActivityLog.time":req.body.time},
            {$set:{'foodActivityLog.$.food':req.body.food,'foodActivityLog.$.drinks':req.body.drinks,'foodActivityLog.$.vomiting':req.body.vomiting,'foodActivityLog.$.laxating':req.body.laxating,'foodActivityLog.$.binge':req.body.binge,'foodActivityLog.$.notes':req.body.notes,'foodActivityLog.$.image':req.body.image}},
            {_id:0,__v:0},function(err,result){
                if(err){
                    console.log("Error: \n"+err);
                    res.status(500).send({message:"Error: \n"+err});
                }else if(result.nModified==1){
                    console.log("Daily activity document is updated");
                    res.status(200).send({message:"Document Updated"});
                }else if(result.nModified==0){
                    console.log("No Changes made");
                    res.status(409).send({message:"No Changes made"});
                }
            });
    }else if(req.body.activityType=="physical"){
        DailyActivityLog.update({_id:req.body.username,"physicalActivityLog.date":req.body.date,"physicalActivityLog.time":req.body.time},
            {$set:{'physicalActivityLog.$.duration':req.body.duration,'physicalActivityLog.$.activity':req.body.activity}},
            {_id:0,__v:0},function(err,result){
                if(err){
                    console.log("Error: \n"+err);
                    res.status(500).send({message:"Error: \n"+err});
                }else if(result.nModified==1){
                    console.log("Document is Updated");
                    res.status(200).send({message:"Document Updated"});
                }else if(result.nModified==0){
                    console.log("No Changes made");
                    res.status(409).send({message:"No Changes made"});
                }
            });
    }

};

exports.deleteDailyActivityLog=function(req,res){
    if(req.body.activityType =="food") {
        DailyActivityLog.update({_id:req.body.username},{$pull:{foodActivityLog:{date:req.body.date,time:req.body.time}}},function(err,result){
            if(err){
                console.log("Error: \n"+err);
                res.status(500).send({message:"Error: \n"+err});
            }else if(result.nModified == 1){
                console.log("Food log deleted");
                res.status(200).send({message:"Food log deleted"});
            }else if(result.nModified==0){
                console.log("Log doesn't exist.");
                res.status(409).send({message:"Log doesn't exist."});
            }
        });
    }else if(req.body.activityType =="physical"){
        DailyActivityLog.update({_id:req.body.username},{$pull:{physicalActivityLog:{date:req.body.date,time:req.body.time}}},function(err,result){
            if(err){
                console.log("Error: \n"+err);
                res.status(500).send({message:"Error: \n"+err});
            }else if(result.nModified == 1){
                console.log("Physical log deleted");
                res.status(200).send({message:"Physical log deleted"});
            }else if(result.nModified==0){
                console.log("Log doesn't exist.");
                res.status(409).send({message:"Log doesn't exist."});
            }
        });
    }
};

exports.statusCheck=function(){
    var today=new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();
    DailyActivityLog.find({$or:[{"foodActivityLog.date":today},{"physicalActivityLog.date":today}]},{_id:1},function(err,result){
       if(err){
           console.log("Error: \n"+err);
       }else if(result){
           console.log("Result.\n");
           userController.find({_id:{$nin:result},role:"user"},{_id:1},function(err,result){
               if(err){
                   console.log("Error: \n"+err);
               }else if(result){
                   console.log(result);
               }
           });
       }
    });
};
