function getPanelNumber(){
	var src = document.getElementById('panel').src;
	return parseInt(src.substring(src.lastIndexOf('/')+1,src.lastIndexOf('.')));
}
function derp(){
	document.getElementById('derp').innerHTML += "derp";
}
function setPanel(panel){
	document.getElementById('panel').src = panel + ".gif";
	document.getElementById("jump").elements[0].value = panel;
    //document.getElementById("comictext").src = panel + ".html";
	document.getElementById("permalink").innerHTML = "mithrilnova.me/gsc/?panel="+getPanelNumber();
	document.getElementById("permalink").href = "?panel="+getPanelNumber();
}
function changePanel(offset){
	setPanel(getPanelNumber()+offset);
}
function jump(){
	setPanel(document.getElementById("jump").elements[0].value);
}
function init(){
	var url = window.location.href;
	var number = parseInt(url.substring(url.lastIndexOf('=')+1));
	setPanel(isNaN(number)?1:number);
}
function keyRespond(e){
	if(!e) e=window.event;
	if(e.keyCode == 37) changePanel(-1);
	else if (e.keyCode == 39) changePanel(1);
}
window.onload = init;
document.onkeydown = keyRespond;