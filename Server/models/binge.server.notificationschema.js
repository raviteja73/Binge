/**
 * Created by chsra on 12/12/2016.
 */

var mongoose=require('mongoose');
var Schema=mongoose.Schema;

var notificationChildSchema=new Schema({
   type:{type:String,required:true},
   content:{type:String,required:true},
   sentAt:{type:String,required:false}
});

var notificationParentSchema=new Schema({
   _id:{type:String,required:true},
   notifications:{type:[notificationChildSchema],required:true}
});
module.exports=mongoose.model("notification",notificationParentSchema);