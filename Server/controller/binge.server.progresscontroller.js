/**
 * Created by chsra on 12/13/2016.
 */

var UserController=require('../models/binge.server.userschema');
var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();
var date1,date2,timeDiff,diffDays,currentWeek,currentStep;

exports.getProgress=function(req,res){
    UserController.find({_id:req.body.username},{"details.startDate":1,"details.currentStep":1},function(err,result){
      if(err){
          console.log("Error: \n" + err);
          res.status(500).send({message:"Error: \n" + err,resultCode:-1});
      }else if(result==null){
          console.log("User doesn't exist");
          res.status(200).send({message:"User doesn't exist",resultCode:0});
      }else if(result){
          console.log("Retrieving progress");
          date1 = new Date(today);
          date2 = new Date(result[0]["details"]["startDate"]);
          timeDiff = Math.abs(date1.getTime() - date2.getTime());
          diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));
          if(diffDays==0) currentWeek=1;
          else currentWeek=Math.ceil((diffDays/7));
          currentStep=parseInt((result[0]["details"]["currentStep"]).substring(4));
          res.status(200).send({message:"Progress retrieved",result:[{week:currentWeek,currentStep:currentStep}],resultCode:1});
      }
    });
};
