<?php
$conn = mysqli_connect("localhost","root","","livestreamapp_db");
if(mysqli_connect_errno()){
    echo "". mysqli_connect_error();
    exit();
    
}

$sql = "SELECT * FROM users";
$rows = mysqli_fetch_array(mysqli_query($conn,$sql), 1);

$resp = array();
$resp['code'] = "200";
$resp["msg"] = "success";
$resp["response"] = $rows;

echo json_encode($resp);



?>