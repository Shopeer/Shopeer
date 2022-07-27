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

/**database is initialized as follows:
 * Rob:
 * ---- peers: Bob, Tim
 * ---- invitations: Jim
 * ---- received invitations: 
 * ---- blocked:
 * Bob:
 * ---- peers: Rob
 * ---- invitations: 
 * ---- received invitations: 
 * ---- blocked:
 * Tim:
 * ---- peers: Rob
 * ---- invitations: 
 * ---- received invitations: 
 * ---- blocked:
 * Jim
 * ---- peers: 
 * ---- invitations: 
 * ---- received invitations: Rob
 * ---- blocked:
 * Tam:
 * ---- peers: 
 * ---- invitations: 
 * ---- received invitations: 
 * ---- blocked:
 * Pam: 
 * ---- peers:
 * ---- invitations: 
 * ---- received invitations: 
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


beforeAll(() => {
  return resetDatabase()
});
// afterAll(() => {
//   return resetDatabase()
// });

async function initializeDatabase() {
  for (let i = 0; i < emails.length; i++) {
    await request(app).post('/user/registration').query({ name: names[i], email: emails[i]})
  }
  
}
async function resetDatabase() {
  for (let i = 0; i < emails.length; i++) {
    await request(app).delete('/user/registration').query({ name: names[i], email: emails[i]})
  }
  
}

/**
 * The get_peers scenario should only run if post_invitations passes
 */
describe("Get all peers scenario", () => {

  it('should return 404 for non-existing user', async function () {
    const nonexistentEmail = "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to get a nonexisting user's peerlist
    const response = await request(app).get('/user/peers').query({ email: nonexistentEmail }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return a peerlist sucessfully', async function () {
    // first, register rob, bob, and tim.
    await request(app).post('/user/registration').query({ name: names[0], email: emails[0]})
    await request(app).post('/user/registration').query({ name: names[1], email: emails[1]})
    await request(app).post('/user/registration').query({ name: names[2], email: emails[2]})
    // next, make rob peers with bob and tim by sending mutual invitations
    await request(app).post('/user/invitations').query({ email: emails[0], target_peer_email: emails[1]})
    await request(app).post('/user/invitations').query({ email: emails[1], target_peer_email: emails[0]})
    await request(app).post('/user/invitations').query({ email: emails[0], target_peer_email: emails[2]})
    await request(app).post('/user/invitations').query({ email: emails[2], target_peer_email: emails[0]})
    // attempt to get rob's peerlist
    const response = await request(app).get('/user/peers').query({ email: emails[0] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(2);
    expect(response.body[0].name).toEqual(names[1]);
    expect(response.body[0].email).toEqual(emails[1]);
    expect(response.body[1].name).toEqual(names[2]);
    expect(response.body[1].email).toEqual(emails[2]);
    expect(response.status).toEqual(200);
  });

  it('should return an empty peerlist successfully', async function () {
    // first, register Jim
    await request(app).post('/user/registration').query({ name: names[3], email: emails[3]})
    // attempt to get Jim's peerlist
    const response = await request(app).get('/user/peers').query({ email: emails[3] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(0);
    expect(response.status).toEqual(200);
  });

  });

  /**
 * The get_peers scenario should only run if post_invitations passes
 */
describe("Delete peer scenario", () => {

  it('should return 404-user-not-found for non-existing user', async function () {
    const nonexistentEmail= "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to delete a random email from this nonexisting user's peerlist
    const response = await request(app).delete('/user/peers').query({ email: nonexistentEmail, target_peer_email: emails[0] }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 404-targetpeer-not-found for target peer not in peerlist', async function () {
    // attempt to delete jim from rob's peerlist
    const response = await request(app).delete('/user/peers').query({ email: emails[0], target_peer_email: emails[3] }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"Target peer not found."});
    expect(response.status).toEqual(404);
  });

  it('should successfully delete a peer', async function () {
    // attempt to get bob from rob's peerlist
    const response = await request(app).delete('/user/peers').query({ email: emails[0], target_peer_email: emails[1] }).set('Accept', 'application/json')
    
    expect(response.body[0].modifiedCount).toEqual(1);
    expect(response.body[1].modifiedCount).toEqual(1);
    expect(response.status).toEqual(200);
  });

  });