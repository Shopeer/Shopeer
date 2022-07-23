// Not tested, client side not yet working

const express = require('express')
const path = require('path')
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.set('port', (process.env.PORT || 5000));

app.use(express.static(__dirname + '/public'));

// views is directory for all template files
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');

console.log("outside io");

io.on('connection', function(socket){

  console.log('User Conncetion');

  socket.on('connect user', function(user){
    console.log("Connected user ");
    io.emit('connect user', user);
  });

  socket.on('on typing', function(typing){
    console.log("Typing.... ");
    io.emit('on typing', typing);
  });

  socket.on('chat message', function(msg){
    console.log("Message " + msg['message']);
    io.emit('chat message', msg);
  });
});

http.listen(app.get('port'), function() {
  console.log('Node app is running on port', app.get('port'));
});






// const express = require('express'); //requires express module
// const socket = require('socket.io'); //requires socket.io module
// const fs = require('fs');
// const app = express();
// var PORT = process.env.PORT || 3000;
// const server = app.listen(PORT); //hosts server on localhost:3000



// app.use(express.static('public'));
// console.log('Server is running');
// const io = socket(server);

// var count = 0;


// //Socket.io Connection------------------
// io.on('connection', (socket) => {

//     console.log("New socket connection: " + socket.id)

//     socket.on('counter', () => {
//         count++;
//         console.log(count)
//         io.emit('counter', count);
//     })
// })