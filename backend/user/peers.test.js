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


beforeEach(() => {
  resetDatabase()
  return initializeDatabase()
});
afterAll(() => {
  return resetDatabase()
});

async function initializeDatabase() {
  // register the test users
  for (let i = 0; i < emails.length; i++) {
    await request(app).post('/user/registration').query({ name: names[i], email: emails[i]})
  }
  // make rob peers with bob and tim by sending mutual invitations
  await request(app).post('/user/invitations').query({ email: emails[0], target_peer_email: emails[1]})
  await request(app).post('/user/invitations').query({ email: emails[1], target_peer_email: emails[0]})
  await request(app).post('/user/invitations').query({ email: emails[0], target_peer_email: emails[2]})
  await request(app).post('/user/invitations').query({ email: emails[2], target_peer_email: emails[0]})
  // make bob send an invitation to tim and jim
  await request(app).post('/user/invitations').query({ email: emails[1], target_peer_email: emails[2]})
  await request(app).post('/user/invitations').query({ email: emails[1], target_peer_email: emails[3]})
  
}
async function resetDatabase() {
  for (let i = 0; i < emails.length; i++) {
    await request(app).delete('/user/registration').query({ name: names[i], email: emails[i]})
  }
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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
    // attempt to get rob's peerlist, which should contain bob and tim
    const response = await request(app).get('/user/peers').query({ email: emails[0] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(2);
    expect(response.body[0].name).toEqual(names[1]);
    expect(response.body[0].email).toEqual(emails[1]);
    expect(response.body[1].name).toEqual(names[2]);
    expect(response.body[1].email).toEqual(emails[2]);
    expect(response.status).toEqual(200);
  });

  it('should return an empty peerlist successfully', async function () {
    // attempt to get Jim's peerlist
    const response = await request(app).get('/user/peers').query({ email: emails[3] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(0);
    expect(response.status).toEqual(200);
  });

});

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    const thisUser = await request(app).get('/user/profile').query({ email: emails[0] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[1] }).set('Accept', 'application/json')
    
    // bob and rob should no longer be subsets of each others' peerlists
    expect(thisUser.peers).toEqual(expect.not.arrayContaining([emails[1]]))
    expect(targetUser.peers).toEqual(expect.not.arrayContaining([emails[0]]))
    expect(response.status).toEqual(200);
  });

});
    
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// describe("Get sent-invitations scenario", () => {
//   it('should return 404 for non-existing user', async function () {
//     const nonexistentEmail = "nonexisting_test_email@test.com"
//     // first try to delete the user from the database, just in case.
//     await request(app).delete('/user/registration').query({email: nonexistentEmail })
//     // attempt to get a nonexisting user's peerlist
//     const response = await request(app).get('/user/invitations').query({ email: nonexistentEmail }).set('Accept', 'application/json')
    
//     expect(response.body).toEqual({"response":"User not found."});
//     expect(response.status).toEqual(404);
//   });

//   it('should return an invitations list sucessfully', async function () {
//     // attempt to get bob's invitations list, which should contain tim and jim
//     const response = await request(app).get('/user/invitations').query({ email: emails[0] }).set('Accept', 'application/json')
    
//     expect(response.body.length).toEqual(2);
//     expect(response.body[0].name).toEqual(names[1]);
//     expect(response.body[0].email).toEqual(emails[1]);
//     expect(response.body[1].name).toEqual(names[2]);
//     expect(response.body[1].email).toEqual(emails[2]);
//     expect(response.status).toEqual(200);
//   });

//   it('should return an empty peerlist successfully', async function () {
//     // attempt to get Jim's peerlist
//     const response = await request(app).get('/user/peers').query({ email: emails[3] }).set('Accept', 'application/json')
    
//     expect(response.body.length).toEqual(0);
//     expect(response.status).toEqual(200);
//   });

// })


////////////////////////////////////////////////////////////////////////////////////////////////////////////////
describe("Retract a sent invitation scenario", () => {

  it('should return 404-user-not-found for non-existing user', async function () {
    const nonexistentEmail= "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to delete a random email from this nonexisting user's sent-invitations list
    const response = await request(app).delete('/user/invitations').query({ email: nonexistentEmail, target_peer_email: emails[0] }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 404-targetinvite-not-found for target invite not in sent invitations list', async function () {
    // attempt to delete jim from rob's invitations list. Jim is not currently in rob's invitations list
    const response = await request(app).delete('/user/invitations').query({ email: emails[0], target_peer_email: emails[3] }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"Target user not found."});
    expect(response.status).toEqual(404);
  });

  it('should successfully retract an invitation', async function () {
    // attempt to retract bob's invitation to tim
    const response = await request(app).delete('/user/invitations').query({ email: emails[1], target_peer_email: emails[2] }).set('Accept', 'application/json')
    const thisUser = await request(app).get('/user/profile').query({ email: emails[1] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[2] }).set('Accept', 'application/json')
    // tim should no longer be a subset of bob's invitations list
    // bob should no longer be a subset of tim's received invitations list
    expect(thisUser.invites).toEqual(expect.not.arrayContaining([emails[2]]))
    expect(targetUser.received_invites).toEqual(expect.not.arrayContaining([emails[1]]))
    expect(response.status).toEqual(200);
  });

});