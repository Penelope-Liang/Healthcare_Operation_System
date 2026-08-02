<?php
function json_response($payload, $statusCode = 200) {
    http_response_code($statusCode);
    header('Content-Type: application/json');
    echo json_encode($payload);
    exit;
}

function json_request_body() {
    $rawBody = file_get_contents('php://input');
    $data = json_decode($rawBody, true);
    if (!is_array($data)) {
        json_response(['error' => 'Request body must be valid JSON.'], 400);
    }
    return $data;
}
?>
