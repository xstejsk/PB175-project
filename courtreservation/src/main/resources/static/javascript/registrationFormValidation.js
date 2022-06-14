const password = document.querySelector('#password');
const confirmPassword = document.querySelector('#confirmPassword');

function validate() {
    if ($('#password').val() == $('#confirmPassword').val()) {
        $('#message').html('Password matches').css('color', 'green');
    } else
        $('#message').html('Password does not match').css('color', 'red');
}

password.addEventListener('input', validate);
confirmPassword.addEventListener('input', validate);