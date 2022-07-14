const request = require('supertest');
const assert = require('assert');
const express = require('express');

const app = express();

app.get('/user', function(req, res) {
  res.status(200).json({ name: 'john' });
});

app.get('/user_another', function(req, res) {
  res.status(200).json({ name: 'bob' });
});


module.exports = app;