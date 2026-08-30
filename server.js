const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const ROOT_DIR = __dirname;
const PLUGINS_DIR = path.join(ROOT_DIR, 'plugins');
const OPISY_DIR = path.join(ROOT_DIR, 'opisy');
const DATA_DIR = path.join(ROOT_DIR, 'data');
const SERVERS_FILE = path.join(DATA_DIR, 'servers.json');

if (!fs.existsSync(PLUGINS_DIR)) fs.mkdirSync(PLUGINS_DIR, { recursive: true });
if (!fs.existsSync(OPISY_DIR)) fs.mkdirSync(OPISY_DIR, { recursive: true });
if (!fs.existsSync(DATA_DIR)) fs.mkdirSync(DATA_DIR, { recursive: true });
if (!fs.existsSync(SERVERS_FILE)) {
  fs.writeFileSync(SERVERS_FILE, JSON.stringify({ servers: {} }, null, 2), 'utf8');
}

function escapeHtml(text) {
  if (!text) return '';
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function generateSmartDescription(name, commands, apiVersion) {
  const n = name.toLowerCase();

  if (n.includes('trade') || n.includes('wymiana')) {
    return {
      type: 'Plugin',
      desc: 'Zaawansowany system bezpiecznej wymiany przedmiotów i waluty pomiędzy graczami z dedykowanym interfejsem graficznym GUI, zabezpieczeniem przed oszustwami i podglądem na żywo.',
      tags: ['Java', 'Paper', 'GUI Trade', 'Economy']
    };
  }
  if (n.includes('sprawdzanie') || n.includes('sprawdz')) {
    return {
      type: 'Plugin',
      desc: 'Kompletny system kontroli i sprawdzania podejrzanych graczy z funkcją strefy sprawdzania, automatycznych kar za brak współpracy / przyznanie się oraz powiadomieniami dla administracji.',
      tags: ['Java', 'Paper', 'AntyCheat', 'Admin Tool']
    };
  }
  if (n.includes('helpop')) {
    return {
      type: 'Plugin + Bot',
      desc: 'System szybkiego kontaktu graczy z administracją serwera. Wiadomości wysyłane komendą /helpop trafiają natychmiastowo na serwer gry oraz dedykowany kanał Discord przez Webhook.',
      tags: ['Java', 'Discord Webhook', 'Paper', 'Support']
    };
  }
  if (n.includes('dcconsole') || n.includes('console')) {
    return {
      type: 'Plugin + Bot',
      desc: 'Zdalna konsola serwera Minecraft dostępna z poziomu Discorda dla uprawnionych administratorów z pełną autoryzacją i logowaniem komend.',
      tags: ['Java', 'Discord API', 'Remote Console']
    };
  }
  if (n.includes('rtp')) {
    return {
      type: 'Plugin',
      desc: 'System losowej teleportacji graczy (Random Teleport) po mapie z inteligentnym wyszukiwaniem bezpiecznych lokacji (omijanie wody, lawy i blokad) oraz obsługą przycisków.',
      tags: ['Java', 'Paper', 'RTP', 'Teleport']
    };
  }
  if (n.includes('pin')) {
    return {
      type: 'Plugin',
      desc: 'Dwuetapowe zabezpieczenie kont administracyjnych unikalnym kodem PIN w GUI, blokujące poruszanie się i komendy do momentu poprawnej autoryzacji.',
      tags: ['Java', 'Security', '2FA PIN', 'Paper']
    };
  }
  if (n.includes('statystyk') || n.includes('stats')) {
    return {
      type: 'Plugin',
      desc: 'Moduł zbierania, archiwizacji i prezentacji szczegółowych statystyk graczy (czas gry, zabójstwa, zgony, wykopane surowce, rankingi) z bazą danych MySQL.',
      tags: ['Java', 'MySQL', 'Stats Engine', 'Paper']
    };
  }
  if (n.includes('kopie') || n.includes('backup')) {
    return {
      type: 'Plugin / System',
      desc: 'Automatyczny system tworzenia skompresowanych kopii zapasowych (backupów) map i konfiguracji serwera z harmonogramem i asynchronicznym zapisem.',
      tags: ['Java', 'Async Backup', 'System']
    };
  }
  if (n.includes('generator') || n.includes('stoniarka')) {
    return {
      type: 'Plugin',
      desc: 'Wydajny generator kamienia, obsydianu i cennych rud z konfigurowalnym czasem odnawiania, dropem oraz craftingami bloków generujących.',
      tags: ['Java', 'Paper', 'Generators', 'Optimized']
    };
  }
  if (n.includes('kod') || n.includes('voucher')) {
    return {
      type: 'Plugin',
      desc: 'System kodów podarunkowych i voucherów jednorazowych lub wielorazowych umożliwiający graczom odbieranie nagród, rang i waluty komendą /kod.',
      tags: ['Java', 'Paper', 'Rewards', 'Promo Codes']
    };
  }
  if (n.includes('kolejka') || n.includes('queue')) {
    return {
      type: 'Plugin',
      desc: 'System kolejkowania logowania graczy przy pełnym serwerze lub po restarcie z obsługą priorytetów dla rang VIP/Administracji i ochroną przed lagami.',
      tags: ['Java', 'Queue System', 'Velocity/Bungee', 'Paper']
    };
  }
  if (n.includes('lobby') || n.includes('hub')) {
    return {
      type: 'Plugin',
      desc: 'Moduł szybkiego powrotu do lobby serwerowego (/lobby, /hub) z wykrywaniem stanu walki (Anty-Combat) oraz efektami dźwiękowymi i wizualnymi.',
      tags: ['Java', 'Paper', 'Lobby System']
    };
  }
  if (n.includes('powitania') || n.includes('welcome')) {
    return {
      type: 'Plugin',
      desc: 'System powitań nowych i powracających graczy z konfigurowalnymi wiadomościami na czacie, tytułami (Titles), paskiem akcji (Actionbar) i dźwiękami.',
      tags: ['Java', 'Paper', 'Welcome Messages']
    };
  }
  if (n.includes('antysweap') || n.includes('sweep')) {
    return {
      type: 'Plugin',
      desc: 'Modyfikacja mechaniki zamachu mieczem (Sweep Attack) przywracająca klasyczny styl walki PvP bez obrażeń obszarowych na sojuszników.',
      tags: ['Java', 'Paper', 'PvP Engine']
    };
  }
  if (n.includes('lightlevel') || n.includes('light')) {
    return {
      type: 'Plugin',
      desc: 'Optymalizator oświetlenia na wyspach i chunkach, eliminujący spadki TPS wywoływane przez częste zmiany światła i duże farmy.',
      tags: ['Java', 'Paper', 'Optimization', 'Light Engine']
    };
  }
  if (n.includes('skrzyn') || n.includes('cooldown')) {
    return {
      type: 'Plugin',
      desc: 'Zarządzanie czasem odnawiania otwierania skrzyń ze specjalnym dropem i nagrodami dziennymi z zapisem stanu dla każdego gracza.',
      tags: ['Java', 'Paper', 'Cooldowns', 'Loot']
    };
  }
  if (n.includes('restart') || n.includes('autorestart')) {
    return {
      type: 'System',
      desc: 'Harmonogram automatycznych restartów serwera z odliczaniem na czacie, actionbarze i bezpiecznym zapisem danych graczy przed wyłączeniem.',
      tags: ['Java', 'Server Maintenance', 'AutoRestart']
    };
  }
  if (n.includes('core') || n.includes('shield')) {
    return {
      type: 'Plugin Core',
      desc: 'Główny, wysoko zoptymalizowany silnik serwerowy (Core) zawierający zestaw niezbędnych komend, zarządzanie ekwipunkami, leczeniem oraz ochroną serwera.',
      tags: ['Java', 'Paper', 'Core Engine', 'Performance']
    };
  }
  if (n.includes('bot') || n.includes('discord')) {
    return {
      type: 'Bot Discord',
      desc: 'Zaawansowany bot Discord zintegrowany z serwerem Minecraft – automatyczna weryfikacja, synchronizacja rang, logi kar, statystyki i powiadomienia.',
      tags: ['Python / Java', 'Discord API', 'Automation', 'Sync']
    };
  }

  if (commands && commands.length > 0) {
    const cmdList = commands.slice(0, 4).map(c => '/' + c.name).join(', ');
    return {
      type: 'Plugin',
      desc: `Dedykowany plugin serwerowy oferujący funkcje i komendy: ${cmdList}. Zoptymalizowany pod kątem zerowego wpływu na wydajność serwera.`,
      tags: ['Java', apiVersion ? `Minecraft ${apiVersion}` : 'Paper', 'Custom']
    };
  }

  return {
    type: 'Plugin',
    desc: 'Dedykowane rozwiązanie serwerowe zoptymalizowane pod wysokie obciążenie i stabilność.',
    tags: ['Java', 'Paper', 'Custom']
  };
}

function inspectProjectFiles(projectName) {
  const folderPath = path.join(PLUGINS_DIR, projectName);
  const info = {
    pluginName: projectName,
    version: '1.0',
    apiVersion: '1.20',
    mainClass: '',
    author: 'Jakub (olczku)',
    description: '',
    commands: [],
    permissions: []
  };

  if (!fs.existsSync(folderPath) || !fs.statSync(folderPath).isDirectory()) return info;

  const candidates = [
    path.join(folderPath, 'src', 'main', 'resources', 'plugin.yml'),
    path.join(folderPath, 'target', 'classes', 'plugin.yml'),
    path.join(folderPath, 'plugin.yml')
  ];

  let pluginYmlContent = null;
  for (const c of candidates) {
    if (fs.existsSync(c)) {
      try {
        pluginYmlContent = fs.readFileSync(c, 'utf8');
        break;
      } catch (e) {}
    }
  }

  if (pluginYmlContent) {
    const lines = pluginYmlContent.replace(/\r/g, '').split('\n');
    let inCommands = false;
    let inPermissions = false;
    let currCmd = null;

    for (const line of lines) {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith('#')) continue;

      const mName = line.match(/^name\s*:\s*['"]?([^'"]+)['"]?/i);
      const mVer = line.match(/^version\s*:\s*['"]?([^'"]+)['"]?/i);
      const mApiVer = line.match(/^api-version\s*:\s*['"]?([^'"]+)['"]?/i);
      const mMain = line.match(/^main\s*:\s*['"]?([^'"]+)['"]?/i);
      const mAuthor = line.match(/^author\s*:\s*['"]?([^'"]+)['"]?/i);
      const mDesc = line.match(/^description\s*:\s*['"]?([^'"]+)['"]?/i);

      if (mName) info.pluginName = mName[1].trim();
      if (mVer) info.version = mVer[1].trim();
      if (mApiVer) info.apiVersion = mApiVer[1].trim();
      if (mMain) info.mainClass = mMain[1].trim();
      if (mAuthor) info.author = mAuthor[1].trim();
      if (mDesc) info.description = mDesc[1].trim();

      if (/^commands\s*:/i.test(line)) {
        inCommands = true;
        inPermissions = false;
        continue;
      }
      if (/^permissions\s*:/i.test(line)) {
        inCommands = false;
        inPermissions = true;
        continue;
      }

      if (inCommands) {
        const cmdMatch = line.match(/^ {2}([a-zA-Z0-9_\-]+)\s*:/);
        if (cmdMatch) {
          currCmd = { name: cmdMatch[1], desc: '', usage: '', perm: '' };
          info.commands.push(currCmd);
        } else if (currCmd) {
          const mCDesc = line.match(/^ {4}description\s*:\s*['"]?([^'"]+)['"]?/i);
          const mCUsage = line.match(/^ {4}usage\s*:\s*['"]?([^'"]+)['"]?/i);
          const mCPerm = line.match(/^ {4}permission\s*:\s*['"]?([^'"]+)['"]?/i);
          if (mCDesc) currCmd.desc = mCDesc[1].trim();
          if (mCUsage) currCmd.usage = mCUsage[1].trim();
          if (mCPerm) currCmd.perm = mCPerm[1].trim();
          else if (/^[a-zA-Z0-9_\-]+:/.test(line)) {
            inCommands = false;
          }
        }
      }

      if (inPermissions) {
        const permMatch = line.match(/^ {2}([a-zA-Z0-9_\-\.]+)\s*:/);
        if (permMatch) {
          info.permissions.push(permMatch[1]);
        } else if (/^[a-zA-Z0-9_\-]+:/.test(line)) {
          inPermissions = false;
        }
      }
    }
  }

  return info;
}

function getProjectFullData(projectName) {
  const inspected = inspectProjectFiles(projectName);
  const opisFile = path.join(OPISY_DIR, projectName + '.txt');

  const smart = generateSmartDescription(projectName, inspected.commands, inspected.apiVersion);
  let type = smart.type;
  let description = smart.desc;
  let tags = smart.tags;

  if (!fs.existsSync(opisFile)) {
    const template = `typ: ${type}\nopis: ${description}\ntagi: ${tags.join(', ')}\n`;
    try {
      fs.writeFileSync(opisFile, template, 'utf8');
    } catch (e) {}
  } else {
    try {
      const content = fs.readFileSync(opisFile, 'utf8');
      const lines = content.replace(/\r/g, '').split('\n');
      const customDesc = [];

      for (const line of lines) {
        const trimmed = line.trim();
        if (!trimmed) continue;

        const matchTyp = trimmed.match(/^typ\s*:\s*(.+)$/i);
        const matchOpis = trimmed.match(/^opis\s*:\s*(.+)$/i);
        const matchTagi = trimmed.match(/^tagi\s*:\s*(.+)$/i);

        if (matchTyp) {
          type = matchTyp[1].trim();
        } else if (matchOpis) {
          customDesc.push(matchOpis[1].trim());
        } else if (matchTagi) {
          tags = matchTagi[1].split(',').map(t => t.trim()).filter(Boolean);
        } else {
          customDesc.push(trimmed);
        }
      }

      if (customDesc.length > 0) {
        const parsed = customDesc.join('\n');
        if (parsed !== 'Plugin / projekt bez opisu.' && parsed !== 'Dedykowany plugin serwerowy zoptymalizowany pod wysokie obciążenie.') {
          description = parsed;
        }
      }
    } catch (e) {}
  }

  // Obrazek
  let image = null;
  const folderPath = path.join(PLUGINS_DIR, projectName);
  if (fs.existsSync(folderPath) && fs.statSync(folderPath).isDirectory()) {
    for (const candidate of ['image.webp', 'image.png', 'image.jpg', 'image.jpeg']) {
      if (fs.existsSync(path.join(folderPath, candidate))) {
        image = 'plugins/' + encodeURIComponent(projectName) + '/' + candidate;
        break;
      }
    }
  }

  if (!image) {
    for (const candidate of [`${projectName}.webp`, `${projectName}.png`, `${projectName}.jpg`]) {
      if (fs.existsSync(path.join(OPISY_DIR, candidate))) {
        image = 'opisy/' + encodeURIComponent(candidate);
        break;
      }
    }
  }

  return {
    name: projectName,
    pluginName: inspected.pluginName || projectName,
    type: type,
    description: description,
    tags: tags,
    image: image,
    version: inspected.version,
    apiVersion: inspected.apiVersion,
    mainClass: inspected.mainClass,
    author: inspected.author,
    commands: inspected.commands,
    permissions: inspected.permissions
  };
}

function getProjects() {
  const detectedProjects = new Set();

  if (fs.existsSync(PLUGINS_DIR)) {
    const items = fs.readdirSync(PLUGINS_DIR);
    for (const item of items) {
      if (item.startsWith('.')) continue;
      const fullPath = path.join(PLUGINS_DIR, item);
      let projectName = item;

      if (fs.statSync(fullPath).isFile()) {
        const ext = path.extname(item).toLowerCase();
        if (['.png', '.jpg', '.jpeg', '.webp', '.txt'].includes(ext)) continue;
        projectName = path.basename(item, ext);
      }
      detectedProjects.add(projectName);
    }
  }

  if (fs.existsSync(OPISY_DIR)) {
    const items = fs.readdirSync(OPISY_DIR);
    for (const item of items) {
      if (item.startsWith('.')) continue;
      const ext = path.extname(item).toLowerCase();
      if (ext === '.txt') {
        const projectName = path.basename(item, ext);
        detectedProjects.add(projectName);
      }
    }
  }

  const projects = Array.from(detectedProjects).map(pName => getProjectFullData(pName));
  projects.sort((a, b) => a.name.localeCompare(b.name, undefined, { sensitivity: 'base' }));
  return projects;
}

function getLiveStats() {
  const now = Math.floor(Date.now() / 1000);
  const activeThreshold = 1200; // 20 mins
  let activeServers = 0;
  let totalPlayers = 0;
  const pluginCounts = {};
  const uniqueServerIds = new Set();

  try {
    if (fs.existsSync(SERVERS_FILE)) {
      const data = JSON.parse(fs.readFileSync(SERVERS_FILE, 'utf8'));
      if (data && data.servers) {
        for (const key in data.servers) {
          const info = data.servers[key];
          if (now - (info.lastPing || 0) <= activeThreshold) {
            const pName = info.plugin || 'Unknown';
            pluginCounts[pName] = (pluginCounts[pName] || 0) + 1;
            uniqueServerIds.add(info.serverId || pName);
            totalPlayers += parseInt(info.players || 0, 10);
          }
        }
        activeServers = uniqueServerIds.size;
      }
    }
  } catch (e) {}

  return { activeServers, totalPlayers, pluginCounts, timestamp: now };
}

function saveHeartbeat(body) {
  const now = Math.floor(Date.now() / 1000);
  let db = { servers: {} };
  try {
    if (fs.existsSync(SERVERS_FILE)) {
      db = JSON.parse(fs.readFileSync(SERVERS_FILE, 'utf8')) || { servers: {} };
    }
  } catch (e) {
    db = { servers: {} };
  }

  const pluginName = (body.plugin || 'Unknown').replace(/[^a-zA-Z0-9_\-\.]/g, '');
  const serverId = (body.server_id || 'anon').toString().substring(0, 32);
  const key = `${pluginName}@${serverId}`;

  db.servers[key] = {
    plugin: pluginName,
    serverId: serverId,
    players: Math.max(0, parseInt(body.players || 0, 10)),
    serverVersion: (body.server_version || 'Unknown').toString().substring(0, 64),
    pluginVersion: (body.plugin_version || '1.0.0').toString().substring(0, 32),
    lastPing: now
  };

  for (const k in db.servers) {
    if (now - db.servers[k].lastPing > 86400) {
      delete db.servers[k];
    }
  }

  fs.writeFileSync(SERVERS_FILE, JSON.stringify(db, null, 2), 'utf8');
}

function renderHtml(projects, stats) {
  const totalProjects = projects.length;
  const year = new Date().getFullYear();

  let cardsHtml = '';
  projects.forEach((project, idx) => {
    const searchData = escapeHtml((project.name + ' ' + project.type + ' ' + project.description + ' ' + (project.tags || []).join(' ')).toLowerCase());
    const safeName = escapeHtml(project.name);
    const safeType = escapeHtml(project.type.toUpperCase());
    const safeDesc = escapeHtml(project.description).replace(/\n/g, '<br>');
    const pCount = stats.pluginCounts[project.name] || 0;
    const badgeStyle = pCount > 0 ? '' : 'display:none;';

    const isBot = project.type.toLowerCase().includes('bot');
    const isSystem = project.type.toLowerCase().includes('system');
    const pillClass = isBot ? 'bot-pill' : (isSystem ? 'system-pill' : '');

    const imgTag = project.image 
      ? `<img class="project-card-img" src="${escapeHtml(project.image)}" alt="${safeName}" loading="lazy">` 
      : '';

    const tagsHtml = project.tags && project.tags.length > 0
      ? `<div class="project-tags">${project.tags.map(t => `<span class="project-tag">${escapeHtml(t)}</span>`).join('')}</div>`
      : '';

    cardsHtml += `
      <article class="project-card" data-project="${safeName}" data-search="${searchData}">
        ${imgTag}
        <div class="project-card-top">
          <span class="project-type-pill ${pillClass}">
            <span class="dot"></span> ${safeType}
          </span>
          <span class="plugin-live-badge" id="badge-${safeName}" style="${badgeStyle}">
            <span class="live-dot"></span> <span class="badge-num">${pCount}</span> serwerów
          </span>
        </div>
        <h3>${safeName}</h3>
        <p>${safeDesc}</p>
        ${tagsHtml}
        <div class="project-card-footer">
          <button type="button" class="project-btn-details" onclick="openDetailsModal(${idx})">
            🔍 Szczegóły
          </button>
          <a href="#contact" class="project-action-btn">Zamów podobny →</a>
        </div>
      </article>`;
  });

  const emptyMsg = projects.length === 0 
    ? '<div class="empty-state">Brak wykrytych projektów. Wrzuć folder lub plik do katalogu <code>/plugins/</code>.</div>' 
    : '';

  const projectsJson = JSON.stringify(projects).replace(/</g, '\\u003c');

  return `<!doctype html>
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
      <a href="#projects">Realizacje (${totalProjects})</a>
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
        <span id="statActiveServers">${stats.activeServers}</span>
      </div>
      <div class="stat-label">Aktywne serwery</div>
    </div>
    <div class="stat-item">
      <div class="stat-number"><span class="highlight">24/7</span></div>
      <div class="stat-label">Wsparcie techniczne</div>
    </div>
    <div class="stat-item">
      <div class="stat-number"><span class="highlight">${totalProjects}</span>+</div>
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
      ${cardsHtml}
      ${emptyMsg}
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
      © ${year} <strong>Jakub (olczku)</strong> · Plugin Developer
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
const projectsData = ${projectsJson};

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
    cmdList.innerHTML = p.commands.map(c => \`
      <div class="modal-cmd-item">
        <div class="modal-cmd-head">
          <span class="modal-cmd-name">/\${escapeHtml(c.name)}</span>
          \${c.usage ? \`<span class="project-tag">\${escapeHtml(c.usage)}</span>\` : ''}
        </div>
        \${c.desc ? \`<div class="modal-cmd-desc">\${escapeHtml(c.desc)}</div>\` : ''}
        \${c.perm ? \`<div class="modal-cmd-perm">Uprawnienie: \${escapeHtml(c.perm)}</div>\` : ''}
      </div>
    \`).join('');
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
</html>`;
}

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.webp': 'image/webp',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
};

const server = http.createServer((req, res) => {
  const urlParts = req.url.split('?');
  const urlPath = decodeURIComponent(urlParts[0]);

  // API Stats Endpoint
  if (urlPath === '/api/stats' || urlPath === '/api/stats.php') {
    const stats = getLiveStats();
    res.writeHead(200, { 
      'Content-Type': 'application/json; charset=utf-8',
      'Access-Control-Allow-Origin': '*'
    });
    res.end(JSON.stringify(stats));
    return;
  }

  // API Heartbeat Endpoint
  if (urlPath === '/api/heartbeat' || urlPath === '/api/heartbeat.php') {
    if (req.method === 'OPTIONS') {
      res.writeHead(200, {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'POST, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type'
      });
      res.end();
      return;
    }

    if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => { body += chunk; });
      req.on('end', () => {
        try {
          const parsed = JSON.parse(body);
          if (parsed && parsed.plugin && parsed.server_id) {
            saveHeartbeat(parsed);
            res.writeHead(200, { 
              'Content-Type': 'application/json; charset=utf-8',
              'Access-Control-Allow-Origin': '*'
            });
            res.end(JSON.stringify({ status: 'ok' }));
            return;
          }
        } catch (e) {}
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Invalid payload' }));
      });
      return;
    }
  }

  // Main Page
  if (urlPath === '/' || urlPath === '/index.html' || urlPath === '/index.php') {
    const projects = getProjects();
    const stats = getLiveStats();
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(renderHtml(projects, stats));
    return;
  }

  // Static Assets
  const filePath = path.join(ROOT_DIR, urlPath.replace(/^\//, ''));
  if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';
    res.writeHead(200, { 'Content-Type': contentType });
    fs.createReadStream(filePath).pipe(res);
    return;
  }

  res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
  res.end('404 Not Found');
});

server.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});
