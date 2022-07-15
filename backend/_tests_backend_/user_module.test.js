const request = require('supertest');

const user_profile_router = require('./profile_mock')
const user_peers_router = require('./peers_mock')


describe('Profile Submodule', function () {
  it('GET /profile', async function () {
    const response = await request(user_profile_router)
      .get('/profile')
      .query({ email: 'jimothy@gmail.com' })
      .set('Accept', 'application/json')
    expect(response.status).toEqual(200);
    expect(response.body.email).toEqual('jimothy@gmail.com');
    expect(response.headers["content-type"]).toMatch(/json/);
  });
  it('GET /profile', async function () {
    const response = await request(user_profile_router)
      .get('/profile')
      .query({ email: 'jimothy@gmail..com' })
      .set('Accept', 'application/json')
    expect(response.status).toEqual(400);
    expect(response.text).toEqual('Error');
  });

  it('PUT /profile', async function () {
    const response = await request(user_profile_router)
      .put('/profile')
      .query({
        email: 'jimothy@gmail.com',
        name: "Jimothy",
        description: "Cool bio",
        photo: "Cool photo"
      })
      .set('Accept', 'application/json')
    expect(response.status).toEqual(200);
    expect(response.body.email).toEqual('jimothy@gmail.com');
    expect(response.body.name).toEqual("Jimothy");
    expect(response.body.description).toEqual('Cool bio');
    expect(response.body.photo).toEqual('Cool photo');
    expect(response.headers["content-type"]).toMatch(/json/);
  });
  it('PUT /profile Error', async function () {
    const response = await request(user_profile_router)
      .put('/profile')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy",
        description: "Cool bio",
        photo: "Cool photo"
      })
      .set('Accept', 'application/json')
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error")
  });

  it('POST /registration', async function () {
    const response = await request(user_profile_router)
      .post('/registration')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('POST /registration Error', async function () {
    const response = await request(user_profile_router)
      .post('/registration')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });

  it('DELETE /registration', async function () {
    const response = await request(user_profile_router)
      .delete('/registration')
      .query({
        email: 'jimothy@gmail.com',
        FCM_token: "asdfqwer"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('DELETE /registration Error', async function () {
    const response = await request(user_profile_router)
      .delete('/registration')
      .query({
        email: 'jimothy@gmail..com',
        FCM_token: "asdfqwer"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });
});

describe('Peers Submodule', function () {
  it('GET /peers', async function () {
    const response = await request(user_peers_router)
      .get('/peers')
      .query({ email: 'jimothy@gmail.com' })
      .set('Accept', 'application/json')
    expect(response.status).toEqual(200);
    expect(response.body.email).toEqual('jimothy@gmail.com');
  });
  it('GET /peers', async function () {
    const response = await request(user_peers_router)
      .get('/peers')
      .query({ email: 'jimothy@gmail..com' })
      .set('Accept', 'application/json')
    expect(response.status).toEqual(400);
    expect(response.text).toEqual('Error');
  });

  it('DELETE /peers', async function () {
    const response = await request(user_peers_router)
      .delete('/peers')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('DELETE /peers Error', async function () {
    const response = await request(user_peers_router)
      .delete('/peers')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });

  it('GET /blocked', async function () {
    const response = await request(user_peers_router)
      .get('/blocked')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('GET /blocked Error', async function () {
    const response = await request(user_peers_router)
      .get('/blocked')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });

  it('POST /blocked', async function () {
    const response = await request(user_peers_router)
      .post('/blocked')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('POST /blocked Error', async function () {
    const response = await request(user_peers_router)
      .post('/blocked')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });


  it('DELETE /blocked', async function () {
    const response = await request(user_peers_router)
      .delete('/blocked')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('DELETE /blocked Error', async function () {
    const response = await request(user_peers_router)
      .delete('/blocked')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });


  it('GET /invitations', async function () {
    const response = await request(user_peers_router)
      .get('/invitations')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('GET /invitations Error', async function () {
    const response = await request(user_peers_router)
      .get('/invitations')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });

  it('POST /invitations', async function () {
    const response = await request(user_peers_router)
      .post('/invitations')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('POST /invitations Error', async function () {
    const response = await request(user_peers_router)
      .post('/invitations')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });


  it('DELETE /invitations', async function () {
    const response = await request(user_peers_router)
      .delete('/invitations')
      .query({
        email: "jimothy@gmail.com",
        name: "Jimothy"
      })
    expect(response.status).toEqual(200);
    expect(response.text).toEqual("Success");
  });
  it('DELETE /invitations Error', async function () {
    const response = await request(user_peers_router)
      .delete('/invitations')
      .query({
        email: 'jimothy@gmail..com',
        name: "Jimothy"
      })
    expect(response.status).toEqual(400);
    expect(response.text).toEqual("Error");
  });



});
