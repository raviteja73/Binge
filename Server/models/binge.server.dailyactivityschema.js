/**
 * Created by chsra on 11/3/2016.
 */
var mongoose=require('mongoose');
var Schema=mongoose.Schema;

//defining schema for user

var userFoodActivitySchema=new Schema({
    _id:{type:String},
    date:{type:String,requires:true},
    time:{type: String, required:true},
    place:{type: String, required:true},
    food:{type:String,required:true},
    drinks:{type:String,required:false},
    binge:{type:Boolean,required:true},
    vomiting:{type:Boolean,required:true},
    laxating:{type:Boolean,required:true},
    image:{type:String,required:true},
    notes:{type:String,required:true},
    loggedAt:{type:Date,default:new Date(),required:false}
});
var userPhysicalActivitySchema=new Schema({
    _id:{type:String},
    date:{type:String, required:true},
    time:{type:String,required:true},
    activity:{type:String, required:true},
    duration:{type:String,required:true},
    loggedAt:{type:Date,default:new Date(),required:false}
});
var userDailyActivitySchema=new Schema({
    _id:{type:String,required:true},
    foodActivityLog:[userFoodActivitySchema],
    physicalActivityLog:{type:[userPhysicalActivitySchema],required:false}
});

module.exports=mongoose.model("dailyactivity",userDailyActivitySchema);
