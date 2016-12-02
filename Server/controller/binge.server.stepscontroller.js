/**
 * Created by chsra on 11/25/2016.
 */

var Steps=require("../models/binge.server.stepschema");
var Tasks=require('../models/binge.server.taskschema');

exports.assignStep=function(req,res){
    Tasks.find({_id:req.body.task},{_id:0},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err});
        }else if(result){
            console.log(result[0]['tasks']);
            Steps.update({_id:req.body.username},{$addToSet:{steps:{tasks:result[0]['tasks'],name:req.body.task}}},{upsert:true},function(err,result){
                if(err){
                    console.log("Error: \n"+err);
                    res.status(500).send({message:"Error: \n"+err});
                }else if(result.upserted || result.nModified==1){
                    console.log("Step created");
                    res.status(201).send({message:"Step created"});
                }else if(result.nModified==0){
                    console.log("Step not created");
                    res.status(409).send({message:"Step not created"});
                }
            });
        }
    });
};

exports.getSteps=function(req,res){
  Steps.find({_id:req.body.username},{_id:0,__v:0,"steps._id":0},function(err,result){
      if(err){
          console.log("Error: \n"+err);
          res.status(500).send({message:"Error: \n"+err});
      }else if(result){
          res.status(200).send(result[0]);
      }
  });
};

exports.editStep=function(req,res){
  var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();
  Steps.update({_id:req.body.username,"steps.name":req.body.name},{$set:{'steps.$.status':req.body.status}},function(err,result){
     if(err){
         console.log("Error: \n"+err);
         res.status(500).send({message:"Error: \n"+err});
     }else if(result.nModified==1){
         console.log("Step updated");
         res.status(200).send({message:"Step updated"});
     }else if(result.nModified==0){
         console.log("Step not updated!");
         res.status(409).send({message:"Step not updated"});
     }
  });
};

exports.deleteStep=function(req,res){
  Steps.update({_id:req.body.username},{$pull:{steps:{name:req.body.name}}},function(err,result){
      if(err){
          console.log("Error: \n"+err);
          res.status(500).send({message:"Error: \n"+err});
      }else if(result.nModified == 1){
          console.log("Step deleted");
          res.status(200).send({message:"Step deleted"});
      }else if(result.nModified==0){
          console.log("Step not deleted");
          res.status(409).send({message:"Step not deleted"});
      }
  });
};


