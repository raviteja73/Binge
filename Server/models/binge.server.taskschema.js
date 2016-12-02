/**
 * Created by chsra on 12/1/2016.
 */

var mongoose=require('mongoose');
var Schema=mongoose.Schema;

var taskSchema=new Schema({
    _id:{type:String,required:true},
    tasks:{type:[String],required:true}
});

module.exports=mongoose.model("task",taskSchema);