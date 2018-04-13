$(function(){
    // Handle login
    $('#createTransBtn').click(function(){
    	var code = $('#transcode').val().trim(),
    		to = $('#toAccountNum').val().trim(),
    		amount = $('#amount').val().trim(),
    		hasError = false;
    	if (!code) {
    		$('#input-group-transcode').addClass('has-error');
    		hasError = true;
    	} else {
    		$('#input-group-transcode').removeClass('has-error');
    	}
    	if (!to) {
    		$('#input-group-toAccount').addClass('has-error');
    		hasError = true;
    	} else {
    		$('#input-group-toAccount').removeClass('has-error');
    	}
    	if (!amount) {
    		$('#input-group-amount').addClass('has-error');
    		hasError = true;
    	} else {
    		$('#input-group-amount').removeClass('has-error');
    	}
    	if (hasError) {
    		$('messageBox').html('<p class="text-danger">All fields are mandatory.</p>');
    		$('#messageBox').removeClass('hidden');
    		return false;
    	} else {
    		$('#messageBox').addClass('hidden');
    		$('#createTransBtn').submit();    		
    	}
    });
});