/**
 * Created by chsra on 11/21/2016.
 */

var UserAppointments=require('../models/binge.server.appointmentschema.js');

exports.createAppointment=function(req,res){
    var appointmentDetails={_id:req.body.date+" "+req.body.time,time:req.body.time,date:req.body.date};

    UserAppointments.findOne({_id:req.body.username}).exec(function(err,doc){
    if(err){
        console.log("Error: \n"+err);
        res.status(500).send({message:"Error: \n"+err});
    }else if(doc==null){
        console.log("Creating doc...");
        UserAppointments.update({_id: req.body.username},{$addToSet:{supporterAppointments:{_id:req.body.user,userAppointments:appointmentDetails}}},{upsert:true},function (err,result){
           if(err){
               console.log("Error: \n"+err);
               res.status(500).send({message:"Error: \n"+err});
           }else if(result){
               console.log("Appointment created");
               res.status(201).send({message:"Appointment created"});
           }
        });
    }else if(doc){
        console.log("updating doc...");
            UserAppointments.update({_id: req.body.username,"supporterAppointments._id":req.body.user}, {$addToSet: {'supporterAppointments.$.userAppointments':appointmentDetails}},{upsert:true}, function (err, result) {
                if (err) {
                    console.log("Error: \n"+ err);
                    res.status(500).send("Error: \n"+ err);
                } else if (result.nModified == 1) {
                    console.log("Document updated");
                    res.status(200).send({message: "Document updated"});
                } else if (result.nModified == 0) {
                    console.log("No changes made");
                    res.status(409).send({message: "No changes made"});
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
                res.status(500).send("Error: \n"+ err);
            }else if(result){
                console.log("Retrieved appointments");
                res.status(200).send({message:"Retrieved appointments",result:result[0]});
            }
        });
    }else if(req.body.role=="user"){
        UserAppointments.find({"supporterAppointments._id":req.body.username},{_id:0,__v:0,"supporterAppointments.userAppointments._id":0},function(err,result){
            if(err){
                console.log("Error: \n"+ err);
                res.status(500).send("Error: \n"+ err);
            }else if(result){
                console.log("Retrieved appointments");
                res.status(200).send({message:"Retrieved appointments",result:result[0]});
            }
        });
    }
};

exports.editAppointment=function(req,res){
    if(req.body.role =="supporter"){
        UserAppointments.update({_id:req.body.username,"supporterAppointments.userAppointments.time":req.body.time,"supporterAppointments.userAppointments.date":req.body.date},{$set:{'supporterAppointments.$.userAppointments.status':req.body.status}},function(err,result){
            if(err){
                console.log("Error: \n"+ err);
                res.status(500).send("Error: \n"+ err);
            }else if(result.nModified == 1){
                console.log("Changes made to appointment");
                res.status(200).send({message:"Changes made to appointment"});
            }
        });
    }else if(req.body.role =="user"){
        UserAppointments.update({_id:req.body.username,"supporterAppointments.userAppointments.time":req.body.time,"supporterAppointments.userAppointments.date":req.body.date},{$set:{'supporterAppointments.$.userAppointments.notes':req.body.notes}},function(err,result){
            if(err){
                console.log("Error: \n"+ err);
                res.status(500).send("Error: \n"+ err);
            }else if(result.nModified == 1){
                console.log("Notes added to the appointment");
                res.status(200).send({message:"Notes added to the appointment"});
            }
        });
    }
};

exports.deleteAppointment=function(req,res){
    UserAppointments.update({_id:req.body.username},{$pull:{supporterAppointments:{userAppointments:{date:req.body.date,time:req.body.time}}}},function(err,result){
        if(err){
            console.log("Error: \n"+ err);
            res.status(500).send("Error: \n"+ err);
        }else if(result.nModified == 1){
            console.log("Appointment cancelled");
            res.status(200).send({message:"Appointment cancelled"});
        }else if(result.nModified==0){
            console.log("Unable to delete appointment");
            res.status(409).send({message:"Unable to delete appointment"});
        }
    });
};
