const request = require('supertest');
const express = require('express');
var bodyParser = require('body-parser')
const app = express()
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))
// reuse original application?
const roomsRouter = require('../chat/room');
app.use('/chat/room', roomsRouter)
const mssgRouter = require('../chat/message');
app.use('/chat/message', mssgRouter)
const user_profile_router = require('../user/profile.js');
app.use('*', user_profile_router);
app.use('/user', user_profile_router)
const user_peers_router = require('../user/peers.js');
app.use('*', user_peers_router);
app.use('/user', user_peers_router)

const invalidRoomId = "clearlyfakeid"
const fakeRoomId = "62e2feb74ce5451dd12322a4"
const nonexistingUser = "nonexistingchatuser@test.com"
const testUserA = {
  name: "Alice",
  email: "alice@test.com"
}
const testUserB = {
  name: "Bob",
  email: "bob@test.com"
}
const testUserC = {
  name: "Cathy",
  email: "cathy@test.com"
}
const testMessageA = {email: testUserA.email, text: "sup!", time: "3 PM"}
const testRoom = {name: "Alice/Bob", peerslist: [testUserA.email, testUserB.email], chathistory: [] }
const testRoom_2 = {name: "Alice/Cathy", peerslist: [testUserA.email, testUserC.email], chathistory: [] }
var testRoomId;
var testRoomId_2;

beforeAll(() => {
  resetDatabase();
  return initializeDatabase()
});

afterAll(() => {
  // return resetDatabase()

})

async function initializeDatabase() {
  try {
    // register three users
    await request(app).post('/user/registration').query({ name: testUserA.name, email: testUserA.email})
    await request(app).post('/user/registration').query({ name: testUserB.name, email: testUserB.email})
    await request(app).post('/user/registration').query({ name: testUserC.name, email: testUserC.email})
    // make A and B peers, and A and C peers
    await request(app).post('/user/invitations').query({ email: testUserA.email, target_peer_email: testUserB.email})
    await request(app).post('/user/invitations').query({ email: testUserB.email, target_peer_email: testUserA.email})
    await request(app).post('/user/invitations').query({ email: testUserA.email, target_peer_email: testUserC.email})
    await request(app).post('/user/invitations').query({ email: testUserC.email, target_peer_email: testUserA.email})
    // create two chatrooms, one with A/C and one with A/B
    var room = await request(app).post('/chat/room').set('Accept', 'application/json').send( testRoom )
    var room_2 = await request(app).post('/chat/room').set('Accept', 'application/json').send( testRoom_2 )
    testRoomId = room.body.insertedId
    testRoomId_2 = room_2.body.insertedId
    
  } catch (err) {
    console.log(err)
  }
}
async function resetDatabase() {
  try {
    await request(app).delete('/chat/room').query({ room_id: testUserA.email })
    await request(app).delete('/chat/room').query({ room_id: testUserB.email })
    await request(app).delete('/chat/room').query({ room_id: testUserC.email })
    
  } catch (err) {
    console.log(err)
  }
}


describe("Send message scenario", () => {

    it('should return 404 for nonexisting room', async function () {
      // first try to delete the room, just in case
      await request(app).delete('/room').query({ room_id: fakeRoomId })
      
      const response = await request(app).post('/chat/message').query({ room_id: fakeRoomId }).set('Accept', 'application/json').send( testMessageA )
      expect(response.body).toEqual({"response": "Room not found."});
      expect(response.status).toEqual(404);
    });
  
    it('should return 400 for invalid room id', async function () {
        
        const response = await request(app).post('/chat/message').query({ room_id: invalidRoomId }).set('Accept', 'application/json').send( testMessageA )
        expect(response.body).toEqual({"response": "Invalid room id."});
        expect(response.status).toEqual(400);
    });
  
    it('should return 400 for missing fields', async function () {
      const missingFieldsMessage = {text: "there's no email or time!"}
      const response = await request(app).post('/chat/message').query({ room_id: testRoomId }).set('Accept', 'application/json').send( missingFieldsMessage )
      expect(response.body).toEqual({"response": "Missing fields."});
      expect(response.status).toEqual(400);
    });

    it('should successfully post a message', async function () {
      // posts to room 2
      const response = await request(app).post('/chat/message').query({ room_id: testRoomId_2 }).set('Accept', 'application/json').send( testMessageA )
      expect(response.body).toEqual({ "response": "Message successfully posted." });
      expect(response.status).toEqual(201);
    });
  
});

describe("Get all rooms scenario", () => {

  it('should return 404 for nonexisting user', async function () {
    // first try to delete the user, just in case
    await request(app).delete('/user/registration').query({ room_id: nonexistingUser })
    
    const response = await request(app).get('/chat/room/all').query({ email: nonexistingUser }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response": "User not found."});
    expect(response.status).toEqual(404);
  });

  it('should successfully return all rooms containing a user', async function () {
    const response = await request(app).get('/chat/room/all').query({ email: testUserA.email }).set('Accept', 'application/json')
    console.log(response.body)
    expect(response.body.length).toEqual(2);
    expect(response.body[0].name).toEqual("Alice/Bob");
    expect(response.body[1].name).toEqual("Alice/Cathy");
    expect(response.body[0]._id).toEqual(testRoomId);
    expect(response.body[1]._id).toEqual(testRoomId_2);
    expect(response.status).toEqual(200);
  });

});

describe("Get chatroom history scenario", () => {

  it('should return 404 for nonexisting room', async function () {
    // first try to delete the room, just in case
    await request(app).delete('/room').query({ room_id: fakeRoomId })
    
    const response = await request(app).get('/chat/room/history').query({ room_id: fakeRoomId }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response": "Room not found."});
    expect(response.status).toEqual(404);
  });

  it('should get message history of an existing room', async function () {
    // first post three messages to the Alice/Bob room
    var newMssg1 = {email: testUserA.email, text: "hey, this is Alice!", time: "3:05"}
    var newMssg2 = {email: testUserA.email, text: "how are you, Bob?", time: "3:06 PM"}
    var newMssg3 = {email: testUserB.email, text: "hi Alice, I'm doing great!", time: "3:07 PM"}
    await request(app).post('/chat/message')
      .query({ room_id: testRoomId }).set('Accept', 'application/json')
      .send( newMssg1 )
    await request(app).post('/chat/message')
      .query({ room_id: testRoomId }).set('Accept', 'application/json')
      .send( newMssg2 )
    await request(app).post('/chat/message')
      .query({ room_id: testRoomId }).set('Accept', 'application/json')
      .send( newMssg3 )
    
    var response = await request(app).get('/chat/room/history').query({room_id: testRoomId})
    expect(response.body.length).toEqual(3);
    expect(response.body[0].email).toEqual(newMssg1.email)
    expect(response.body[1].email).toEqual(newMssg2.email)
    expect(response.body[2].email).toEqual(newMssg3.email)
    expect(response.body[0].text).toEqual(newMssg1.text)
    expect(response.body[1].text).toEqual(newMssg2.text)
    expect(response.body[2].text).toEqual(newMssg3.text)
    expect(response.body[0].time).toEqual(newMssg1.time)
    expect(response.body[1].time).toEqual(newMssg2.time)
    expect(response.body[2].time).toEqual(newMssg3.time)
    expect(response.status).toEqual(200);
  });

});

// describe("Post new room scenario", () => {

//   it('should return 404 for nonexisting room', async function () {
//     // first try to delete the room, just in case
//     await request(app).delete('/room').query({ room_id: fakeRoomId })
    
//     const response = await request(app).get('/chat/room/history').query({ room_id: fakeRoomId }).set('Accept', 'application/json')
//     expect(response.body).toEqual({"response": "Room not found."});
//     expect(response.status).toEqual(404);
//   });

//   it('should get message history of an existing room', async function () {
//     // first post three messages to the Alice/Bob room
//     var newMssg1 = {email: testUserA.email, text: "hey, this is Alice!", time: "3:05"}
//     var newMssg2 = {email: testUserA.email, text: "how are you, Bob?", time: "3:06 PM"}
//     var newMssg3 = {email: testUserB.email, text: "hi Alice, I'm doing great!", time: "3:07 PM"}
//     await request(app).post('/chat/message')
//       .query({ room_id: testRoomId }).set('Accept', 'application/json')
//       .send( newMssg1 )
//     await request(app).post('/chat/message')
//       .query({ room_id: testRoomId }).set('Accept', 'application/json')
//       .send( newMssg2 )
//     await request(app).post('/chat/message')
//       .query({ room_id: testRoomId }).set('Accept', 'application/json')
//       .send( newMssg3 )
    
//     var response = await request(app).get('/chat/room/history').query({room_id: testRoomId})
//     expect(response.body.length).toEqual(3);
//     expect(response.body[0].email).toEqual(newMssg1.email)
//     expect(response.body[1].email).toEqual(newMssg2.email)
//     expect(response.body[2].email).toEqual(newMssg3.email)
//     expect(response.body[0].text).toEqual(newMssg1.text)
//     expect(response.body[1].text).toEqual(newMssg2.text)
//     expect(response.body[2].text).toEqual(newMssg3.text)
//     expect(response.body[0].time).toEqual(newMssg1.time)
//     expect(response.body[1].time).toEqual(newMssg2.time)
//     expect(response.body[2].time).toEqual(newMssg3.time)
//     expect(response.status).toEqual(200);
//   });

// });