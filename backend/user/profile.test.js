
const request = require('supertest')
const app = require('../config/app')
const {user_collection, room_collection} = require('../config/mongodb_connection')

const rob_email = 'robithy_test_user@test.com'
const rob_name = 'robithy'
const bob_email = 'bobithy_test_user@test.com'
const bob_name = 'bobithy'
const tim_email = 'timothy_test_user@test.com'
const tim_name = 'timothy'
const pam_email = 'pambert_test_user@test.com'
const pam_name = 'pambert'
const sam_email = 'sambert_test_user@test.com'
const sam_name = 'sambert'
var emails = [rob_email, bob_email, tim_email, pam_email, sam_email]
var names = [rob_name, bob_name, tim_name, pam_name, sam_name]

async function initializeDatabase() {
  await user_collection.insertMany([{ name: names[0], email: emails[0] }, { name: names[1], email: emails[1] }, { name: names[2], email: emails[2] }])
}
async function resetDatabase() {
  await user_collection.deleteMany({email: {$in: emails}})
}

beforeAll(() => {
  initializeDatabase();
});

afterAll(() => {
  resetDatabase();
});

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

    // Comment out as empty name is now allowed.
    // it('PUT /profile - invalid name', async function () {
    //   const response = await request(app)
    //     .put('/user/profile')
    //     .query({
    //       email: emails[0]
    //     })
    //     .send({
    //       name: ""
    //     })
    //   expect(response.status).toEqual(400);
    //   expect(response.text).toEqual("Error: Invalid name");
    // });

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

    // Commenting out this test, as "name" is now allowed to be null.
    // it('PUT /profile - null name', async function () {
    //   const response = await request(app)
    //     .put('/user/profile')
    //     .query({
    //       email: emails[0]
    //     })
    //     .send({
    //       name: null
    //     })
    //   expect(response.status).toEqual(400);
    //   expect(response.text).toEqual("Error: Invalid name");
    // });

    it('PUT /profile - user not found', async function () {
      const response = await request(app)
        .put('/user/profile')
        .query({
          email: "null@null.com"
        })
        .send({
          name: "null"
        })
      expect(response.status).toEqual(404);
      expect(response.text).toEqual('{"response":"User not found."}');
    });
  })

  describe('POST /user/registration', function () {
    it('POST /registration in body - success', async function () {
      const response = await request(app)
        .post('/user/registration')
        .send({
          email: sam_email,
          name: sam_name,
          photo: "randomphotostring"
        })
      expect(response.status).toEqual(201);
      expect(response.text).toEqual("Success");
    });


    it('POST /registration - success', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: pam_email,
          name: pam_name
        })
      expect(response.status).toEqual(201);
      expect(response.text).toEqual("Success");
    });


    it('POST /registration - user exists', async function () {
      await user_collection.insertOne({ email: emails[1], name: names[1] })
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

    it('POST /registration - null email', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: null,
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

    it('POST /registration - null name', async function () {
      const response = await request(app)
        .post('/user/registration')
        .query({
          email: emails[0],
          name: null
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


    it('DELETE /registration - null email', async function () {
      const response = await request(app)
        .delete('/user/registration')
        .query({
          email: null,
          name: names[0]
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email");
    });

  })
});

