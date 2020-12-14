const sign_up_ghost_btn = document.getElementById("sign_up_ghost");
const sign_in_ghost_btn = document.getElementById("sign_in_ghost");
const sign_in_btn = document.getElementById("sign_in");
const sign_up_btn = document.getElementById("sign_up");
const container = document.getElementById("container");

if (sign_up_ghost_btn) {
  sign_up_ghost_btn.addEventListener("click", () => {
    container.classList.add("right-panel-active");
  });
}

if (sign_in_ghost_btn) {
  sign_in_ghost_btn.addEventListener("click", () => {
    container.classList.remove("right-panel-active");
  });
}

if (sign_up_btn) {
  sign_up_btn.addEventListener("click", () => {
    console.log("test");
    let data = new FormData();
    console.log(document.getElementById("sign_up_id").value);
    data.append("member_name", document.getElementById("sign_up_name").value);
    data.append("member_id", document.getElementById("sign_up_id").value);
    data.append("member_pwd", document.getElementById("sign_up_pwd").value);
    console.log("data : ", data);

    $.ajax({
      type: "POST",
      url: "/sign_up",
      enctype: "multipart/form-data",
      data: data,
      contentType: false,
      processData: false,
      success: function (data) {
        console.log("data : ", data);
        alert("회원가입에 성공했습니다");
      },
      error: function (error) {
        console.log("ERROR : ", error);
        alert(error.responseText);
      },
    });
  });
}

if (sign_in_btn) {
  sign_in_btn.addEventListener("click", () => {
    let data = new FormData();
    data.append("member_id", document.getElementById("sign_in_id").value);
    data.append("member_pwd", document.getElementById("sign_in_pwd").value);
    console.log("data : ", data);

    $.ajax({
      type: "POST",
      url: "/sign_in",
      enctype: "multipart/form-data",
      data: data,
      contentType: false,
      processData: false,
      success: function (data) {
        window.location = "/";
      },
      error: function (e) {
        alert(e.responseText);
      },
    });
  });
}
