
var admin = require("firebase-admin");

var serviceAccount = require("./cpen-shopeer-firebase-adminsdk.json");

// admin.initializeApp({
//   credential: admin.credential.cert(serviceAccount)
// });


admin.initializeApp({
    credential: admin.credential.applicationDefault(),
  });


// Unique to device
var registrationToken = "ddKNsLykRXGHOmJEz6ubJp:APA91bGLKjD_3lzU0J2ADavLck8eJRPgQRNp5-ReMr1YRHaDAYj-mqgWJEeI1bgybDAoRBnPD8srogCnxf2fBMr_E7WyN_hsQUxKXDspmMRK_u8gvGt5vSeHDEbqdj9UDS-x60yOoVVd"

var payload = {
    data: {
        MyKey1: "Hello Firebase!-asdf"
    },
    token: registrationToken
};

var options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
}


admin.messaging().send(payload)
    .then(function(response) {
        console.log("Sucess sent message:", response)
    })
    .catch(function(error) {
        console.log("Error sending message:", error)
    })

// getMessaging().send(payload)
//     .then(function(response) {
//         console.log("Sucess sent message:", response)
//     })
//     .catch(function(error) {
//         console.log("Error sending message:", error)
//     })