/**
 * Created by chsra on 11/30/2016.
 */

var ChallengeController=require('../models/binge.server.challengeschema');

exports.createChallenge=function(req,res){

    var entry={
      challenge:req.body.challenge,
      status:"Created",
      challengeNumber:req.body.challengeNumber
    };
    ChallengeController.update({_id:req.body.username},{$addToSet:{challenges:entry}},{upsert:true},function(err,result){
        if(err){
            console.log("Error: \n"+err);
            res.status(500).send({message:"Error: \n"+err});
        }else if(result.upserted || result.nModified==1){
            console.log("Challenge reated");
            res.status(201).send({message:" Challenge created"});
        }else if(result.nModified==0){
            console.log("Challenge exists");
            res.status(409).send({message:"Challenge exists"});
        }
    });
};

exports.editChallenge=function(req,res){
    ChallengeController.update({_id:req.body.username,"challenges.challengeNumber":req.body.challengeNumber},{$set:{'challenges.$.challenge':req.body.challenge,'challenges.$.status':"Updated"}},function(err,result){
        if(err){
            console.log("Error.\n"+err);
            res.status(500).send({message:"Error.\n"+err});
        }else if(result.nModified==1){
            console.log("Challenge updated");
            res.status(200).send({message:"Challenge updated"});
        }else if(result.nModified==0){
            console.log("Challenge not updated");
            res.status(409).send({message:"Challenge not updated"});
        }
    });
};

exports.getChallenge=function(req,res){
  ChallengeController.find({_id:req.body.username},{_id:0,__v:0,"challenges._id":0},function(err,result){
      if(err){
          console.log("Error.\n"+err);
          res.status(500).send({message:"Error.\n"+err});
      }else if(!result){
          console.log("No challenges found");
          res.status(404).send({message:"No challenges found"});
      }else if(result){
          console.log("Challenges found");
          res.status(409).send({message:"Challenges retrieved",result:result[0]});
      }
  });
};

exports.deleteChallenge=function(req,res){
    ChallengeController.update({_id:req.body.username},{$pull:{challenges:{challengeNumber:req.body.challengeNumber}}},function(err,result){
        if(err){
            console.log("Error.\n"+err);
            res.status(500).send({message:"Error.\n"+err});
        }else if(result.nModified==1){
            console.log("Challenge deleted");
            res.status(200).send({message:"Challenge deleted"});
        }else if(result.nModified==0){
            console.log("Unable to delete challenge");
            res.status(409).send({message:"Unable to delete challenge"});
        }
    });
};
