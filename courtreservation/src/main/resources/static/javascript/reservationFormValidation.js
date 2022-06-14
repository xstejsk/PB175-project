var blockedByDuration = false;
var blockedByStartDate = false;

const duration = document.querySelector('#duration')
const startDate = document.querySelector('#startDate')

duration.addEventListener('input', function () {
    if ($('#startHrs').val() == 17 && $('#duration').val() > 1) {
        $('#durationWarning').html('Reservation must end at 18:00 at the latest').css('color', 'red');
        document.getElementById("submitButton").disabled = true;
        blockedByDuration = true;

    }else {
        $('#durationWarning').html('');
        if (!blockedByStartDate){
            document.getElementById("submitButton").disabled = false;
        }

        blockedByDuration = false;
    }
});

startDate.addEventListener('input', function () {
    var date = new Date($('#startDate').val());
    if (date.getDay() == 6 || date.getDay() == 0) {
        $('#dateWarning').html('Available only on workdays').css('color', 'red');
        document.getElementById("submitButton").disabled = true;
        blockedByStartDate = true;
    }else {
        $('#dateWarning').html('');
        if (!blockedByDuration) {
            document.getElementById("submitButton").disabled = false;
        }
        blockedByStartDate = false;
    }
});

