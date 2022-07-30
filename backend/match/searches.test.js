
// const user_profile_router = require('./profile_mock')
// const user_peers_router = require('./peers_mock')

const request = require('supertest');
const express = require('express');
const app = require('../config/app')


var user_collection = require('../config/mongodb_connection')

const ann_email = 'test_user_ann@test.com'
const ann_name = 'test_user_ann'
const bob_email = 'test_user_bob@test.com'
const bob_name = 'test_user_bob'
const chob_email = 'test_user_chob@test.com'
const chob_name = 'test_user_chob'
const dob_email = 'test_user_dob@test.com'
const dob_name = 'test_user_dob'
var emails = [ann_email, bob_email, chob_email, dob_email]
var names = [ann_name, bob_name, chob_name, dob_name]

async function initializeDatabase() {
  await user_collection.insertMany([{ name: names[0], email: emails[0] }, { name: names[1], email: emails[1] }, { name: names[2], email: emails[2] }, { name: names[3], email: emails[3] }])
}
async function resetDatabase() {
  await user_collection.deleteMany({})
}

// this is used as a delay function
// function sleep(ms) {
//   return new Promise((resolve) => {
//     setTimeout(resolve, ms);
//   });
// }

// this is used as another delay function
// await new Promise(res => setTimeout(() => { res() }, 200))


beforeAll(() => {
  initializeDatabase();
});

afterAll(() => {
  resetDatabase();
});


describe('Tests for Searches Submodule', function () {

  describe('GET /match/searches', function () {

    it('GET /match/searches illegal email', async function () {
      const response = await request(app)
        .get('/match/searches')
        .query({
          email: "!@#$"
        })
        .set('Accept', 'application/json')
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email")
    });

    it('GET /match/searches empty email', async function () {
      const response = await request(app)
        .get('/match/searches')
        .query({
          email: ""
        })
        .set('Accept', 'application/json')
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email")
    });

    it('GET /match/searches null email', async function () {
      const response = await request(app)
        .get('/match/searches')
        .query({
          email: null
        })
      expect(response.status).toEqual(400);
      expect(response.text).toEqual("Error: Invalid email")
    });












  })

  describe('POST /match/searches', function () {
    describe('POST /match/searches bad email', function () {
      it('POST /match/searches illegal email', async function () {
        const response = await request(app)
          .post('/match/searches')
          .query({
            email: "!@#$"
          })
          .set('Accept', 'application/json')
        expect(response.status).toEqual(400);
        expect(response.text).toEqual("Error: Invalid email")
      });

      it('POST /match/searches empty email', async function () {
        const response = await request(app)
          .post('/match/searches')
          .query({
            email: ""
          })
          .set('Accept', 'application/json')
        expect(response.status).toEqual(400);
        expect(response.text).toEqual("Error: Invalid email")
      });

      it('POST /match/searches null email', async function () {
        const response = await request(app)
          .post('/match/searches')
          .query({
            email: null
          })
        expect(response.status).toEqual(400);
        expect(response.text).toEqual("Error: Invalid email")
      });

    })
    describe('POST /match/searches bad search name', function () {
      it('POST /match/searches illegal search name', async function () {
        const response = await request(app)
          .post('/match/searches')
          .query({
            email: "test@test.com"
          })
          .send({
            search_name: "!@#$",
            activity: "!@#$",
            location_name: "!@#$",
            location_long: "!@#$",
            location_lati: "!@#$",
            max_range: "!@#$",
            max_budget: "!@#$"
          })
          .set('Accept', 'application/json')
        expect(response.status).toEqual(400);
        expect(response.text).toEqual("Error: Bad fields")
      });

      it('POST /match/searches null search name', async function () {
        const response = await request(app)
          .post('/match/searches')
          .query({
            email: "test@test.com"
          })
          .send({
          })
        expect(response.status).toEqual(400);
        expect(response.text).toEqual("Error: Bad fields")
      });

    })
    describe('POST /match/searches', function () {

      it('POST /match/searches no user', async function () {
        const response = await request(app)
          .post('/match/searches')
          .query({
            email: "null@null.com"
          })
          .send({
            search_name: "asdf",
            activity: "asdf",
            location_name: "asdf",
            location_long: 49.49,
            location_lati: 49.49,
            max_range: 3,
            max_budget: 300
          })
          .set('Accept', 'application/json')
        expect(response.status).toEqual(404);
        expect(JSON.parse(response.text).response).toEqual("User not found")
      })


      it('POST /match/searches success', async function () {
        await request(app)
          .post('/user/registration')
          .query({
            email: "Search@gmail.com",
            name: "Search"
          })

        const response = await request(app)
          .post('/match/searches')
          .query({
            email: "Search@gmail.com"
          })
          .send({
            search_name: "asdf",
            activity: "asdf",
            location_name: "asdf",
            location_long: 49.49,
            location_lati: 49.49,
            max_range: 3,
            max_budget: 300
          })
          .set('Accept', 'application/json')
        expect(response.status).toEqual(201);
        expect(JSON.parse(response.text).response).toEqual("Added new search")
      })


      it('POST /match/searches search exists', async function () {
        await request(app)
          .post('/user/registration')
          .query({
            email: "Searchexist@gmail.com",
            name: "Searchexist"
          })

        function create_search_object(body) {
          var ret_object = {
            search_name: body.search_name,
            activity: body.activity,
            location_name: body.location_name,
            location_long: body.location_long,
            location_lati: body.location_lati,
            max_range: body.max_range,
            max_budget: body.max_budget
          }
          return ret_object
        }

        var search_object = create_search_object({
          search_name: "Searchexist",
          activity: "asdf",
          location_name: "asdf",
          location_long: 49.49,
          location_lati: 49.49,
          max_range: 3,
          max_budget: 300
        })

        await user_collection.updateOne({ email: "Searchexist@gmail.com" }, { $push: { searches: search_object } })

        const response = await request(app)
          .post('/match/searches')
          .query({
            email: "Searchexist@gmail.com"
          })
          .send({
            search_name: "Searchexist",
            activity: "asdf",
            location_name: "asdf",
            location_long: 49.49,
            location_lati: 49.49,
            max_range: 3,
            max_budget: 300
          })
          .set('Accept', 'application/json')
        expect(response.status).toEqual(409);
        expect(JSON.parse(response.text).response).toEqual("Search already exists")
      })
    })
  })


  describe('DELETE /match/searches', function () {
    describe('DELETE /match/searches', function () {

      it('POST /match/searches no user', async function () {
        await request(app)
          .post('/match/searches')
          .query({
            email: "Search@gmail.com"
          })
          .send({
            search_name: "asdf",
            activity: "asdf",
            location_name: "asdf",
            location_long: 49.49,
            location_lati: 49.49,
            max_range: 3,
            max_budget: 300
          })
          .set('Accept', 'application/json')


        const response = await request(app)
          .delete('/match/searches')
          .query({
            email: "null@null.com",
            search_name: "asdf"
          })
          .set('Accept', 'application/json')
        expect(response.status).toEqual(404);
        expect(JSON.parse(response.text).response).toEqual("Removed search")
      })

    })
    describe('DELETE /match/searches', function () {

    })
    describe('DELETE /match/searches', function () {

    })
    describe('DELETE /match/searches', function () {

    })

  })

})