$("#accordion").accordion();
$('.operation').button();

function perform_operation(oname,operation,id) {
	$.post(
			'internal/jmx.html', 
			{ oname : oname , operation : operation }, 
			function(data) { $('#'+id).html(data); }
	);
}