
// const user_profile_router = require('./profile_mock')
// const user_peers_router = require('./peers_mock')

const request = require('supertest');
const express = require('express');
const validator = require('validator')
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





    
})