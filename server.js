var express = require('express');
var app = express();

var path = require('path');

app.use(express.static('View'));

// viewed at http://localhost:8080
app.get('/', function(req, res) {
    res.sendFile(path.join(__dirname + '/View/test.html'));
});

app.listen(3000, function () {
  console.log('Example app listening on port 3000!');
});
