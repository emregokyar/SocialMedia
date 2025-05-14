document.querySelectorAll("button").forEach(element=>{
    element.addEventListener("mouseover", function(){
        element.style.backgroundColor="#0f3d83"
    });

    element.addEventListener("mouseout", function(){
        element.style.backgroundColor="white"
    })
});

document.querySelectorAll("#reset-btn-settings").forEach(element=>{
    element.addEventListener("mouseover", function(){
        element.style.backgroundColor="#0f3d83"
    });

    element.addEventListener("mouseout", function(){
        element.style.backgroundColor="white"
    })
});

document.querySelector("#create-btn").addEventListener("click", function () {
    document.querySelector(".sign-in").style.display ="none";
    document.querySelector(".create-account").style.display ="block";
});

document.querySelector("#back-btn").addEventListener("click", function () {
    document.querySelector(".create-account").style.display ="none";
    document.querySelector(".sign-in").style.display ="block";
});

document.querySelector("#reset-btn").addEventListener("click", function () {
    document.querySelector(".sign-in").style.display ="none";
    document.querySelector(".password-reset").style.display ="block";
});

document.querySelector("#back-btn-2").addEventListener("click", function () {
    document.querySelector(".password-reset").style.display ="none";
    document.querySelector(".sign-in").style.display ="block";
});


const preview = document.querySelector("#preview");

$("#fileInput").change(function() {
    const file = this.files[0];
    if (file) {
    const fileReader = new FileReader();

    $("#photo-area").addClass("d-none");
    $("#preview").removeClass("d-none");

    fileReader.onload = function(e) {
      preview.src = e.target.result;
    };
    fileReader.readAsDataURL(file);
    }
});

$("#cancel-button").click(function () {
    $("#photo-area").removeClass("d-none");
    $("#preview").addClass("d-none").attr("src", "");
    $("#fileInput").val("");
});


$("#upload-button").hover(function(){
    $(this).css("background-color", "#0f3d83");
});

$("#cancel-button").hover(function(){
    $(this).css("background-color", "#0f3d83");
    $(this).css("color", "white");
});

$("#cancel-button").mouseout(function(){
  $(this).css("background-color", "white");
  $(this).css("color", "#0f3d83");
});