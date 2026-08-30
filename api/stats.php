<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');

$dbFile = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'data' . DIRECTORY_SEPARATOR . 'servers.json';
$now = time();
$activeThreshold = 1200; // 20 minutes

$activeServers = 0;
$totalPlayers = 0;
$pluginCounts = [];
$uniqueServerIds = [];

if (is_file($dbFile)) {
    $content = file_get_contents($dbFile);
    $parsed = json_decode($content, true);
    if (isset($parsed['servers']) && is_array($parsed['servers'])) {
        foreach ($parsed['servers'] as $info) {
            $lastPing = isset($info['lastPing']) ? (int)$info['lastPing'] : 0;
            if ($now - $lastPing <= $activeThreshold) {
                $pName = $info['plugin'] ?? 'Unknown';
                $pluginCounts[$pName] = ($pluginCounts[$pName] ?? 0) + 1;
                $uniqueServerIds[$info['serverId'] ?? $pName] = true;
                $totalPlayers += isset($info['players']) ? (int)$info['players'] : 0;
            }
        }
        $activeServers = count($uniqueServerIds);
    }
}

echo json_encode([
    'activeServers' => $activeServers,
    'totalPlayers' => $totalPlayers,
    'pluginCounts' => $pluginCounts,
    'timestamp' => $now
]);
