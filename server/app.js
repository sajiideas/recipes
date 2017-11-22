var express = require("express")
var logger = require("morgan")
var path = require("path")
var bodyParser = require("body-parser")
var neo4j = require("neo4j-driver").v1

var app = express();

var driver = neo4j.driver("bolt://localhost",neo4j.auth.basic("neo4j","neo4j"))

var session = driver.session();
//views
app.set("views",path.join(__dirname,"views"));
app.set("view engine","ejs");

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:false}));
app.use(express.static(path.join(__dirname,"public")));

app.get('/',function(req,res){
    session
    .run("MATCH (n:Ingredient) RETURN n LIMIT 25")
    .then(function(result){
      var ingredients = []
      result.records.forEach(function(record){
        ingredients.push(record._fields[0].properties.name)
        console.log(record._fields)
      })
      res.render("index",{ingredients:ingredients})
    })
    .catch(function(err){
      console.log(err)
    });
})
app.listen(3000);
console.log('Server started with port 3000');

module.exports = app;
