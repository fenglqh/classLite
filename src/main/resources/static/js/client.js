// 实现标签页的切换
function switchTab() {
    // 1. 先获取到相关元素（会话图标，好友图标，会话列表，好友列表）
    let tabSession = document.querySelector(".main .left .tab .tab-session")
    let tabFriend = document.querySelector(".main .left .tab .tab-friend");
    // [0] 表示会话列表 [1]表示好友列表
    let lists = document.querySelectorAll(".main .left .list");
    // 2. 针对标签按钮，注册点击事件
    //     点击会话列表，会话图标选中，好友图标未选中，
    // 会话列表显示，好友列表隐藏
    tabSession.onclick = function() {
        tabSession.style.backgroundImage = 'url("img/当前会话2.png")';
        tabFriend.style.backgroundImage = 'url("img/用户.png")';
        lists[0].classList = 'list';
        lists[1].classList = 'list hidden';
    }

    tabFriend.onclick = function() {
        tabSession.style.backgroundImage = 'url("img/当前会话.png")';
        tabFriend.style.backgroundImage = 'url("img/用户2.png")';
        lists[1].classList = 'list';
        lists[0].classList = 'list hidden';
    }
     
}

switchTab();


// 获取用户名
function getUserInfo() {
    $.ajax({
        url : "/userinfo",
        type: "post",
        success : function(result) {
            // 校验条件，userId是否为0
            // 如果userId为0， 则跳转到登录页面
            // 如果非0，则显示内容到页面上，并且往html元素中存入userId以备后用
            if (result && result.userId > 0) {
                let username = document.querySelector(".main .left .user");
                username.innerHTML = result.userName;
                username.setAttribute("user-id", result.userId);
            } else {
                alert("请先登录！");
                location.assign("/login.html");
            }
        }
    })
}

getUserInfo();

// 获取好友列表
function getFriendList() {
    $.ajax({
        url: '/getFriendList',
        type: 'get',
        success: function(body) {
            // 1. 先把之前的好友列表的内容都清空，防止多次重复触发请求
            let friendList = document.querySelector("#list-friend");
            friendList.innerHTML = '';
            // 2. 遍历返回的数组，将friendName显示
            // 并将friendId 作为html的自定义属性保存，已备后用
            for (let friend of body) {
                let li = document.createElement('li');
                li.innerHTML = '<h4>'+ friend.friendName+'</h4>'
                li.setAttribute("friend-id",body.friendId);
                friendList.appendChild(li);
                // 给每个好友注册一个点击事件
                li.onclick = function() {
                    clickFriend(friend);
                }
            }
        },
        error: function() {
            console.error("获取好友列表失败!");
            location.assign("/login.html");
        }
        
    })
}
getFriendList();

// 获取会话列表
function getSessionList() {
    $.ajax({
        url: "/getSessionList",
        type: "get",
        success: function(body) {
            if (body) {
                // 1. 清空之前的所有会话
                let sessionList = document.querySelector("#session-list");
                sessionList.innerHTML = '';
                // 2. 遍历会话列表并显示
                for (let session of body) {
                    let li = document.createElement("li");
                    // 将sessionId作为html的自定义属性进行存储
                    li.setAttribute("sessionId", session.sessionId);
                    // 对于message做一个截断
                    if (session.lastMessage && session.lastMessage.length > 10) {
                        session.lastMessage = session.lastMessage.substring(0,10) + "...";
                    }
                    li.innerHTML = '<h3>'+session.friends[0].friendName+'</h3>' + '<p>'+session.lastMessage+'</p>';
                    sessionList.appendChild(li);

                    // 给li标签注册一个点击事件
                    li.onclick = function() {
                        clickSession(li);
                    }
                }
            }
        }
    })
}
getSessionList();


function clickSession(current_li){
    // 1. 设置高亮
    let all_li = document.querySelectorAll("#session-list>li");
    activeSession(all_li, current_li);
    // 2. 更改右侧标题，获取历史会话消息
    let sessionId = current_li.getAttribute("sessionId");
    getHistoryMessage(sessionId);
};


function activeSession(all_li, current_li) {
    for (let li of all_li) {
        if (li == current_li) {
            current_li.className = "selected";
        } else {
            li.className = '';
        }
    }
}

function getHistoryMessage(sessionId){
    // 1. 清空右侧标题，和消息数据
    let title = document.querySelector(".main .right .title");
    let messageList = document.querySelector(".main .right .message-show");
    title.innerHTML = '';
    messageList.innerHTML = '';
    // 2. 利用被选中会话，重新设置标题
    let selectedH3 = document.querySelector(".main .left .list .selected>h3");
    title.innerHTML = selectedH3.innerHTML;
    // 3. 重新获取历史消息
    messageList.innerHTML = '';
    $.ajax({
        url: "/getHistoryMessage",
        type: "get",
        data: {
            sessionId:sessionId,
        },
        success: function(body) {
            if (!body) return;
            // 获取当前用户id
            let username = document.querySelector(".main .left .user");
            let userId = username.getAttribute("user-id");
            for (let message of body) {
                // formId 和登录者相同则局右
                let div = document.createElement("div");
                if (userId == message.fromId) {
                    div.className = "message message-right";
                } else {
                    div.className = "message message-left";
                }
                // 显示fromName和content
                div.innerHTML = '<div class="box">'+'<h4>'+message.fromName+'</h4>'+'<p>'+message.content+'</p>'+'</div>';

                // messageId 作为自定义属性存储
                div.setAttribute("message-id",message.messageId);
                messageList.appendChild(div);
            }

            // 4. 控制滚动条滚动到最下面
            scrollBottom(messageList);
        }
    })
}

// 控制滚动条,滚动到容器的最下面
function scrollBottom(messageList) {
    // 1. 获取可视区域的高度
    let clientHeight = messageList.offsetHeight;
    // 2. 获取内容物的高度
    let scrollHeight = messageList.scrollHeight;
    // 3. 控制滚动条滚动，第一个参数是水平滚动距离，第二个是垂直滚动距离
    messageList.scrollTo(0, scrollHeight - clientHeight);
}

//  点击好友创建会话
function clickFriend(friend) {
    // 1. 查询会话是否存在
    let sessionLi = findSessionByName(friend.friendName);
    let all_li = document.querySelector("#session-list");
    // 2. 如果会话存在，这将这个会话选中并置顶和查询历史消息
    if (sessionLi) {
        all_li.insertBefore(sessionLi, all_li.children[0]);
        sessionLi.click();
    } else {
    // 3. 如果会话不存在，创建一个新会话
        sessionLi = document.createElement("li");
        sessionLi.innerHTML = '<h3>'+friend.friendName+'</h3>'+'<p></p>'
        // 置顶
        all_li.insertBefore(sessionLi, all_li.children[0]);
        // 注册点击事件
        sessionLi.onclick = function() {
            clickSession(sessionLi);
        }
        sessionLi.click();
        // 将新创建的会话告诉后端
        createSession(friend.friendId, sessionLi);
       
    }
    // 4. 切换标签页到会话部分
    let tabSession = document.querySelector(".main .left .tab .tab-session")
    tabSession.click();

}

function findSessionByName(friendName) {
    // 遍历会话列表，看看会话的标题部分是否和friend名字一样
    let sessionList = document.querySelectorAll("#session-list>li");
    for (let session of sessionList) {
        if (session.querySelector("h3").innerHTML == friendName) {
            return session;
        }
    }
    return null;
}

//  创建新会话
function createSession(toUserId, sessionLi) {
    // 发送ajax请求创建新的session并获取sessionId
    $.ajax({
        url : "createSession?toUserId="+toUserId,
        type : "post",
        success : function(body) {
    // 将sessionId作为html的自定义属性存储
            console.log("创建新会话成功！");
            sessionLi.setAttribute("sessionId", body.sessionId);
        },
        error : function() {
            console.log("创建新会话失败！");
        }
    })

}

// ----------------------------------------------------------
// ------------操纵websocket实现消息传输-----------------------
// ----------------------------------------------------------
let webSocket = new WebSocket("ws:127.0.0.1:8080/message");
webSocket.onopen = function() {
    console.log("websocket 连接建立成功！");
}

webSocket.onmessage = function(e) {
    console.log("websocket  服务器发送来的消息是:"+e.data);
    // 将json格式的e.data转化为js对象
    let res = JSON.parse(e.data);
    if (res.type == 'message') {
        // 处理响应
        handleMessage(res);
    } else {
        console.log("websocket 类型不对！");
    }
}

webSocket.onerror = function() {
    console.log("websocket 连接异常！");
}

webSocket.onclose = function() {
    console.log("websocket 连接关闭成功！");
}

function handleMessage(res) {
    // 展示服务器发送过来的消息
    // 展示到会话预览区，以及消息列表中

    // 1. 根据响应中的sessionId 获取到当前会话对应的li标签
    //      如果li标签不存在，则新建一个
    let acceptSessionId = res.sessionId;
    let sessionList = document.querySelector("#session-list");
    let sessionLis = document.querySelectorAll("#session-list li");
    let sessionLi = null;
    for (let Li of sessionLis) {
        if (acceptSessionId == Li.getAttribute("sessionId")) {
            sessionLi = Li;
            break;
        }
    }

    // 2. 将收到的content展示在li标签的p标签中，如果消息过长要进行截断
    if (res.content && res.content.length > 10) {
        res.content = res.content.substring(0,10) +'...';
    }
    if (sessionLi == null) {
        sessionLi = document.createElement("li");
        sessionLi.innerHTML = '<h3>'+res.fromName+'</h3>'+'<p>'+res.content+'</p>';
        sessionList.appendChild(sessionLi);
        //  注册点击事件
        sessionLi.onclick = function() {
            clickSession(sessionLi);
        }
    } else {
        sessionLi.querySelector("p").innerHTML = res.content;
    }
    // 3. 将收到消息的会话置顶
    sessionList.insertBefore(sessionLi, sessionList.children[0]);
    // 4. 如果当前收到的会话被选中，则展示到消息列表中
    //      移动滚动条
    if (!(sessionLi.className == "selected")) return;


    let username = document.querySelector(".main .left .user");
    let userId = username.getAttribute("user-id");
    let messageList = document.querySelector(".right .message-show");
    let div = document.createElement('div');
    div.innerHTML = '<div class="box">'+'<h4>'+res.fromName+'</h4>'+
    '<p>'+res.content+'</p></div>';
    if (userId == res.fromId) {
        div.className = "message message-right";
    } else {
        div.className = "message message-left";
    }
    messageList.appendChild(div);
    // 滚动条移动
    scrollBottom(messageList);
}
// ----------------------------------------------------------
// ------------发送消息---------------------------------------
// ----------------------------------------------------------

function initSubmitButton() {
    // 1. 获取编辑框和点击按钮
    let messageInput = document.querySelector(".right .text-input");
    let messageButton = document.querySelector(".right .ctrl>button");
    // 2. 给按钮注册点击事件
    messageButton.onclick = function() {
        // 获取消息，如果消息为空则什么也不做
        if (!messageInput.value) {
            return;
        }
        // 获取sessionId 如果没有被选中的会话则什么也不干
        let sessionLi = document.querySelector(".left .list .selected");
        if (!sessionLi) {
            return;
        }
        let sessionId = sessionLi.getAttribute("sessionId");
        // 构造发送数据
        let send_message = {
            type: "message", // 对于数据做个标识
            sessionId: sessionId,
            content: messageInput.value
        };
        // 发送数据，但是参数只能是string类型，所以要进行转换
        send_message = JSON.stringify(send_message);
        webSocket.send(send_message);
        // 3. 清空输入框
        messageInput.value = '';
    }
}
initSubmitButton();