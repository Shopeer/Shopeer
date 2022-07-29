
// const user_profile_router = require('./profile_mock')
// const user_peers_router = require('./peers_mock')

const request = require('supertest');
const express = require('express');
const validator = require('validator')
const app = require('../config/app')


var user_collection = require('../config/mongodb_connection')

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

const rob_email = 'robithy_test_user@test.com'
const rob_name = 'robithy'
const bob_email = 'bobithy_test_user@test.com'
const bob_name = 'bobithy'
const tim_email = 'timothy_test_user@test.com'
const tim_name = 'timothy'
// const jim_email = 'jimothy_test_user@test.com'
// const jim_name = 'jimothy'
// const tam_email = 'tambert_test_user@test.com'
// const tam_name = 'tambert'
const pam_email = 'pambert_test_user@test.com'
const pam_name = 'pambert'
// var emails = [rob_email, bob_email, tim_email, jim_email, tam_email, pam_email]
// var names = [rob_name, bob_name, tim_name, jim_name, tam_name, pam_name]
var emails = [rob_email, bob_email, tim_email]
var names = [rob_name, bob_name, tim_name]

async function initializeDatabase() {
  // for (let i = 1; i < emails.length; i++) {
  //   // await request(app).post('/user/registration').query({ name: names[i], email: emails[i] })
  //   await user_collection.insertOne({ name: names[i], email: emails[i] })
  // }
  await user_collection.insertMany([{ name: names[0], email: emails[0] }, { name: names[1], email: emails[1] }, { name: names[2], email: emails[2] }])
}
async function resetDatabase() {
  // for (let i = 0; i < emails.length; i++) {
  //   // await request(app).delete('/user/registration').query({ name: names[i], email: emails[i] })
  //   // await user_collection.deleteMany({ name: names[i], email: emails[i] })
  //   await user_collection.deleteMany({})
  // }
  await user_collection.deleteMany({})
}

function sleep(ms) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
}
beforeAll(() => {
  initializeDatabase();
});

afterAll(() => {
  resetDatabase();
});

// this is used as a delay function
// await new Promise(res => setTimeout(() => { res() }, 200))

describe('Tests for Profile Submodule', function () {
  describe('GET /user/profile', function () {
    it('GET /profile success', async function () {
      await user_collection.insertOne({ name: names[0], email: emails[0] })
      const response = await request(app)
        .get('/user/profile')
        .query({
          email: emails[0]
        })
        .set('Accept', 'application/json')
      expect(response.status).toEqual(200);
      expect(response.body.email).toEqual(emails[0]);
      expect(response.headers["content-type"]).toMatch(/json/);
    });
    it('GET /profile illegal email', async function () {
      const response = await request(app)
        .get('/user/profile')
        .query({
          email: "!@#$"
        })
        .set('Accept', 'application/json')
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email")
    });
    it('GET /profile empty email', async function () {
      const response = await request(app)
        .get('/user/profile')
        .query({
          email: ""
        })
        .set('Accept', 'application/json')
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email")
    });
    it('GET /profile null email', async function () {
      const response = await request(app)
        .get('/user/profile')
        .query({
          email: null
        })
        .set('Accept', 'application/json')
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email")
    });
    it('GET /profile user does not exist', async function () {
      const response = await request(app)
        .get('/user/profile')
        .query({
          email: "12344321@gmail.com",
          name: "hi"
        })
        .set('Accept', 'application/json')
      expect(response.status).toEqual(404);
      expect(JSON.parse(response.text).response).toEqual("User does not exist")
    });
  })

  describe('PUT /user/profile', function () {
    it('PUT /profile - success', async function () {
      await user_collection.insertOne({ name: names[0], email: emails[0] })
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: emails[0]
        })
        .send({
          name: names[0],
          description: "cool description",
          photo: "cool photo"
        })
      expect(response.status).toEqual(200);
      expect(response.text).toEqual("Success");
    });

    it('PUT /profile - invalid email', async function () {
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: "!@#$"
        })
        .send({
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('PUT /profile - invalid name', async function () {
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: emails[0]
        })
        .send({
          name: "!@#$"
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid name");
    });

    it('PUT /profile - invalid email', async function () {
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: ""
        })
        .send({
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('PUT /profile - invalid name', async function () {
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: emails[0]
        })
        .send({
          name: ""
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid name");
    });

    it('PUT /profile - null email', async function () {
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: null
        })
        .send({
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('PUT /profile - null name', async function () {
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: emails[0]
        })
        .send({
          name: null
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid name");
    });
  })

  describe('POST /user/registration', function () {
    it('POST /registration - success', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: pam_email,
          name: pam_name
        })
      expect(response.status).toEqual(200);
      expect(response.text).toEqual("Success");
    });


    it('POST /registration - user exists', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: emails[1],
          name: names[1]
        })
      expect(response.status).toEqual(409);
      expect(response.text).toEqual("User already exists");
    });

    it('POST /registration - invalid email', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: "!@#$",
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('POST /registration - invalid name', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: emails[0],
          name: "!@#$"
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid name");
    });

    it('POST /registration - invalid email', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: "",
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('POST /registration - invalid name', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: emails[0],
          name: ""
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid name");
    });
  })

  describe('DELETE /user/registration', function () {
    it('DELETE /registration - success', async function () {
      await user_collection.insertOne({ email: pam_email, name: pam_name })
      const response = await request(app)
        .delete('/user/registration')
        .query({
          email: pam_email,
          name: pam_name
        })
      expect(response.status).toEqual(200);
      expect(response.text).toEqual("User deleted");
    });

    it('DELETE /registration - user does not exists', async function () {
      const response = await request(app)
        .delete('/user/registration')
        .query({
          email: pam_email,
          name: pam_name
        })
      expect(response.status).toEqual(404);
      expect(response.text).toEqual("User does not exist");
    });

    it('DELETE /registration - invalid email', async function () {
      const response = await request(app)
        .delete('/user/registration')
        .query({
          email: "!@#$",
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('DELETE /registration - invalid name', async function () {
      const response = await request(app)
        .delete('/user/registration')
        .query({
          email: emails[0],
          name: "!@#$"
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid name");
    });

    it('DELETE /registration - invalid email', async function () {
      const response = await request(app)
        .delete('/user/registration')
        .query({
          email: "",
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('DELETE /registration - invalid name', async function () {
      const response = await request(app)
        .delete('/user/registration')
        .query({
          email: emails[0],
          name: ""
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid name");
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
