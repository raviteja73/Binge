/**
 * Created by chsra on 11/30/2016.
 */

var mongoose=require('mongoose');
var Schema=mongoose.Schema;

var today=new Date().getMonth()+1+"/"+new Date().getDate()+"/"+new Date().getFullYear();

var challengeChildSchema=new Schema({
   _id:{type:String},
   challenge:{type:String,required:true},
   status:{type:String,required:true},
   challengeNumber:{type:String,required:true},
   createdOn:{type:String,default:today}
});

var challengeParentSchema=new Schema({
   _id:{type:String,required:true},
    challenges:{type:[challengeChildSchema],required:true}
});

module.exports=mongoose.model("challenge",challengeParentSchema);
