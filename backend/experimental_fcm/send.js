
var admin = require("firebase-admin");
var messaging = require('firebase-admin/messaging');

var serviceAccount = require("../../../cpen-shopeer-firebase-adminsdk-i0ot8-422a1bc1f5.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

// const { initializeApp } = require('firebase-admin/app');
// initializeApp({
//     credential: applicationDefault()
// });


var regTokens = ['ddKNsLykRXGHOmJEz6ubJp:APA91bGLKjD_3lzU0J2ADavLck8eJRPgQRNp5-ReMr1YRHaDAYj-mqgWJEeI1bgybDAoRBnPD8srogCnxf2fBMr_E7WyN_hsQUxKXDspmMRK_u8gvGt5vSeHDEbqdj9UDS-x60yOoVVd'];

var payload = {
    data: {
        MyKey1: "Hello"
    }
}

var options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
}

try {
    admin.messaging().sendToDevice(regTokens, payload, options)
    .then(function(response){
        console.log("Success sent message: ", response)
    })
    .catch(function(error){
        console.log("Error: ", error)
    })

} catch {
    console.log("Caught Error")
}
