/**
 * Created by chsra on 12/1/2016.
 */

var mongoose=require('mongoose');
var Schema=mongoose.Schema;
var rand = require('mongoose-simple-random');

var testSchema=new Schema({
    message:String,
    step:String
});
testSchema.plugin(rand);


module.exports=mongoose.model("message",testSchema);