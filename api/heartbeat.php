<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Only POST allowed']);
    exit;
}

$input = file_get_contents('php://input');
$data = json_decode($input, true);

if (!$data || empty($data['plugin']) || empty($data['server_id'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Missing plugin or server_id']);
    exit;
}

$dbFile = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'data' . DIRECTORY_SEPARATOR . 'servers.json';
if (!is_dir(dirname($dbFile))) {
    mkdir(dirname($dbFile), 0777, true);
}

$servers = [];
if (is_file($dbFile)) {
    $content = file_get_contents($dbFile);
    $parsed = json_decode($content, true);
    if (isset($parsed['servers']) && is_array($parsed['servers'])) {
        $servers = $parsed['servers'];
    }
}

$now = time();
$pluginName = preg_replace('/[^a-zA-Z0-9_\-\.]/', '', (string)$data['plugin']);
$serverId = substr(hash('sha256', (string)$data['server_id']), 0, 32);
$key = $pluginName . '@' . $serverId;

$servers[$key] = [
    'plugin' => $pluginName,
    'serverId' => $serverId,
    'players' => isset($data['players']) ? max(0, (int)$data['players']) : 0,
    'serverVersion' => isset($data['server_version']) ? substr(strip_tags((string)$data['server_version']), 0, 64) : 'Unknown',
    'pluginVersion' => isset($data['plugin_version']) ? substr(strip_tags((string)$data['plugin_version']), 0, 32) : '1.0.0',
    'lastPing' => $now
];

// Clean up servers that haven't pinged in 24 hours to keep file small
foreach ($servers as $k => $info) {
    if ($now - $info['lastPing'] > 86400) {
        unset($servers[$k]);
    }
}

file_put_contents($dbFile, json_encode(['servers' => $servers], JSON_PRETTY_PRINT));

echo json_encode([
    'status' => 'ok',
    'activeServers' => count($servers)
]);
