const request = require('supertest');
const express = require('express');
const app = express()
// reuse original application?
const user_profile_router = require('../user/profile.js');
app.use('*', user_profile_router);
app.use('/user', user_profile_router)

const user_peers_router = require('../user/peers.js');
const MongoClient = require('mongo-mock/lib/mongo_client.js');
app.use('*', user_peers_router);
app.use('/user', user_peers_router)

/**database is initialized as follows:
 * Rob: 0
 * ---- peers: Bob, Tim
 * ---- invitations: 
 * ---- received invitations: 
 * ---- blocked:
 * Bob: 1
 * ---- peers: Rob
 * ---- invitations: Tim, Jim
 * ---- received invitations: 
 * ---- blocked:
 * Tim: 2
 * ---- peers: Rob
 * ---- invitations: 
 * ---- received invitations: Bob
 * ---- blocked:
 * Jim 3
 * ---- peers: 
 * ---- invitations: 
 * ---- received invitations: Bob
 * ---- blocked:
 * Tam 4
 * ---- peers: 
 * ---- invitations: (she sends one to Bob)
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


beforeEach(() => {
  resetDatabase()
  return initializeDatabase()
});
afterEach(() => {
  return resetDatabase()
});
// afterAll(() => {
// })

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
  // make jim block tam and pam
  await request(app).post('/user/blocked').query({ email: emails[3], target_peer_email: emails[4]})
  await request(app).post('/user/blocked').query({ email: emails[3], target_peer_email: emails[5]})
  
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
const nonexistentEmail= "nonexisting_test_email@test.com"
  it('should return 404-user-not-found for non-existing user', async function () {
    
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
    // attempt to delete bob from rob's peerlist
    const response = await request(app).delete('/user/peers').query({ email: emails[0], target_peer_email: emails[1] }).set('Accept', 'application/json')
    const thisUser = await request(app).get('/user/profile').query({ email: emails[0] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[1] }).set('Accept', 'application/json')
    
    // bob and rob should no longer be subsets of each others' peerlists
    expect(thisUser.body.peers).toEqual(expect.not.arrayContaining([emails[1]]))
    expect(targetUser.body.peers).toEqual(expect.not.arrayContaining([emails[0]]))
    expect(response.status).toEqual(200);
  });

});
    
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
describe("Get sent-invitations scenario", () => {
  it('should return 404 for non-existing user', async function () {
    const nonexistentEmail = "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to get a nonexisting user's sent invitations list
    const response = await request(app).get('/user/invitations').query({ email: nonexistentEmail }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return an invitations list sucessfully', async function () {
    // attempt to get bob's invitations list, which should contain tim and jim
    const response = await request(app).get('/user/invitations').query({ email: emails[1] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(2);
    expect(response.body[0].name).toEqual(names[2]);
    expect(response.body[0].email).toEqual(emails[2]);
    expect(response.body[1].name).toEqual(names[3]);
    expect(response.body[1].email).toEqual(emails[3]);
    expect(response.status).toEqual(200);
  });

  it('should return an empty invitation list successfully', async function () {
    // attempt to get Tim's peerlist
    const response = await request(app).get('/user/invitations').query({ email: emails[2] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(0);
    expect(response.status).toEqual(200);
  });

})

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
describe("Get received-invitations scenario", () => {
  it('should return 404 for non-existing user', async function () {
    const nonexistentEmail = "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to get a nonexisting user's received invitations list
    const response = await request(app).get('/user/invitations/received').query({ email: nonexistentEmail }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return a received-invitations list sucessfully', async function () {
    // attempt to get tim's received-invitation list which should contain bob
    const response = await request(app).get('/user/invitations/received').query({ email: emails[2] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(1);
    expect(response.body[0].name).toEqual(names[1]);
    expect(response.body[0].email).toEqual(emails[1]);
    expect(response.status).toEqual(200);
  });

  it('should return an empty invitation list successfully', async function () {
    // attempt to get rob's received invitations list
    const response = await request(app).get('/user/invitations/received').query({ email: emails[0] }).set('Accept', 'application/json')
    
    expect(response.body.length).toEqual(0);
    expect(response.status).toEqual(200);
  });

})


////////////////////////////////////////////////////////////////////////////////////////////////////////////////
describe("Send an invitation", () => {
  it('should return 404 for non-existing user', async function () {
    const nonexistentEmail = "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to post an invitation to Rob
    const response = await request(app).post('/user/invitations').query({ email: nonexistentEmail, target_peer_email: emails[0] })

    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 404 for non-existing target user', async function () {
    const nonexistentEmail = "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to post an invitation to Rob
    const response = await request(app).post('/user/invitations').query({ email: emails[0], target_peer_email: nonexistentEmail })
    console.log(response.body)
    console.log("above should be 404 response")
    expect(response.body).toEqual({"response":"Target user not found."});
    expect(response.status).toEqual(404);
  });

  it('should successfully post an invitation', async function () {
    // attempt to post an invitation from pam to tam
    const response = await request(app).post('/user/invitations').query({ email: emails[5], target_peer_email: emails[4] })
    const thisUser = await request(app).get('/user/profile').query({ email: emails[5] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[4] }).set('Accept', 'application/json')

    expect(thisUser.body.invites).toEqual(expect.arrayContaining([emails[4]]))
    expect(targetUser.body.received_invites).toEqual(expect.arrayContaining([emails[5]]))
    expect(response.body).toEqual({"response":"Success, invitation sent."});
    expect(response.status).toEqual(200);
  });
  it('should successfully create new peers', async function () {
    // first post an invitation from pam to tam
    await request(app).post('/user/invitations').query({ email: emails[5], target_peer_email: emails[4] })
    // then post an invitation from tam to pam. This should make them peers
    response = await request(app).post('/user/invitations').query({ email: emails[4], target_peer_email: emails[5] })
    const thisUser = await request(app).get('/user/profile').query({ email: emails[4] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[5] }).set('Accept', 'application/json')

    expect(thisUser.body.invites).toEqual(expect.not.arrayContaining([emails[5]]))
    expect(thisUser.body.received_invites).toEqual(expect.not.arrayContaining([emails[5]]))
    expect(thisUser.body.peers).toEqual(expect.arrayContaining([emails[5]]))
    expect(targetUser.body.invites).toEqual(expect.not.arrayContaining([emails[4]]))
    expect(targetUser.body.received_invites).toEqual(expect.not.arrayContaining([emails[4]]))
    expect(targetUser.body.peers).toEqual(expect.arrayContaining([emails[4]]))
    
    expect(response.body).toEqual({"response":"Success, both are now peers."});
    expect(response.status).toEqual(201);
  });
  it('should return 409 for an invite conflict', async function () {
    // first post an invitation from bob to tam
    await request(app).post('/user/invitations').query({ email: emails[1], target_peer_email: emails[5] })
    // then repeat
    response = await request(app).post('/user/invitations').query({ email: emails[1], target_peer_email: emails[5] })
    expect(response.body).toEqual({"response":"Target already in invitation list."});
    expect(response.status).toEqual(409);
  });
  it('should return 409 for a peer conflict', async function () {
    // rob and tim are already peers. try to repeat:
    response = await request(app).post('/user/invitations').query({ email: emails[2], target_peer_email: emails[0] })
    expect(response.body).toEqual({"response":"Target already in peerlist."});
    expect(response.status).toEqual(409);
  });
  it('should return 400 if a blocked user tries to send an invite', async function () {
    // pam attempts to send jim an invite
    const response = await request(app).post('/user/invitations').query({ email: emails[5], target_peer_email: emails[3] })
    const thisUser = await request(app).get('/user/profile').query({ email: emails[5] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[3] }).set('Accept', 'application/json')

    expect(thisUser.body.invites).toEqual(expect.not.arrayContaining([emails[3]]))
    expect(targetUser.body.received_invites).toEqual(expect.not.arrayContaining([emails[5]]))
    expect(response.body).toEqual({"response":"The target user cannot be invited."});
    expect(response.status).toEqual(400);
  });
  it('should return 409 if a user tries to invite themself', async function () {
    // rob attempts to invite self
    const response = await request(app).post('/user/invitations').query({ email: emails[0], target_peer_email: emails[0] })
    const thisUser = await request(app).get('/user/profile').query({ email: emails[0] }).set('Accept', 'application/json')

    expect(thisUser.body.invites).toEqual(expect.not.arrayContaining([emails[0]]))
    expect(thisUser.body.received_invites).toEqual(expect.not.arrayContaining([emails[0]]))
    expect(response.body).toEqual({"response":"Cannot operate on self."});
    expect(response.status).toEqual(409);
  });


})

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
    expect(thisUser.body.invites).toEqual(expect.not.arrayContaining([emails[2]]))
    expect(targetUser.body.received_invites).toEqual(expect.not.arrayContaining([emails[1]]))
    expect(response.status).toEqual(200);
  });

});

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
describe("Get blocklist scenario", () => {

  it('should return 404-user-not-found for non-existing user', async function () {
    const nonexistentEmail = "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    const response = await request(app).get('/user/blocked').query({ email: nonexistentEmail }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });
  it('should successfully return blocklist', async function () {
    // jim has blocked tam and pam
    const response = await request(app).get('/user/blocked').query({ email: emails[3] }).set('Accept', 'application/json')
    // get blocked returns a list of emails instead of a list of objects per FE's request
    expect(response.body).toEqual([emails[4], emails[5]]);
    expect(response.status).toEqual(200);
  });

  it('should return an empty blocklist successfully', async function () {
    // attempt to get Tim's blocklist
    const response = await request(app).get('/user/blocked').query({ email: emails[2] }).set('Accept', 'application/json')
    expect(response.body).toEqual([]);
    expect(response.status).toEqual(200);
  });


});

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
describe("Block user scenario", () => {
  const nonexistentEmail= "nonexisting_test_email@test.com"
  it('should return 404-user-not-found for non-existing user', async function () {
    
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt block
    const response = await request(app).post('/user/blocked').query({ email: nonexistentEmail, target_peer_email: emails[0] }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 404-not-found for nonexisting block target', async function () {
    // attempt to delete jim from rob's invitations list. Jim is not currently in rob's invitations list
    const response = await request(app).post('/user/blocked').query({ email: emails[0], target_peer_email: nonexistentEmail }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response":"Target user not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 409 for a block conflict', async function () {
    // jim already has tam blocked. if he tries again he should get a conflict
    const response = await request(app).post('/user/blocked').query({ email: emails[3], target_peer_email: emails[4] }).set('Accept', 'application/json')
    expect(response.status).toEqual(409);
    expect(response.body).toEqual({"response":"User already in blocklist."});
  });

  it('should successfully block someone who was previously a peer', async function () {
    // rob and tim are currently peers. 
    // if rob blocks tim, they should no longer be peers
    const response = await request(app).post('/user/blocked').query({ email: emails[0], target_peer_email: emails[2] }).set('Accept', 'application/json')
    const thisUser = await request(app).get('/user/profile').query({ email: emails[0] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[2] }).set('Accept', 'application/json')
    // check they they are no longer peers
    expect(thisUser.body.blocked).toEqual(expect.arrayContaining([emails[2]]))
    expect(thisUser.body.peers).toEqual(expect.not.arrayContaining([emails[2]]))
    expect(targetUser.body.peers).toEqual(expect.not.arrayContaining([emails[0]]))
    expect(response.status).toEqual(201);
  });
  it('should successfully block someone who previously sent an invite', async function () {
    // jim received an invite from bob
    // jim attempts to block bob:
    const response = await request(app).post('/user/blocked').query({ email: emails[3], target_peer_email: emails[1] }).set('Accept', 'application/json')
    const thisUser = await request(app).get('/user/profile').query({ email: emails[3] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[1] }).set('Accept', 'application/json')
    // check the invite is removed
    expect(thisUser.body.blocked).toEqual(expect.arrayContaining([emails[1]]))
    expect(thisUser.body.received_invites).toEqual(expect.not.arrayContaining([emails[1]]))
    expect(targetUser.body.invites).toEqual(expect.not.arrayContaining([emails[3]]))
    expect(response.status).toEqual(201);
  });
  it('should successfully block someone who previously received an invite', async function () {
    // bob sent an invite to jim. Now bob wants to block jim
    const response = await request(app).post('/user/blocked').query({ email: emails[1], target_peer_email: emails[3] }).set('Accept', 'application/json')
    const targetUser = await request(app).get('/user/profile').query({ email: emails[3] }).set('Accept', 'application/json')
    const thisUser = await request(app).get('/user/profile').query({ email: emails[1] }).set('Accept', 'application/json')
    // check the invite is removed
    expect(thisUser.body.blocked).toEqual(expect.arrayContaining([emails[3]]))
    expect(thisUser.body.received_invites).toEqual(expect.not.arrayContaining([emails[3]]))
    expect(targetUser.body.invites).toEqual(expect.not.arrayContaining([emails[1]]))
    expect(response.status).toEqual(201);
  });

});

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
describe("Unblock user scenario", () => {

  it('should return 404-user-not-found for non-existing user', async function () {
    const nonexistentEmail= "nonexisting_test_email@test.com"
    // first try to delete the user from the database, just in case.
    await request(app).delete('/user/registration').query({email: nonexistentEmail })
    // attempt to delete a random email from this nonexisting user's sent-invitations list
    const response = await request(app).delete('/user/blocked').query({ email: nonexistentEmail, target_peer_email: emails[0] }).set('Accept', 'application/json')
    
    expect(response.body).toEqual({"response":"User not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 404-target-not-found for target not in blocklist', async function () {
    // attempt to unblock pam for tam if not currently in tam's blocklist
    const response = await request(app).delete('/user/blocked').query({ email: emails[4], target_peer_email: emails[5] }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response":"Target peer is not in blocklist."});
    expect(response.status).toEqual(404);
  });

  it('should successfully unblock a user', async function () {
    // jim has tam blocked
    const response = await request(app).delete('/user/blocked').query({ email: emails[3], target_peer_email: emails[4] }).set('Accept', 'application/json')
    const thisUser = await request(app).get('/user/profile').query({ email: emails[3] }).set('Accept', 'application/json')
    expect(thisUser.body.blocked).toEqual(expect.not.arrayContaining([emails[4]]))
    expect(response.status).toEqual(200);
  });

});