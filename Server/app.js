/**
 * Created by chsra on 10/31/2016.
 */
var express = require('express');
var bodyParser = require('body-parser');
var path = require('path');
var routes = require('./routes');
var mongoose = require('mongoose');
var session = require('express-session');
var app = express();

app.use(function (req, res, next) {
    res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
    res.header('Expires', '-1');
    res.header('Pragma', 'no-cache');
    next()
});
app.use(function (req, res, next) {
    res.setHeader('Access-Control-Allow-Origin', '*');
    next();
});
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb',extended: true}));
app.use(session({resave: false, saveUninitialized: false, secret: 'sessionSecret'}));
app.set('views', './views');
app.set('view engine', 'ejs');
app.use('/views', express.static(__dirname + '/views'));

//connecting to mongodb
var uri="mongodb://localhost:27017/binge";
var options = {
    db: { native_parser: true },
    server: { poolSize: 10, reconnectTries: 5},
    auto_reconnect:true
};
mongoose.connect(uri,options);

// When successfully connected
mongoose.connection.on('connected', function () {
    console.log('Mongoose default connection open on: '+uri);
});

// If the connection throws an error
mongoose.connection.on('error',function (err) {
    console.log('Mongoose default connection error: ' + err);
});

// When the connection is disconnected
mongoose.connection.on('disconnected', function () {
    console.log('Mongoose default connection disconnected');
});

// If the Node process ends, close the Mongoose connection
process.on('SIGINT', function() {
    mongoose.connection.close(function () {
        console.log('Mongoose default connection disconnected through app termination');
        process.exit(0);
    });
});

//configuring routes
routes.configure(app);

var server = app.listen(3000, function () {
    console.log('Port is: ' + server.address().port)
});
