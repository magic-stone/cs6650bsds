'use strict'
var mysql = require('mysql');
var config = require('./config.json');

// If 'client' variable doesn't exist
// if (typeof client === 'undefined') {
//   // Connect to the MySQL database
//   var client = mysql.createConnection({
//     host            : config.host,
//     user            : config.user,
//     password        : config.password,
//     database        : config.database
//   });
 
//   client.connect()
// }

var pool = mysql.createPool({
  host            : config.host,
  user            : config.user,
  password        : config.password,
  database        : config.database
});

exports.handler = (event, context, callback) => {
  context.callbackWaitsForEmptyEventLoop = false;
  let userID = event.pathParameters.userID;
  let day = event.pathParameters.day;
  let time = event.pathParameters.time;
  let count = event.pathParameters.stepCount;
  
  let curQuery = 'INSERT IGNORE INTO StepCount (UserID, DayIndex, TimeInterval, StepCount) VALUES (' + userID + ', ' + day + ', ' + time + ', ' + count + ')';
  
  pool.getConnection((err, con) => {
    if (err) throw err;
    con.query(curQuery, (err, res) => {
      con.release();
      if (err) throw err;
      let response = {
        statusCode: '200',
        body: 'insert success',
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Methods': '*',
          'Access-Control-Allow-Origin': '*'
        }
      };
      callback(null, response);
    });
  });
  // client.query(curQuery, function(err, rows, fields) {
  //   if (err) throw err;
  //   let response = {
  //     statusCode: '200',
  //     body: 'insert success',
  //     headers: {
  //       'Content-Type': 'application/json',
  //       'Access-Control-Allow-Methods': '*',
  //       'Access-Control-Allow-Origin': '*'
  //     }
  //   };
  //   callback(null, response);
  // });
//  client.query(curQuery, function (err, results, fields) {
//    count = results;
//    console.log("query result" + results);
//    if (err) throw err;
//  });

};