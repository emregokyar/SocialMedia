document.querySelectorAll("button").forEach(element=>{
    element.addEventListener("mouseover", function(){
        element.style.backgroundColor="#0f3d83"
    });

    element.addEventListener("mouseout", function(){
        element.style.backgroundColor="white"
    })
});

document.querySelector("#create-btn").addEventListener("click", function () {
    document.querySelector("#sign-in").style.display ="none";
    document.querySelector("#create-account").style.display ="inline";
});

document.querySelector("#back-btn").addEventListener("click", function () {
    document.querySelector("#create-account").style.display ="none";
    document.querySelector("#sign-in").style.display ="inline";
});

document.querySelector("#reset-btn").addEventListener("click", function () {
    document.querySelector("#sign-in").style.display ="none";
    document.querySelector("#password-reset").style.display ="inline";
});

document.querySelector("#back-btn-2").addEventListener("click", function () {
    document.querySelector("#password-reset").style.display ="none";
    document.querySelector("#sign-in").style.display ="inline";
});