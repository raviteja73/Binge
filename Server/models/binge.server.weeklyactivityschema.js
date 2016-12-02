/**
 * Created by chsra on 11/10/2016.
 */
var mongoose=require('mongoose');
var Schema=mongoose.Schema;

var weeklyChildScehma=new Schema({
   _id:{type:String,required:true},
   weekNumber:{type:String,required:true},
   startDate:{type:String, required:true},
   endDate:{type:String,required:true},
   binges:{type:String,required:true},
   weightControlUsage:{type:String,required:true},
   goodDays:{type:String,required:true},
   weight:{type:String,required:true},
   fruitVegetableCount:{type:String,required:true},
   physicallyActiveDays:{type:String,required:true},
   events:{type:String,required:false}

});
var weeklySchema=new Schema({
   _id:{type:String,required:true},
   weeklyLog:{type:[weeklyChildScehma],required:false}
});

module.exports=mongoose.model("weeklyactivity",weeklySchema);
