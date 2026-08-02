<?php
function form_value($name) {
    return trim($_POST[$name] ?? '');
}

function require_form_fields($fields) {
    foreach ($fields as $field => $label) {
        if (form_value($field) === '') {
            throw new InvalidArgumentException($label . ' is required.');
        }
    }
}

function require_pattern($value, $pattern, $message) {
    if (!preg_match($pattern, $value)) {
        throw new InvalidArgumentException($message);
    }
}

function require_date_value($value, $label) {
    require_pattern($value, '/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/', $label . ' must use YYYY-MM-DD format.');
    [$year, $month, $day] = array_map('intval', explode('-', $value));
    if (!checkdate($month, $day, $year)) {
        throw new InvalidArgumentException($label . ' must be a valid date.');
    }
}

function require_time_value($value, $label) {
    require_pattern($value, '/^[0-9]{2}:[0-9]{2}$/', $label . ' must use HH:MM format.');
    [$hour, $minute] = array_map('intval', explode(':', $value));
    if ($hour > 23 || $minute > 59) {
        throw new InvalidArgumentException($label . ' must be a valid time.');
    }
}

function require_min_integer($value, $minimum, $label) {
    if (!filter_var($value, FILTER_VALIDATE_INT) && $value !== '0') {
        throw new InvalidArgumentException($label . ' must be a whole number.');
    }
    if ((int) $value < $minimum) {
        throw new InvalidArgumentException($label . ' must be at least ' . $minimum . '.');
    }
}
?>
