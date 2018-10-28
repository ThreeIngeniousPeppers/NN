var myResult;
var actualPoints,
    predictedPoints,
    times;

$(document).ready(function() {
    $.ajax({
        type: "GET",
        url: "out.csv",
        dataType: "text",
        success: function(data) {

            var parsedData = processData(data);
            myResult = parsedData;
            console.log(parsedData);
            times = Array.from(myResult, x=>x.time);
            actualPoints = Array.from(myResult, x=>x.actual);
            predictedPoints = Array.from(myResult, x=>x.predicted);

            renderChart();
        }
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
    return resultArray;
}

function renderChart() {
    // Set new default font family and font color to mimic Bootstrap's default styling
    Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
    Chart.defaults.global.defaultFontColor = '#292b2c';

    // Area Chart Example
    var ctx = document.getElementById("myAreaChart");

    var options = {
        scales: {
                  xAxes: [{
                    time: {
                      unit: 'date'
                    },
                    gridLines: {
                      display: false
                    },
                    ticks: {
                      maxTicksLimit: 7
                    }
                  }],
                  yAxes: [{
                    ticks: {
                      min: 6468.2996,
                      max: 6468.30075,
                      maxTicksLimit: 0.0000000001
                    },
                    gridLines: {
                      color: "rgba(0, 0, 0, .125)",
                    }
                  }]
        },
        legend: {
                  display: false
                }
    };

    var dataInLine = {
         labels: times,
         datasets: [{
           label: "Actual",
           lineTension: 0.3,
           backgroundColor: "rgba(2,117,216,0.2)",
           borderColor: "rgba(2,117,216,1)",
           pointRadius: 5,
           pointBackgroundColor: "rgba(2,117,216,1)",
           pointBorderColor: "rgba(255,255,255,0.8)",
           pointHoverRadius: 5,
           pointHoverBackgroundColor: "rgba(2,117,216,1)",
           pointHitRadius: 50,
           pointBorderWidth: 2,
           data: actualPoints,
         },
         {
            label: "Predicted",
            lineTension: 0.3,
            backgroundColor: "rgba(2,117,216,0.2)",
            borderColor: "rgba(216,24,2,1)",
            pointRadius: 5,
            pointBackgroundColor: "rgba(216,24,2,1)",
            pointBorderColor: "rgba(216,24,2,0.8)",
            pointHoverRadius: 5,
            pointHoverBackgroundColor: "rgba(2,117,216,1)",
            pointHitRadius: 50,
            pointBorderWidth: 2,
            data: predictedPoints,

                  }
         ],
       };


    var myLineChart = new Chart(ctx, {
      type: 'line',
      data: dataInLine,
      options: options,
    });


}