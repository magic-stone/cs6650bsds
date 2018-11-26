'use strict'
var mysql = require('mysql');
var config = require('./config.json');

// If 'client' variable doesn't exist
if (typeof client === 'undefined') {
  // Connect to the MySQL database
  var client = mysql.createConnection({
    host            : config.host,
    user            : config.user,
    password        : config.password,
    database        : config.database
  });
 
  client.connect()
}

// var pool = mysql.createPool({
//   host            : config.host,
//   user            : config.user,
//   password        : config.password,
//   database        : config.database
// });

exports.handler = (event, context, callback) => {
  context.callbackWaitsForEmptyEventLoop = false;
  let userID = event.pathParameters.userID;
  let day = event.pathParameters.day;
  let count = undefined;
  let curQuery = 'SELECT SUM(StepCount) AS StepCount FROM StepCount Where UserID = ' + userID + ' AND DayIndex = ' + day;
  
  client.query(curQuery, function(err, rows, fields) {
    if (err) throw err;
    count = rows[0].StepCount;
    let response = {
      statusCode: '200',
      body: count,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Methods': '*',
        'Access-Control-Allow-Origin': '*'
      }
    };
    callback(null, response);
  });
//  client.query(curQuery, function (err, results, fields) {
//    count = results;
//    console.log("query result" + results);
//    if (err) throw err;
//  });

};