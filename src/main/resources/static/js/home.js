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

document.addEventListener("DOMContentLoaded", function () {
    // Liking Photo with CSRF
    document.querySelectorAll(".like-photo-form").forEach(form => {
        form.addEventListener("submit", function (e) {
            e.preventDefault();

            const formData = new FormData(form);
            const photoId = formData.get("photoId");

            fetch("/likePhoto?photoId=" + photoId, {
                method: "POST"
            }).then(() => {
                 const button = document.getElementById("like-photo-button-" + photoId);
                    const img = button.querySelector("img");

                    // Check current image src to determine like state
                    const isLiked = img.src.includes("heart-fill.svg");

                    // Toggle the image
                    if (isLiked) {
                        img.src = "/assets/heart.svg";
                    } else {
                        img.src = "/assets/heart-fill.svg";
                    }
            });
        });
    });

    // Adding Comment with CSRF
    document.querySelectorAll(".comment-form").forEach(form => {
        form.addEventListener("submit", function (e) {
            e.preventDefault();
            const formData = new FormData(form);
            const photoId = form.getAttribute("data-photo-id");

            fetch("/commentOnPost?photoId=" + photoId, {
                method: "POST",
                body: formData
            }).then(() => location.reload());
        });
    });

    // Deleting Comment with CSRF
    document.querySelectorAll(".delete-comment").forEach(form => {
        form.addEventListener("submit", function (e) {
            e.preventDefault();
            const formData = new FormData(form);
            const commentId = formData.get("commentId");

            fetch("/deleteUserComment?commentId=" + commentId, {
                method: "POST"
            }).then(() => {
                const commentDiv = document.querySelector(`[data-comment-id="${commentId}"]`);
                if (commentDiv) {
                    commentDiv.remove();
                }
            });
        });
    });
});
