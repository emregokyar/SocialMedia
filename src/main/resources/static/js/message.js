// Message Area - Changing into File input
const fileInput = $(".file-input");
const fileButton = $(".file-btn");
const chatArea = $(".send-chat");

fileButton.click(function() {
    if (fileInput.hasClass("d-none")) {
        fileInput.removeClass("d-none");
        chatArea.addClass("d-none");
        chatArea.val("");
    } else {
        fileInput.addClass("d-none");
        chatArea.removeClass("d-none");
        fileInput.val("");
    }
});

//Second Part
var messageInput = $("#message-input");
var sendButton = $("#send-button");
var recipientId;
var stompClient = null;

$(function() {
    findAndDisplayConnectedUsers();
    connect();
});

//Connecting Web Socket
function connect(event) {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
    event.preventDefault();
}

var currentUserId = $('#current-user-id').val().trim();

function onConnected() {
    let str = String(currentUserId);
    stompClient.subscribe(`/user/${str}/messages`, function(message) {
        onMessageReceived(message)
    });
}

function onError(error) {
    console.error('WebSocket connection error:', error);
}

//Displaying Messaged Users
async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch('/messages/messagedUsers');
    let messagedUsers = await connectedUsersResponse.json();

    const userList = $(".scroll-list");
    userList.empty();

    messagedUsers.forEach(usr => {
        const profilePhotoPath = usr.photoPath || '/assets/person-fill.svg'; //PhotoPath
        const fullName = usr.fullName;
        const recipientId = usr.id;

        const $a = $(`
            <a href="#" class="list-group-item list-group-item-action py-3 lh-sm user-item" id="user-${recipientId}">
                <div class="d-flex w-100 align-items-center">
                    <img src="${profilePhotoPath}" alt="profile-picture" class="rounded-circle" height="40" width="40"/>
                    <p class="fw-bold mb-1 mx-2">${fullName}</p>
                </div>
            </a>
        `);

        userList.append($a);
    });
}

//when clicked on the user
var messagePart = $(".message-part");
$('.scroll-list').on('click', '.user-item', function() {
    messagePart.removeClass("d-none");
    recipientId = $(this).attr('id').split('-')[1];

    fetchAndDisplayUserChat();
});

//Displaying the messages
var chatList = $(".chat-list");

async function fetchAndDisplayUserChat() {
    chatList.empty();
    const userChatResponse = await fetch(`/messages/${recipientId}`);
    const userChat = await userChatResponse.json();

    userChat.forEach(chat => {
        if (chat.senderDTO.id === parseInt(recipientId)) {
            if (chat.type === 'TEXT') {
                var profilePhotoPath = chat.senderDTO.photoPath || '/assets/person-fill.svg';
                var messageCnt = chat.content;
                const $li = $(`
                    <li class="d-flex justify-content-start mb-4">
                        <img src="${profilePhotoPath}" alt="profile-picture" class="rounded-circle mt-1 mx-2" height="40" width="40"/>
                        <div class="card" style="max-width: 50%;">
                            <div class="card-body">
                                <p class="mb-0">
                                    ${messageCnt}
                                </p>
                            </div>
                        </div>
                    </li>
                `);
                chatList.append($li);
            } else {
                var profilePhotoPath = chat.senderDTO.photoPath || '/assets/person-fill.svg';
                var photo = chat.content;
                const $li = $(`
                    <li class="d-flex justify-content-start mb-4">
                        <img src="${profilePhotoPath}" alt="profile-picture" class="rounded-circle mt-1 mx-2 border"  height="40" width="40"/>
                        <img src="${photo}" alt="picture" class="rounded-3 mt-1 mx-2 object-fit-cover" height="500" width="500"/>
                    </li>
                `);
                chatList.append($li);
            }
        } else {
            if (chat.type === 'TEXT') {
                var messageCnt = chat.content;
                const $li = $(`
                    <li class="d-flex justify-content-end mb-4 mx-3">
                        <div class="card" style="max-width: 50%;">
                            <div class="card-body">
                                <p class="mb-0">
                                    ${messageCnt}
                                </p>
                            </div>
                        </div>
                    </li>
                `);
                chatList.append($li);
            } else {
                var photo = chat.content;
                const $li = $(`
                    <li class="d-flex justify-content-end mb-4 mx-2">
                        <img src="${photo}" alt="profile-picture" class="rounded-3 mt-1 mx-2 object-fit-cover" height="500" width="500"/>
                    </li>
                `);
                chatList.append($li);
            }
        }
    });

    $(".chat-area").scrollTop($(".chat-area")[0].scrollHeight);
}

//Sending Message
sendButton.on('click', (event) => sendMessage(event, recipientId));

function sendMessage(event, recipientId) {
    var messageContent = messageInput.val().trim();
    var pickedFile = fileInput[0].files[0];
    if (messageContent && stompClient) {
        var message = {
            content: messageContent,
            type: 'TEXT',
            receiverId: recipientId
        };

        stompClient.send(`/app/send`, {}, JSON.stringify(message));
        messageInput.val('');

        //Displaying the message
        if (message.type === 'TEXT') {
            var messageCnt = message.content;
            const $li = $(`
                <li class="d-flex justify-content-end mb-4 mx-3">
                    <div class="card" style="max-width: 50%;">
                        <div class="card-body">
                            <p class="mb-0">
                                ${messageCnt}
                            </p>
                        </div>
                    </div>
                </li>
            `);
            chatList.append($li);
        } else {
            var photoPath = message.photoPath;
            const $li = $(`
                <li class="d-flex justify-content-end mb-4 mx-2">
                    <img src="${photoPath}" alt="profile-picture" class="rounded-3 mt-1 mx-2 object-fit-cover" height="500" width="500"/>
                </li>
            `);
            chatList.append($li);
        }
    } else if (pickedFile && stompClient) {
        var uniqueNumber = Math.floor(new Date().valueOf() * Math.random());
        var formData = new FormData();
        formData.append("photo", pickedFile);
        formData.append("receiverId", recipientId);
        formData.append("content", uniqueNumber);

        fetch("/messages/uploadPhoto", {
                method: "POST",
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                //Web socket message
                var wsMessage = {
                    type: "PHOTO",
                    content: data.photoPath,
                    receiverId: recipientId,
                };
                stompClient.send("/app/send", {}, JSON.stringify(wsMessage));
                document.querySelector('.file-input').value = '';

                //Display the photo
                var photoPath = data.photoPath;
                const $li = $(`
                 <li class="d-flex justify-content-end mb-4 mx-2">
                     <img src="${photoPath}" alt="profile-picture" class="rounded-3 mt-1 mx-2 object-fit-cover" height="500" width="500"/>
                 </li>
             `);
                chatList.append($li);
            });
    }
    event.preventDefault();
}

//Receiving message
async function onMessageReceived(payload) {
    console.log('Message received', payload);
    await findAndDisplayConnectedUsers();
    const message = JSON.parse(payload.body);
    console.log("this is part that we can see..")
    console.log(message);

    //Displaying the message
    if ((parseInt(currentUserId) === message.receiverId) && (parseInt(recipientId) === message.senderId)) {
        if (message.type === 'TEXT') {
            var profilePhotoPath = message.picturePath || '/assets/person-fill.svg';
            var messageCnt = message.content;
            const $li = $(`
                    <li class="d-flex justify-content-start mb-4">
                        <img src="${profilePhotoPath}" alt="profile-picture" class="rounded-circle mt-1 mx-2 border" height="40" width="40"/>
                        <div class="card" style="max-width: 50%;">
                            <div class="card-body">
                                <p class="mb-0">
                                    ${$('<div>').text(messageCnt).html()}
                                </p>
                            </div>
                        </div>
                    </li>
                `);
            chatList.append($li);
        } else {
            var profilePhotoPath = message.picturePath || '/assets/person-fill.svg';
            var photoPath = message.content;
            const $li = $(`
                <li class="d-flex justify-content-start mb-4">
                    <img src="${profilePhotoPath}" alt="profile-picture" class="rounded-circle mt-1 mx-2" height="40" width="40"/>
                    <img src="${photoPath}" alt="profile-picture" class="rounded-3 mt-1 mx-2 object-fit-cover" height="500" width="500"/>
                </li>
            `);
            chatList.append($li);
        };
    };
}

//Displaying Searched Users
var searchButton = $(".search-user-button");
searchButton.on('click', searchUsersByName);

async function searchUsersByName(event) {
    event.preventDefault();

    var searchUserInput = $("#user-search").val().trim();

    const response = await fetch(`/messages/searchUser?input=${encodeURIComponent(searchUserInput)}`);
    let searchedUsers = await response.json();

    const userList = $(".scroll-list");
    userList.empty();

    searchedUsers.forEach(usr => {
        const profilePhotoPath = usr.photoPath || '/assets/person-fill.svg'; //PhotoPath
        const fullName = usr.fullName;
        const recipientId = usr.id;

        const $a = $(`
            <a href="#" class="list-group-item list-group-item-action py-3 lh-sm user-item" id="user-${recipientId}">
                <div class="d-flex w-100 align-items-center">
                    <img src="${profilePhotoPath}" alt="profile-picture" class="rounded-circle" height="40" width="40"/>
                    <p class="fw-bold mb-1 mx-2">${fullName}</p>
                </div>
            </a>
        `);
        userList.append($a);
    });
}