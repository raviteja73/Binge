/**
 * Created by chsra on 11/21/2016.
 */

var mongoose=require('mongoose');
var Schema=mongoose.Schema;

var childAppointmentsDoc=new Schema({
   _id:{type:String,required:false},
   time:{type:String,required:true},
   date:{type:String,required:true},
   status:{type:String,default:"Created"},
   notes:{}
});

var userAppointments=new Schema({
   _id:{type:String,required:true},
   userAppointments:[childAppointmentsDoc]
});

var supporterAppointments=new Schema({
   _id:{type:String,required:true},
   supporterAppointments:[userAppointments]
});

module.exports=mongoose.model("appointment",supporterAppointments);