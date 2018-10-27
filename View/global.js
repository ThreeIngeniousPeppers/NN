$(document).ready(function() {
    $.ajax({
        type: "GET",
        url: "out.csv",
        dataType: "text",
        success: function(data) {processData(data);}
     });
});

function processData(allText) {
    var record_num = 5;  // or however many elements there are in each row
    var allTextLines = allText.split(/\r\n|\n/);

    var resultArray = [];
    for (var i = 1; i < allTextLines.length; i++){
       var contentArray = allTextLines[i].split(',');
       var rowObject = {
           time: contentArray[0],
           actual: contentArray[1],
           predicted: contentArray[2]
       };
       resultArray.push(rowObject);
    }
    console.log(resultArray);
}