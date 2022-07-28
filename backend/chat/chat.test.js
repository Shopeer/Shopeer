const request = require('supertest');
const express = require('express');
const app = express()
// reuse original application?
const roomsRouter = require('../chat/room');
app.use('/chat', roomsRouter)

const mssgRouter = require('../chat/message');
app.use('/chat', mssgRouter)

describe("Send message scenario", () => {
    const invalidRoomId = "clearlyfakeid"
    const fakeRoomId = "62e2feb74ce5451dd12322a4"
    const testMessage = {"email": "test@mail.com", "text": "sup!", "time": "3 PM"}

    it('should return 404 for nonexisting room', async function () {
      // first try to delete the room, just in case
      await request(app).delete('/room').query({ room_id: fakeRoomId })
      
      const response = await request(app).post('/message').query({ room_id: fakeRoomId }).send(testMessage)
      
      expect(response.body).toEqual({"response": "Room not found."});
      expect(response.status).toEqual(404);
    });
  
    it('should return 400 for invalid room id', async function () {
        
        const response = await request(app).post('/message').query({ room_id: invalidRoomId }).send(testMessage)
        expect(response.status).toEqual(400);
    });
  
    // it('should return an empty peerlist successfully', async function () {
    //   // attempt to get Jim's peerlist
    //   const response = await request(app).get('/user/peers').query({ email: emails[3] }).set('Accept', 'application/json')
      
    //   expect(response.body.length).toEqual(0);
    //   expect(response.status).toEqual(200);
    // });
  
  });