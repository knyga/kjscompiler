/**
 * @depends {jquery-2.0.3.min.js}
 */

console.log('I should be loaded first. You all depend on me');
function addDiv(text) {
	$(document).ready(function() {
		var cont = '<div>' + text + '</div>';
		$('body').append(cont);
	})
}