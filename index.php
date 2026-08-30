<?php
/*
 * Dynamic Portfolio — Jakub (olczku)
 * Inteligentny skaner projektów, pluginów i botów
 */

$rootDir = __DIR__;
$pluginsDir = $rootDir . DIRECTORY_SEPARATOR . 'plugins';
$opisyDir = $rootDir . DIRECTORY_SEPARATOR . 'opisy';

if (!is_dir($pluginsDir)) @mkdir($pluginsDir, 0777, true);
if (!is_dir($opisyDir)) @mkdir($opisyDir, 0777, true);

// Baza wiedzy i inteligentne generowanie opisów
function generateSmartDescription($name, $commands, $apiVersion) {
    $n = strtolower($name);
    
    if (str_contains($n, 'trade') || str_contains($n, 'wymiana')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Zaawansowany system bezpiecznej wymiany przedmiotów i waluty pomiędzy graczami z dedykowanym interfejsem graficznym GUI, zabezpieczeniem przed oszustwami i podglądem na żywo.',
            'tags' => ['Java', 'Paper', 'GUI Trade', 'Economy']
        ];
    }
    if (str_contains($n, 'sprawdzanie') || str_contains($n, 'sprawdz')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Kompletny system kontroli i sprawdzania podejrzanych graczy z funkcją strefy sprawdzania, automatycznych kar za brak współpracy / przyznanie się oraz powiadomieniami dla administracji.',
            'tags' => ['Java', 'Paper', 'AntyCheat', 'Admin Tool']
        ];
    }
    if (str_contains($n, 'helpop')) {
        return [
            'type' => 'Plugin + Bot',
            'desc' => 'System szybkiego kontaktu graczy z administracją serwera. Wiadomości wysyłane komendą /helpop trafiają natychmiastowo na serwer gry oraz dedykowany kanał Discord przez Webhook.',
            'tags' => ['Java', 'Discord Webhook', 'Paper', 'Support']
        ];
    }
    if (str_contains($n, 'dcconsole') || str_contains($n, 'console')) {
        return [
            'type' => 'Plugin + Bot',
            'desc' => 'Zdalna konsola serwera Minecraft dostępna z poziomu Discorda dla uprawnionych administratorów z pełną autoryzacją i logowaniem komend.',
            'tags' => ['Java', 'Discord API', 'Remote Console']
        ];
    }
    if (str_contains($n, 'rtp')) {
        return [
            'type' => 'Plugin',
            'desc' => 'System losowej teleportacji graczy (Random Teleport) po mapie z inteligentnym wyszukiwaniem bezpiecznych lokacji (omijanie wody, lawy i blokad) oraz obsługą przycisków.',
            'tags' => ['Java', 'Paper', 'RTP', 'Teleport']
        ];
    }
    if (str_contains($n, 'pin')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Dwuetapowe zabezpieczenie kont administracyjnych unikalnym kodem PIN w GUI, blokujące poruszanie się i komendy do momentu poprawnej autoryzacji.',
            'tags' => ['Java', 'Security', '2FA PIN', 'Paper']
        ];
    }
    if (str_contains($n, 'statystyk') || str_contains($n, 'stats')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Moduł zbierania, archiwizacji i prezentacji szczegółowych statystyk graczy (czas gry, zabójstwa, zgony, wykopane surowce, rankingi) z bazą danych MySQL.',
            'tags' => ['Java', 'MySQL', 'Stats Engine', 'Paper']
        ];
    }
    if (str_contains($n, 'kopie') || str_contains($n, 'backup')) {
        return [
            'type' => 'Plugin / System',
            'desc' => 'Automatyczny system tworzenia skompresowanych kopii zapasowych (backupów) map i konfiguracji serwera z harmonogramem i asynchronicznym zapisem.',
            'tags' => ['Java', 'Async Backup', 'System']
        ];
    }
    if (str_contains($n, 'generator') || str_contains($n, 'stoniarka')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Wydajny generator kamienia, obsydianu i cennych rud z konfigurowalnym czasem odnawiania, dropem oraz craftingami bloków generujących.',
            'tags' => ['Java', 'Paper', 'Generators', 'Optimized']
        ];
    }
    if (str_contains($n, 'kod') || str_contains($n, 'voucher')) {
        return [
            'type' => 'Plugin',
            'desc' => 'System kodów podarunkowych i voucherów jednorazowych lub wielorazowych umożliwiający graczom odbieranie nagród, rang i waluty komendą /kod.',
            'tags' => ['Java', 'Paper', 'Rewards', 'Promo Codes']
        ];
    }
    if (str_contains($n, 'kolejka') || str_contains($n, 'queue')) {
        return [
            'type' => 'Plugin',
            'desc' => 'System kolejkowania logowania graczy przy pełnym serwerze lub po restarcie z obsługą priorytetów dla rang VIP/Administracji i ochroną przed lagami.',
            'tags' => ['Java', 'Queue System', 'Velocity/Bungee', 'Paper']
        ];
    }
    if (str_contains($n, 'lobby') || str_contains($n, 'hub')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Moduł szybkiego powrotu do lobby serwerowego (/lobby, /hub) z wykrywaniem stanu walki (Anty-Combat) oraz efektami dźwiękowymi i wizualnymi.',
            'tags' => ['Java', 'Paper', 'Lobby System']
        ];
    }
    if (str_contains($n, 'powitania') || str_contains($n, 'welcome')) {
        return [
            'type' => 'Plugin',
            'desc' => 'System powitań nowych i powracających graczy z konfigurowalnymi wiadomościami na czacie, tytułami (Titles), paskiem akcji (Actionbar) i dźwiękami.',
            'tags' => ['Java', 'Paper', 'Welcome Messages']
        ];
    }
    if (str_contains($n, 'antysweap') || str_contains($n, 'sweep')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Modyfikacja mechaniki zamachu mieczem (Sweep Attack) przywracająca klasyczny styl walki PvP bez obrażeń obszarowych na sojuszników.',
            'tags' => ['Java', 'Paper', 'PvP Engine']
        ];
    }
    if (str_contains($n, 'lightlevel') || str_contains($n, 'light')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Optymalizator oświetlenia na wyspach i chunkach, eliminujący spadki TPS wywoływane przez częste zmiany światła i duże farmy.',
            'tags' => ['Java', 'Paper', 'Optimization', 'Light Engine']
        ];
    }
    if (str_contains($n, 'skrzyn') || str_contains($n, 'cooldown')) {
        return [
            'type' => 'Plugin',
            'desc' => 'Zarządzanie czasem odnawiania otwierania skrzyń ze specjalnym dropem i nagrodami dziennymi z zapisem stanu dla każdego gracza.',
            'tags' => ['Java', 'Paper', 'Cooldowns', 'Loot']
        ];
    }
    if (str_contains($n, 'restart') || str_contains($n, 'autorestart')) {
        return [
            'type' => 'System',
            'desc' => 'Harmonogram automatycznych restartów serwera z odliczaniem na czacie, actionbarze i bezpiecznym zapisem danych graczy przed wyłączeniem.',
            'tags' => ['Java', 'Server Maintenance', 'AutoRestart']
        ];
    }
    if (str_contains($n, 'core') || str_contains($n, 'shield')) {
        return [
            'type' => 'Plugin Core',
            'desc' => 'Główny, wysoko zoptymalizowany silnik serwerowy (Core) zawierający zestaw niezbędnych komend, zarządzanie ekwipunkami, leczeniem oraz ochroną serwera.',
            'tags' => ['Java', 'Paper', 'Core Engine', 'Performance']
        ];
    }
    if (str_contains($n, 'bot') || str_contains($n, 'discord')) {
        return [
            'type' => 'Bot Discord',
            'desc' => 'Zaawansowany bot Discord zintegrowany z serwerem Minecraft – automatyczna weryfikacja, synchronizacja rang, logi kar, statystyki i powiadomienia.',
            'tags' => ['Python / Java', 'Discord API', 'Automation', 'Sync']
        ];
    }

    if (!empty($commands)) {
        $cmdList = implode(', ', array_map(fn($c) => '/' . $c['name'], array_slice($commands, 0, 4)));
        return [
            'type' => 'Plugin',
            'desc' => "Dedykowany plugin serwerowy oferujący funkcje i komendy: {$cmdList}. Zoptymalizowany pod kątem zerowego wpływu na wydajność serwera.",
            'tags' => ['Java', $apiVersion ? "Minecraft {$apiVersion}" : 'Paper', 'Custom']
        ];
    }

    return [
        'type' => 'Plugin',
        'desc' => 'Dedykowane rozwiązanie serwerowe zoptymalizowane pod wysokie obciążenie i stabilność.',
        'tags' => ['Java', 'Paper', 'Custom']
    ];
}

// Funkcja skanująca pliki projektu (plugin.yml, pom.xml itp.)
function inspectProjectFiles($projectName, $pluginsDir) {
    $folderPath = $pluginsDir . DIRECTORY_SEPARATOR . $projectName;
    $info = [
        'pluginName' => $projectName,
        'version' => '1.0',
        'apiVersion' => '1.20',
        'mainClass' => '',
        'author' => 'Jakub (olczku)',
        'description' => '',
        'commands' => [],
        'permissions' => []
    ];

    if (!is_dir($folderPath)) return $info;

    // Szukanie plugin.yml
    $candidates = [
        $folderPath . DIRECTORY_SEPARATOR . 'src' . DIRECTORY_SEPARATOR . 'main' . DIRECTORY_SEPARATOR . 'resources' . DIRECTORY_SEPARATOR . 'plugin.yml',
        $folderPath . DIRECTORY_SEPARATOR . 'target' . DIRECTORY_SEPARATOR . 'classes' . DIRECTORY_SEPARATOR . 'plugin.yml',
        $folderPath . DIRECTORY_SEPARATOR . 'plugin.yml'
    ];

    $pluginYmlContent = null;
    foreach ($candidates as $c) {
        if (is_file($c)) {
            $pluginYmlContent = file_get_contents($c);
            break;
        }
    }

    if ($pluginYmlContent) {
        $lines = explode("\n", str_replace("\r", "", $pluginYmlContent));
        $inCommands = false;
        $inPermissions = false;
        $currCmd = null;

        foreach ($lines as $line) {
            $trimmed = trim($line);
            if ($trimmed === '' || str_starts_with($trimmed, '#')) continue;

            if (preg_match('/^name\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) $info['pluginName'] = trim($m[1]);
            if (preg_match('/^version\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) $info['version'] = trim($m[1]);
            if (preg_match('/^api-version\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) $info['apiVersion'] = trim($m[1]);
            if (preg_match('/^main\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) $info['mainClass'] = trim($m[1]);
            if (preg_match('/^author\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) $info['author'] = trim($m[1]);
            if (preg_match('/^description\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) $info['description'] = trim($m[1]);

            if (preg_match('/^commands\s*:/i', $line)) {
                $inCommands = true;
                $inPermissions = false;
                continue;
            }
            if (preg_match('/^permissions\s*:/i', $line)) {
                $inCommands = false;
                $inPermissions = true;
                continue;
            }

            if ($inCommands) {
                if (preg_match('/^ {2}([a-zA-Z0-9_\-]+)\s*:/', $line, $m)) {
                    $currCmd = $m[1];
                    $info['commands'][$currCmd] = ['name' => $currCmd, 'desc' => '', 'usage' => '', 'perm' => ''];
                } elseif ($currCmd && preg_match('/^ {4}description\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) {
                    $info['commands'][$currCmd]['desc'] = trim($m[1]);
                } elseif ($currCmd && preg_match('/^ {4}usage\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) {
                    $info['commands'][$currCmd]['usage'] = trim($m[1]);
                } elseif ($currCmd && preg_match('/^ {4}permission\s*:\s*[\'"]?([^\'"]+)[\'"]?/i', $line, $m)) {
                    $info['commands'][$currCmd]['perm'] = trim($m[1]);
                } elseif (preg_match('/^[a-zA-Z0-9_\-]+:/', $line)) {
                    $inCommands = false;
                }
            }

            if ($inPermissions) {
                if (preg_match('/^ {2}([a-zA-Z0-9_\-\.]+)\s*:/', $line, $m)) {
                    $info['permissions'][] = $m[1];
                } elseif (preg_match('/^[a-zA-Z0-9_\-]+:/', $line)) {
                    $inPermissions = false;
                }
            }
        }
    }

    return $info;
}

// Główna funkcja pobierająca metadane projektu
function getProjectFullData($projectName, $opisyDir, $pluginsDir) {
    $inspected = inspectProjectFiles($projectName, $pluginsDir);
    $opisFile = $opisyDir . DIRECTORY_SEPARATOR . $projectName . '.txt';

    $smart = generateSmartDescription($projectName, $inspected['commands'], $inspected['apiVersion']);
    $type = $smart['type'];
    $description = $smart['desc'];
    $tags = $smart['tags'];

    if (!is_file($opisFile)) {
        // Automatyczne utworzenie bogatego pliku opisu
        $template = "typ: " . $type . "\n"
                  . "opis: " . $description . "\n"
                  . "tagi: " . implode(', ', $tags) . "\n";
        @file_put_contents($opisFile, $template);
    } else {
        $content = file_get_contents($opisFile);
        $lines = explode("\n", str_replace("\r", "", $content));
        $customDesc = [];
        $hasCustomFields = false;

        foreach ($lines as $line) {
            $trimmed = trim($line);
            if ($trimmed === '') continue;

            if (preg_match('/^typ\s*:\s*(.+)$/i', $trimmed, $m)) {
                $type = trim($m[1]);
                $hasCustomFields = true;
            } elseif (preg_match('/^opis\s*:\s*(.+)$/i', $trimmed, $m)) {
                $customDesc[] = trim($m[1]);
                $hasCustomFields = true;
            } elseif (preg_match('/^tagi\s*:\s*(.+)$/i', $trimmed, $m)) {
                $tags = array_map('trim', explode(',', $m[1]));
                $hasCustomFields = true;
            } else {
                $customDesc[] = $trimmed;
            }
        }

        if (!empty($customDesc)) {
            $parsedDesc = implode("\n", $customDesc);
            if ($parsedDesc !== 'Plugin / projekt bez opisu.' && $parsedDesc !== 'Dedykowany plugin serwerowy zoptymalizowany pod wysokie obciążenie.') {
                $description = $parsedDesc;
            }
        }
    }

    // Obrazek
    $image = null;
    $folderPath = $pluginsDir . DIRECTORY_SEPARATOR . $projectName;
    if (is_dir($folderPath)) {
        foreach (['image.webp', 'image.png', 'image.jpg', 'image.jpeg'] as $img) {
            if (is_file($folderPath . DIRECTORY_SEPARATOR . $img)) {
                $image = 'plugins/' . rawurlencode($projectName) . '/' . $img;
                break;
            }
        }
    }
    if (!$image) {
        foreach ([$projectName.'.png', $projectName.'.webp', $projectName.'.jpg'] as $img) {
            if (is_file($opisyDir . DIRECTORY_SEPARATOR . $img)) {
                $image = 'opisy/' . rawurlencode($img);
                break;
            }
        }
    }

    return [
        'name' => $projectName,
        'pluginName' => $inspected['pluginName'] ?: $projectName,
        'type' => $type,
        'description' => $description,
        'tags' => $tags,
        'image' => $image,
        'version' => $inspected['version'],
        'apiVersion' => $inspected['apiVersion'],
        'mainClass' => $inspected['mainClass'],
        'author' => $inspected['author'],
        'commands' => array_values($inspected['commands']),
        'permissions' => $inspected['permissions']
    ];
}

$detectedProjects = [];

if (is_dir($pluginsDir)) {
    foreach (scandir($pluginsDir) as $item) {
        if ($item === '.' || $item === '..' || str_starts_with($item, '.')) continue;
        $fullPath = $pluginsDir . DIRECTORY_SEPARATOR . $item;
        $projectName = $item;
        if (is_file($fullPath)) {
            $ext = strtolower(pathinfo($item, PATHINFO_EXTENSION));
            if (in_array($ext, ['png', 'jpg', 'jpeg', 'webp', 'txt'])) continue;
            $projectName = pathinfo($item, PATHINFO_FILENAME);
        }
        $detectedProjects[$projectName] = true;
    }
}

if (is_dir($opisyDir)) {
    foreach (scandir($opisyDir) as $item) {
        if ($item === '.' || $item === '..' || str_starts_with($item, '.')) continue;
        $ext = strtolower(pathinfo($item, PATHINFO_EXTENSION));
        if ($ext === 'txt') {
            $pName = pathinfo($item, PATHINFO_FILENAME);
            $detectedProjects[$pName] = true;
        }
    }
}

$projects = [];
foreach (array_keys($detectedProjects) as $pName) {
    $projects[] = getProjectFullData($pName, $opisyDir, $pluginsDir);
}

usort($projects, fn($a, $b) => strcasecmp($a['name'], $b['name']));
$totalProjects = count($projects);

// Obliczanie rzeczywistych statystyk serwerów (ostatnie 20 min)
$dbFile = $rootDir . DIRECTORY_SEPARATOR . 'data' . DIRECTORY_SEPARATOR . 'servers.json';
$now = time();
$activeThreshold = 1200; // 20 minut
$activeServers = 0;
$totalPlayers = 0;
$pluginCounts = [];
$uniqueServerIds = [];

if (is_file($dbFile)) {
    $content = @file_get_contents($dbFile);
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
?>
<!doctype html>
<html lang="pl">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="Portfolio Jakuba (olczku) — Plugin Developer. Pluginy Minecraft, boty Discord i automatyzacje.">
  <title>Jakub (olczku) — Plugin Developer</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="style.css">
</head>
<body>

<div class="bg-glow"></div>
<div class="bg-grid"></div>

<!-- Header -->
<header class="header-nav">
  <div class="nav-container">
    <a class="brand" href="#">
      <div class="brand-icon">J</div>
      <span class="brand-text">olczku</span>
    </a>
    <nav class="nav-links">
      <a href="#about">O mnie</a>
      <a href="#services">Oferta</a>
      <a href="#projects">Realizacje (<?= $totalProjects ?>)</a>
      <a href="#contact" class="nav-cta">Skontaktuj się</a>
    </nav>
  </div>
</header>

<main>
  <!-- Hero Section -->
  <section id="about" class="hero">
    <div class="hero-content">
      <div class="status-badge">
        <div class="status-pulse"></div>
        <span>Dostępny do realizacji i zleceń</span>
      </div>
      <h1>Buduję <em>wydajne pluginy</em>,<br>boty i systemy.</h1>
      <p class="hero-lead">
        Cześć, jestem <strong>Jakub (olczku)</strong>. Tworzę autorskie rozwiązania pod serwery Minecraft (Paper, Folia, Spigot), boty Discord oraz systemy automatyzacji. Skupiam się na optymalizacji, niezawodności i czystym kodzie.
      </p>
      <div class="hero-actions">
        <a class="btn btn-primary" href="#contact">Napisz do mnie ↓</a>
        <a class="btn btn-secondary" href="#projects">Zobacz realizacje</a>
      </div>
    </div>

    <!-- Code Showcase Window -->
    <div class="code-window">
      <div class="code-header">
        <div class="code-dots">
          <span class="dot-red"></span>
          <span class="dot-yellow"></span>
          <span class="dot-green"></span>
        </div>
        <span class="code-title">DeveloperProfile.java</span>
      </div>
      <div class="code-body">
        <pre><span class="syn-kw">public class</span> <span class="syn-type">DeveloperProfile</span> {
    <span class="syn-kw">private final</span> <span class="syn-type">String</span> nick = <span class="syn-str">"olczku"</span>;
    <span class="syn-kw">private final</span> <span class="syn-type">String</span> role = <span class="syn-str">"Plugin Developer"</span>;
    <span class="syn-kw">private final</span> <span class="syn-type">String[]</span> stack = {
        <span class="syn-str">"Java / Spigot / Paper"</span>,
        <span class="syn-str">"Python / Discord.py"</span>,
        <span class="syn-str">"MySQL / Redis"</span>
    };

    <span class="syn-kw">public void</span> <span class="syn-fn">createSolution</span>() {
        <span class="syn-type">HighPerformance</span>.enable();
        <span class="syn-type">CleanCode</span>.maintain();
        <span class="syn-type">TelemetryHeartbeat</span>.sync();
    }
}</pre>
      </div>
    </div>
  </section>

  <!-- Live Stats Bar -->
  <div class="stats-bar">
    <div class="stat-item">
      <div class="stat-number">
        <span class="highlight live-indicator-dot"></span>
        <span id="statActiveServers"><?= $activeServers ?></span>
      </div>
      <div class="stat-label">Aktywne serwery</div>
    </div>
    <div class="stat-item">
      <div class="stat-number"><span class="highlight">24/7</span></div>
      <div class="stat-label">Wsparcie techniczne</div>
    </div>
    <div class="stat-item">
      <div class="stat-number"><span class="highlight"><?= $totalProjects ?></span>+</div>
      <div class="stat-label">Projektów w portfolio</div>
    </div>
    <div class="stat-item">
      <div class="stat-number"><span class="highlight">100</span>%</div>
      <div class="stat-label">Autorski & czysty kod</div>
    </div>
  </div>

  <!-- About / Services -->
  <section id="services">
    <div class="section-tag"><span class="dot"></span> 01 / CZYM SIĘ ZAJMUJĘ</div>
    <h2 class="section-title">Technologie i <span>zakres usług</span></h2>
    <p class="section-desc">Tworzę rozwiązania od prostych skryptów po rozbudowane systemy sieciowe.</p>

    <div class="services-grid">
      <article class="service-card">
        <div class="service-icon">☕</div>
        <h3>Pluginy Minecraft</h3>
        <p>Autorskie pluginy pod wersje 1.8 - 1.21+ (Paper, Spigot, Folia, Velocity). Ekonomia, gildie, minigry, dropy i niestandardowe mechaniki.</p>
        <div class="service-tags">
          <span>Java</span>
          <span>PaperMC</span>
          <span>NMS</span>
          <span>Packets</span>
        </div>
      </article>

      <article class="service-card">
        <div class="service-icon">🤖</div>
        <h3>Boty Discord</h3>
        <p>Zaawansowane boty społecznościowe, automatyczna weryfikacja graczy, synchronizacja rang z serwerem Minecraft, ticket systemy.</p>
        <div class="service-tags">
          <span>Python</span>
          <span>Discord.py</span>
          <span>Webhooks</span>
        </div>
      </article>

      <article class="service-card">
        <div class="service-icon">⚡</div>
        <h3>Bazy danych i Integracje</h3>
        <p>Szybki zapis asynchroniczny danych, integracje MySQL, SQLite, Redis dla sieci serwerów i synchronizacji statystyk w czasie rzeczywistym.</p>
        <div class="service-tags">
          <span>MySQL</span>
          <span>Redis</span>
          <span>Async</span>
        </div>
      </article>
    </div>
  </section>

  <!-- Projects Section -->
  <section id="projects">
    <div class="projects-header">
      <div>
        <div class="section-tag"><span class="dot"></span> 02 / REALIZACJE</div>
        <h2 class="section-title">Przykładowe <span>projekty</span></h2>
      </div>
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input id="search" class="search-input" type="search" placeholder="Szukaj projektu, bota lub technologii...">
      </div>
    </div>

    <div id="grid" class="projects-grid">
      <?php foreach ($projects as $idx => $project): 
        $pCount = $pluginCounts[$project['name']] ?? 0;
        $isBot = stripos($project['type'], 'bot') !== false;
        $isSystem = stripos($project['type'], 'system') !== false;
        $pillClass = $isBot ? 'bot-pill' : ($isSystem ? 'system-pill' : '');
        $searchData = strtolower($project['name'] . ' ' . $project['type'] . ' ' . $project['description'] . ' ' . implode(' ', $project['tags']));
      ?>
        <article class="project-card" data-project="<?= htmlspecialchars($project['name'], ENT_QUOTES, 'UTF-8') ?>" data-search="<?= htmlspecialchars($searchData, ENT_QUOTES, 'UTF-8') ?>">
          <?php if ($project['image']): ?>
            <img class="project-card-img" src="<?= htmlspecialchars($project['image'], ENT_QUOTES, 'UTF-8') ?>" alt="<?= htmlspecialchars($project['name'], ENT_QUOTES, 'UTF-8') ?>" loading="lazy">
          <?php endif; ?>
          
          <div class="project-card-top">
            <span class="project-type-pill <?= $pillClass ?>">
              <span class="dot"></span> <?= htmlspecialchars(strtoupper($project['type']), ENT_QUOTES, 'UTF-8') ?>
            </span>
            <span class="plugin-live-badge" id="badge-<?= htmlspecialchars($project['name'], ENT_QUOTES, 'UTF-8') ?>" style="<?= $pCount > 0 ? '' : 'display:none;' ?>">
              <span class="live-dot"></span> <span class="badge-num"><?= $pCount ?></span> serwerów
            </span>
          </div>

          <h3><?= htmlspecialchars($project['name'], ENT_QUOTES, 'UTF-8') ?></h3>
          <p><?= nl2br(htmlspecialchars($project['description'], ENT_QUOTES, 'UTF-8')) ?></p>
          
          <?php if (!empty($project['tags'])): ?>
            <div class="project-tags">
              <?php foreach ($project['tags'] as $tag): ?>
                <span class="project-tag"><?= htmlspecialchars($tag, ENT_QUOTES, 'UTF-8') ?></span>
              <?php endforeach; ?>
            </div>
          <?php endif; ?>

          <div class="project-card-footer">
            <button type="button" class="project-btn-details" onclick="openDetailsModal(<?= $idx ?>)">
              🔍 Szczegóły
            </button>
            <a href="#contact" class="project-action-btn">Zamów podobny →</a>
          </div>
        </article>
      <?php endforeach; ?>

      <?php if (!$projects): ?>
        <div class="empty-state">
          Brak wykrytych projektów. Wrzuć folder lub plik do katalogu <code>/plugins/</code>.
        </div>
      <?php endif; ?>
      <div id="noResults" class="empty-state" hidden>Nie znaleziono projektów pasujących do zapytania.</div>
    </div>
  </section>

  <!-- Contact Section -->
  <section id="contact" class="contact-section">
    <div class="contact-box">
      <div class="contact-layout">
        <div class="contact-info">
          <div class="section-tag"><span class="dot"></span> 03 / KONTAKT</div>
          <h2>Masz pomysł?<br><em>Zrealizujmy go.</em></h2>
          <p>Potrzebujesz autorskiego pluginu, bota na Discorda lub dedykowanego systemu? Skontaktuj się ze mną bezpośrednio przez Discord lub E-mail, a omówimy szczegóły i wycenę.</p>
        </div>

        <div class="contact-cards">
          <!-- Discord Card -->
          <div class="contact-item-card">
            <div class="contact-item-left">
              <div class="contact-avatar-icon icon-discord">💬</div>
              <div class="contact-details">
                <h4>Discord</h4>
                <div class="contact-val">olczku_</div>
              </div>
            </div>
            <div class="contact-actions">
              <button class="btn btn-discord btn-copy" onclick="copyText('olczku_', 'Skopiowano Discord: olczku_')">
                📋 Kopiuj nick
              </button>
            </div>
          </div>

          <!-- Email Card -->
          <div class="contact-item-card">
            <div class="contact-item-left">
              <div class="contact-avatar-icon icon-email">✉️</div>
              <div class="contact-details">
                <h4>Adres E-mail</h4>
                <div class="contact-val">olczkuyt@gmail.com</div>
              </div>
            </div>
            <div class="contact-actions">
              <a class="btn btn-email" href="mailto:olczkuyt@gmail.com">
                Napisz wiadomość
              </a>
              <button class="btn btn-copy" onclick="copyText('olczkuyt@gmail.com', 'Skopiowano E-mail: olczkuyt@gmail.com')">
                📋
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</main>

<!-- Modal Szczegóły Projektu -->
<div id="detailsModal" class="modal-backdrop" onclick="if(event.target === this) closeDetailsModal()">
  <div class="modal-window">
    <div class="modal-header">
      <div class="modal-header-info">
        <div class="modal-badges">
          <span id="modalTypeBadge" class="project-type-pill"><span class="dot"></span> <span id="modalTypeVal">PLUGIN</span></span>
          <span id="modalVerBadge" class="project-tag">v1.0</span>
        </div>
        <h3 id="modalTitle" class="modal-title">Nazwa Projektu</h3>
      </div>
      <button class="modal-close" onclick="closeDetailsModal()">✕</button>
    </div>

    <div class="modal-body">
      <div>
        <div class="modal-section-label">⚡ O projekcie i zastosowaniu</div>
        <p id="modalDesc" class="modal-desc-text"></p>
      </div>

      <div id="modalCommandsWrap">
        <div class="modal-section-label">🛠️ Wykryte komendy i funkcje</div>
        <div id="modalCommandsList" class="modal-commands-box"></div>
      </div>

      <div>
        <div class="modal-section-label">📦 Szczegóły techniczne</div>
        <div class="modal-tech-grid">
          <div class="modal-tech-item">
            <div class="modal-tech-key">Wersja silnika</div>
            <div id="modalApiVer" class="modal-tech-val">Paper / Spigot 1.20</div>
          </div>
          <div class="modal-tech-item">
            <div class="modal-tech-key">Architektura</div>
            <div id="modalArch" class="modal-tech-val">Asynchroniczny / Wysoka wydajność</div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-footer">
      <button class="btn btn-secondary" onclick="closeDetailsModal()">Zamknij</button>
      <a class="btn btn-primary" href="#contact" onclick="closeDetailsModal()">Zamów podobny projekt →</a>
    </div>
  </div>
</div>

<!-- Footer -->
<footer>
  <div class="footer-container">
    <div class="footer-credits">
      © <?= date('Y') ?> <strong>Jakub (olczku)</strong> · Plugin Developer
    </div>
    <div class="footer-status">
      <span class="dot"></span> Discord: <strong>olczku_</strong> · Mail: <strong>olczkuyt@gmail.com</strong>
    </div>
  </div>
</footer>

<!-- Toast Notification -->
<div id="toast" class="toast">
  <span class="toast-icon">✓</span>
  <span id="toastMsg">Skopiowano do schowka!</span>
</div>

<script>
const projectsData = <?= json_encode($projects, JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;

// Search functionality
const searchInput = document.querySelector('#search');
const projectCards = [...document.querySelectorAll('.project-card')];
const noResults = document.querySelector('#noResults');

searchInput?.addEventListener('input', () => {
  const query = searchInput.value.toLowerCase().trim();
  let matches = 0;
  projectCards.forEach(card => {
    const isVisible = card.dataset.search.includes(query);
    card.hidden = !isVisible;
    if (isVisible) matches++;
  });
  if (noResults) {
    noResults.hidden = matches !== 0 || projectCards.length === 0;
  }
});

// Modal Details Logic
function openDetailsModal(index) {
  const p = projectsData[index];
  if (!p) return;

  const modal = document.getElementById('detailsModal');
  const title = document.getElementById('modalTitle');
  const desc = document.getElementById('modalDesc');
  const typeVal = document.getElementById('modalTypeVal');
  const typeBadge = document.getElementById('modalTypeBadge');
  const verBadge = document.getElementById('modalVerBadge');
  const apiVer = document.getElementById('modalApiVer');
  const cmdWrap = document.getElementById('modalCommandsWrap');
  const cmdList = document.getElementById('modalCommandsList');

  title.textContent = p.name;
  desc.textContent = p.description;
  typeVal.textContent = p.type.toUpperCase();

  typeBadge.className = 'project-type-pill';
  if (p.type.toLowerCase().includes('bot')) typeBadge.classList.add('bot-pill');
  else if (p.type.toLowerCase().includes('system')) typeBadge.classList.add('system-pill');

  verBadge.textContent = 'v' + (p.version || '1.0');
  apiVer.textContent = p.apiVersion ? ('Minecraft ' + p.apiVersion) : 'Java / Paper';

  if (p.commands && p.commands.length > 0) {
    cmdWrap.style.display = 'block';
    cmdList.innerHTML = p.commands.map(c => `
      <div class="modal-cmd-item">
        <div class="modal-cmd-head">
          <span class="modal-cmd-name">/${escapeHtml(c.name)}</span>
          ${c.usage ? `<span class="project-tag">${escapeHtml(c.usage)}</span>` : ''}
        </div>
        ${c.desc ? `<div class="modal-cmd-desc">${escapeHtml(c.desc)}</div>` : ''}
        ${c.perm ? `<div class="modal-cmd-perm">Uprawnienie: ${escapeHtml(c.perm)}</div>` : ''}
      </div>
    `).join('');
  } else {
    cmdWrap.style.display = 'none';
  }

  modal.classList.add('active');
  document.body.style.overflow = 'hidden';
}

function closeDetailsModal() {
  const modal = document.getElementById('detailsModal');
  modal.classList.remove('active');
  document.body.style.overflow = '';
}

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closeDetailsModal();
});

function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// Toast / Copy functionality
let toastTimer = null;
function copyText(text, message) {
  navigator.clipboard.writeText(text).then(() => {
    showToast(message);
  }).catch(() => {
    const tempInput = document.createElement('input');
    tempInput.value = text;
    document.body.appendChild(tempInput);
    tempInput.select();
    document.execCommand('copy');
    document.body.removeChild(tempInput);
    showToast(message);
  });
}

function showToast(msg) {
  const toast = document.getElementById('toast');
  const toastMsg = document.getElementById('toastMsg');
  if (!toast || !toastMsg) return;

  toastMsg.textContent = msg;
  toast.classList.add('show');

  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => {
    toast.classList.remove('show');
  }, 3000);
}

// Live stats polling
async function updateLiveStats() {
  try {
    const res = await fetch('/api/stats');
    if (!res.ok) return;
    const data = await res.json();

    const elServers = document.getElementById('statActiveServers');
    if (elServers) elServers.textContent = data.activeServers || 0;

    if (data.pluginCounts) {
      for (const [pName, count] of Object.entries(data.pluginCounts)) {
        const badge = document.getElementById('badge-' + pName);
        if (badge) {
          badge.style.display = count > 0 ? 'inline-flex' : 'none';
          const numSpan = badge.querySelector('.badge-num');
          if (numSpan) numSpan.textContent = count;
        }
      }
    }
  } catch (e) {}
}
setInterval(updateLiveStats, 15000);
</script>

</body>
</html>
