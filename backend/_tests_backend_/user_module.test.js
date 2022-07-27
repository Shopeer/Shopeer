
// const user_profile_router = require('./profile_mock')
// const user_peers_router = require('./peers_mock')

// const user_profile_router = require('../user/profile.js')

const request = require('supertest');
const express = require('express');
const app = express()
// reuse original application?
const user_profile_router = require('../user/profile.js');
app.use('*', user_profile_router);
app.use('/user', user_profile_router)

const user_peers_router = require('../user/peers.js');
app.use('*', user_peers_router);
app.use('/user', user_peers_router)



const { MongoClient } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)
const user_collection = mongoClient.db("shopeer_database").collection("user_collection")

/**database is initialized as follows:
 * Rob:
 * ---- peers: Bob, Tim
 * ---- invitations: 
 * ---- received invitations: 
 * ---- blocked:
 * Bob:
 * ---- peers: Rob
 * ---- invitations: Tim, Jim
 * ---- received invitations: 
 * ---- blocked:
 * Tim:
 * ---- peers: Rob
 * ---- invitations: 
 * ---- received invitations: Bob
 * ---- blocked:
 * Jim
 * ---- peers: 
 * ---- invitations: 
 * ---- received invitations: Bob
 * ---- blocked:
 * Tam:
 * ---- peers: Rob
 * ---- invitations: 
 * ---- received invitations: Bob
 * ---- blocked:
 * Pam
 * ---- peers: 
 * ---- invitations: 
 * ---- received invitations: Bob
 * ---- blocked:
 * */

const rob_email = 'rob_test_user@test.com'
const rob_name = 'rob'
const bob_email = 'bob_test_user@test.com'
const bob_name = 'bob'
const tim_email = 'tim_test_user@test.com'
const tim_name = 'tim'
const jim_email = 'jim_test_user@test.com'
const jim_name = 'jim'
const tam_email = 'tam_test_user@test.com'
const tam_name = 'tam'
const pam_email = 'pam_test_user@test.com'
const pam_name = 'pam'
var emails = [rob_email, bob_email, tim_email, jim_email, tam_email, pam_email]
var names = [rob_name, bob_name, tim_name, jim_name, tam_name, pam_name]


async function initializeDatabase() {
  for (let i = 0; i < emails.length; i++) {
    await request(app).post('/user/registration').query({ name: names[i], email: emails[i] })
  }
  await request(app).delete('/user/registration').query({ name: names[0], email: emails[0] })
}
async function resetDatabase() {
  for (let i = 0; i < emails.length; i++) {
    await request(app).delete('/user/registration').query({ name: names[i], email: emails[i] })
  }
}


beforeEach(() => {
  initializeDatabase();
});

afterEach(() => {
  resetDatabase();
});


describe('Tests for Profile Submodule', function () {
  // it('GET /profile', async function () {
  //   const response = await request(user_profile_router)
  //     .get('/profile')
  //     .query({ email: 'jimothy@gmail.com' })
  //     .set('Accept', 'application/json')
  //   expect(response.status).toEqual(200);
  //   expect(response.body.email).toEqual('jimothy@gmail.com');
  //   expect(response.headers["content-type"]).toMatch(/json/);
  // });

  describe('Tests for Post /registration', function () {
    
    it('POST /registration', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: emails[0],
          name: names[0]
        })
      expect(response.status).toEqual(200);
      expect(response.text).toEqual("Success");
    });

    it('POST /registration - invalid email', async function () {
      await request(app).delete('/user/registration').query({ name: names[0], email: emails[0] })
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: "!@#$",
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });



  })


  // it('GET /profile', async function () {
  //   const response = await request(user_profile_router)
  //     .get('/profile')
  //     .query({ email: 'jimothy@gmail..com' })
  //     .set('Accept', 'application/json')
  //   expect(response.status).toEqual(400);
  //   expect(response.text).toEqual('Error');
  // });

  // it('PUT /profile', async function () {
  //   const response = await request(user_profile_router)
  //     .put('/profile')
  //     .query({
  //       email: 'jimothy@gmail.com',
  //       name: "Jimothy",
  //       description: "Cool bio",
  //       photo: "Cool photo"
  //     })
  //     .set('Accept', 'application/json')
  //   expect(response.status).toEqual(200);
  //   expect(response.body.email).toEqual('jimothy@gmail.com');
  //   expect(response.body.name).toEqual("Jimothy");
  //   expect(response.body.description).toEqual('Cool bio');
  //   expect(response.body.photo).toEqual('Cool photo');
  //   expect(response.headers["content-type"]).toMatch(/json/);
  // });
  // it('PUT /profile Error', async function () {
  //   const response = await request(user_profile_router)
  //     .put('/profile')
  //     .query({
  //       email: 'jimothy@gmail..com',
  //       name: "Jimothy",
  //       description: "Cool bio",
  //       photo: "Cool photo"
  //     })
  //     .set('Accept', 'application/json')
  //   expect(response.status).toEqual(400);
  //   expect(response.text).toEqual("Error")
  // });

  // it('POST /registration', async function () {
  //   const response = await request(user_profile_router)
  //     .post('/registration')
  //     .query({
  //       email: "jimothy@gmail.com",
  //       name: "Jimothy"
  //     })
  //   expect(response.status).toEqual(200);
  //   expect(response.text).toEqual("Success");
  // });
  // it('POST /registration Error', async function () {
  //   const response = await request(user_profile_router)
  //     .post('/registration')
  //     .query({
  //       email: 'jimothy@gmail..com',
  //       name: "Jimothy"
  //     })
  //   expect(response.status).toEqual(400);
  //   expect(response.text).toEqual("Error");
  // });

  // it('DELETE /registration', async function () {
  //   const response = await request(user_profile_router)
  //     .delete('/registration')
  //     .query({
  //       email: 'jimothy@gmail.com',
  //       FCM_token: "asdfqwer"
  //     })
  //   expect(response.status).toEqual(200);
  //   expect(response.text).toEqual("Success");
  // });
  // it('DELETE /registration Error', async function () {
  //   const response = await request(user_profile_router)
  //     .delete('/registration')
  //     .query({
  //       email: 'jimothy@gmail..com',
  //       FCM_token: "asdfqwer"
  //     })
  //   expect(response.status).toEqual(400);
  //   expect(response.text).toEqual("Error");
  // });
});

// describe('Peers Submodule', function () {
//   it('GET /peers', async function () {
//     const response = await request(user_peers_router)
//       .get('/peers')
//       .query({ email: 'jimothy@gmail.com' })
//       .set('Accept', 'application/json')
//     expect(response.status).toEqual(200);
//     expect(response.body.email).toEqual('jimothy@gmail.com');
//   });
//   it('GET /peers', async function () {
//     const response = await request(user_peers_router)
//       .get('/peers')
//       .query({ email: 'jimothy@gmail..com' })
//       .set('Accept', 'application/json')
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual('Error');
//   });

//   it('DELETE /peers', async function () {
//     const response = await request(user_peers_router)
//       .delete('/peers')
//       .query({
//         email: "jimothy@gmail.com",
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(200);
//     expect(response.text).toEqual("Success");
//   });
//   it('DELETE /peers Error', async function () {
//     const response = await request(user_peers_router)
//       .delete('/peers')
//       .query({
//         email: 'jimothy@gmail..com',
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual("Error");
//   });

//   it('GET /blocked', async function () {
//     const response = await request(user_peers_router)
//       .get('/blocked')
//       .query({
//         email: "jimothy@gmail.com",
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(200);
//     expect(response.text).toEqual("Success");
//   });
//   it('GET /blocked Error', async function () {
//     const response = await request(user_peers_router)
//       .get('/blocked')
//       .query({
//         email: 'jimothy@gmail..com',
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual("Error");
//   });

//   it('POST /blocked', async function () {
//     const response = await request(user_peers_router)
//       .post('/blocked')
//       .query({
//         email: "jimothy@gmail.com",
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(200);
//     expect(response.text).toEqual("Success");
//   });
//   it('POST /blocked Error', async function () {
//     const response = await request(user_peers_router)
//       .post('/blocked')
//       .query({
//         email: 'jimothy@gmail..com',
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual("Error");
//   });


//   it('DELETE /blocked', async function () {
//     const response = await request(user_peers_router)
//       .delete('/blocked')
//       .query({
//         email: "jimothy@gmail.com",
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(200);
//     expect(response.text).toEqual("Success");
//   });
//   it('DELETE /blocked Error', async function () {
//     const response = await request(user_peers_router)
//       .delete('/blocked')
//       .query({
//         email: 'jimothy@gmail..com',
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual("Error");
//   });


//   it('GET /invitations', async function () {
//     const response = await request(user_peers_router)
//       .get('/invitations')
//       .query({
//         email: "jimothy@gmail.com",
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(200);
//     expect(response.text).toEqual("Success");
//   });
//   it('GET /invitations Error', async function () {
//     const response = await request(user_peers_router)
//       .get('/invitations')
//       .query({
//         email: 'jimothy@gmail..com',
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual("Error");
//   });

//   it('POST /invitations', async function () {
//     const response = await request(user_peers_router)
//       .post('/invitations')
//       .query({
//         email: "jimothy@gmail.com",
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(200);
//     expect(response.text).toEqual("Success");
//   });
//   it('POST /invitations Error', async function () {
//     const response = await request(user_peers_router)
//       .post('/invitations')
//       .query({
//         email: 'jimothy@gmail..com',
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual("Error");
//   });


//   it('DELETE /invitations', async function () {
//     const response = await request(user_peers_router)
//       .delete('/invitations')
//       .query({
//         email: "jimothy@gmail.com",
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(200);
//     expect(response.text).toEqual("Success");
//   });
//   it('DELETE /invitations Error', async function () {
//     const response = await request(user_peers_router)
//       .delete('/invitations')
//       .query({
//         email: 'jimothy@gmail..com',
//         name: "Jimothy"
//       })
//     expect(response.status).toEqual(400);
//     expect(response.text).toEqual("Error");
//   });



// });
