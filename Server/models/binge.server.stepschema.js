/**
 * Created by chsra on 11/24/2016.
 */

var mongoose=require('mongoose');
var Schema=mongoose.Schema;

//date format in mm-dd-yyyy
var today = new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();

var tasks=new Schema({
   name:{type:String,required:true},
   startedOn:{type:String,default:today,required:false},
   loggedAt:{type:String,default:today,required:false},
   status:{type:String,default:"Created"},
   tasks:{}
});

var steps=new Schema({
   _id:{type:String,required:true},
   steps:{type:[tasks],required:true}
});

module.exports=mongoose.model("step",steps);
