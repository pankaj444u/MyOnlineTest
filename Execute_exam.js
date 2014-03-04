use exam
//db.test.find();
//db.test.remove();
//db.test.insert({qId:'1',question:"Which of these is necessary condition for automatic type conversion in Java?",options:['The destination type is smaller than source type','The destination type is larger than source type','The destination type can be larger or smaller than source type','None of the mentioned'],answer:"The destination type is larger than source type",type:"JAVA"});
//db.test.insert({qId:'2',question:"What is the prototype of the default constructor of this class?"	,options:['prototype()','prototype(void)','public prototype(void)','public prototype()'],answer:"public prototype()",type:"JAVA"})
//db.test.insert({qId:'3',question:"What is the error in this code? \n byte b = 50; b = b * 50;",options:['b can not contain value 100, limited by its range.','* operator has converted b * 50 into int, which can not be converted to byte without casting','b can not contain value 50','No error in this code'],answer:"* operator has converted b * 50 into int, which can not be converted to byte without casting",type:"JAVA"})
//db.test.insert({qId:'4',question:"If an expression contains double, int, float, long, then whole expression will promoted into which of these data types?",options:['long','int','double','float'],answer:"double",type:"JAVA"})

//mongo < E:\Pankaj\MongoDB\Week5\Execute_exam.js

db.users.find().pretty();