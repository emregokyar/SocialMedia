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
