var express = require("express")
var logger = require("morgan")
var path = require("path")
var bodyParser = require("body-parser")


var app = express();


//views
app.set("views",path.join(__dirname,"views"));
app.set("view engine","ejs");

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:false}));
app.use(express.static(path.join(__dirname,"public")));

app.get('/',function(req,res){
  res.send("Fuck yeah!!")
})
app.listen(3000);
console.log('Server started with port 3000');

module.exports = app;
