const { MongoMissingCredentialsError } = require('mongodb');
var gcm = require('node-gcm');



var message = new gcm.Message();

message.addData('hello', 'world');
message.addNotification('title', 'Hello');
message.addNotification('icon', 'ic_launcher');
message.addNotification('body', 'World');



//Add your mobile device registration tokens here
var regTokens = ['ddKNsLykRXGHOmJEz6ubJp:APA91bGLKjD_3lzU0J2ADavLck8eJRPgQRNp5-ReMr1YRHaDAYj-mqgWJEeI1bgybDAoRBnPD8srogCnxf2fBMr_E7WyN_hsQUxKXDspmMRK_u8gvGt5vSeHDEbqdj9UDS-x60yOoVVd'];

//Replace your developer API key with GCM enabled here
var sender = new gcm.Sender('B_nhjvb1_Fiao_uDF261rY2aT12YSgo04ixfUbO1FDM');

sender.send(message, regTokens, function (err, response) {
    if(err) {
      console.error(err);
      console.log(response)
    } else {
      console.log(response);
    }
});

console.log("i got here")