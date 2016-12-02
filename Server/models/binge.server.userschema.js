/**
 * Created by chsra on 11/1/2016.
 */
var mongoose=require('mongoose');
var Schema=mongoose.Schema;

//defining schema for user

    var userSchema=new Schema({
        _id:{type:String},
        username:{type: String, required:true,unique:true},
        password:{type: String, required:true},
        role:{type:String,required:true},
        loggedAt:{type:Date,required:false,default:new Date()},
        details:{}
    });

module.exports=mongoose.model("user",userSchema);

