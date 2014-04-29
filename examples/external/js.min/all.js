console.log("Except me. I do't care about order");console.log("I should be loaded first. You all depend on me");function addDiv(text){$(document).ready(function(){var cont="\x3cdiv\x3e"+text+"\x3c/div\x3e";$("body").append(cont)})};console.log("o2");addDiv("o2");console.log("o1");addDiv("o1");console.log("o3");addDiv("o3");console.log("o4");addDiv("o4");console.log("o5");addDiv("o5");
/** 
* @ignore
**/