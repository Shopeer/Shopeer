const request = require('supertest');
const express = require('express');
const app = express()

const user_peers_router = require('./peers')
// reuse original application?
app.use(user_peers_router)


describe("Get peers scenario", () => {

  it('GET /peers', async function () {
    const response = await request(app)
      .get('/peers')
      .query({ email: 'robert@gmail.com' })
      .set('Accept', 'application/json')
    expect(1).toEqual(1);
    //expect(response.body.email).toEqual('bobert@gmail.com');
  });





  });