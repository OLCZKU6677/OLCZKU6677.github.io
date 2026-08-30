/**
 * Discord PV (Direct Message) Notifier — Jakub (olczku_)
 */

const BOT_TOKEN = "MTU0MzYyNTMxMzE1Njg2MjA2NA.G5UMc-.KqmVMNngqWRMvMDqKXUO86P-frFUg2OSe357Ik";
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

async function discordRequest(endpoint, method = "GET", body = null) {
  const options = {
    method,
    headers: {
      "Authorization": `Bot ${BOT_TOKEN}`,
      "Content-Type": "application/json"
    }
  };
  if (body) options.body = JSON.stringify(body);

  const res = await fetch(`https://discord.com/api/v10${endpoint}`, options);
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(`Discord API Error [${res.status}]: ${JSON.stringify(err)}`);
  }
  return res.json();
}

async function sendDirectMessageToUser(userId, embedData) {
  console.log(`[Bot] Tworzenie kanału PV z użytkownikiem ID: ${userId}...`);
  // 1. Otwórz kanał DM
  const dmChannel = await discordRequest("/users/@me/channels", "POST", {
    recipient_id: userId
  });

  console.log(`[Bot] Wysyłanie powiadomienia na PV (kanał ${dmChannel.id})...`);
  // 2. Wyślij wiadomość
  return discordRequest(`/channels/${dmChannel.id}/messages`, "POST", {
    embeds: [embedData]
  });
}

async function findOlczkuAndTest(userIdOverride = null) {
  try {
    let targetUserId = userIdOverride;

    if (!targetUserId) {
      console.log("[Bot] Pobieranie listy serwerów bota...");
      const guilds = await discordRequest("/users/@me/guilds");
      
      if (!guilds || guilds.length === 0) {
        console.log("\n❌ Bot nie jest jeszcze dodany do żadnego serwera Discord!");
        console.log("Aby bot mógł pisać do Ciebie na PV (zgodnie z zasadami bezpieczeństwa Discorda), musisz go dodać do serwera, na którym jesteś.");
        console.log("👉 Kliknij ten link i dodaj bota: https://discord.com/oauth2/authorize?client_id=1543625313156862064&permissions=2048&scope=bot\n");
        return;
      }

      console.log(`[Bot] Znaleziono ${guilds.length} serwer(ów). Szukanie użytkownika 'olczku_'...`);
      for (const g of guilds) {
        try {
          const members = await discordRequest(`/guilds/${g.id}/members?limit=1000`);
          for (const m of members) {
            const username = m.user.username ? m.user.username.toLowerCase() : "";
            const globalName = m.user.global_name ? m.user.global_name.toLowerCase() : "";
            if (username === "olczku_" || username === "olczku" || globalName.includes("olczku")) {
              targetUserId = m.user.id;
              console.log(`✅ Znaleziono Twoje ID: ${targetUserId} (${m.user.username})!`);
              break;
            }
          }
        } catch (e) {}
        if (targetUserId) break;
      }
    }

    if (!targetUserId) {
      console.log("❌ Nie udało się automatycznie znaleźć użytkownika olczku_. Podaj bezpośrednio swoje ID użytkownika z Discorda.");
      return;
    }

    const testEmbed = {
      title: "👀 Testowe powiadomienie z Twojego portfolio!",
      url: "https://olczku6677.github.io",
      color: 0x00f5a0,
      description: "Cześć **Jakub (olczku_)**! Twój bot został pomyślnie skonfigurowany i będzie wysyłał Ci powiadomienia na PV o każdym odwiedzającym Twoją stronę.",
      fields: [
        { name: "📍 Przykładowa lokalizacja", value: "🏙️ **Warszawa**, Polska 🇵🇱", inline: true },
        { name: "🌐 Adres IP", value: "`188.146.12.34`", inline: true },
        { name: "💻 Urządzenie", value: "🖥️ Windows 11 PC (1920x1080)", inline: true },
        { name: "🧭 Przeglądarka", value: "Google Chrome", inline: true },
        { name: "🕒 Czas", value: new Date().toLocaleString('pl-PL', { timeZone: 'Europe/Warsaw' }), inline: false }
      ],
      footer: {
        text: "Portfolio Tracker • Jakub (olczku)"
      }
    };

    await sendDirectMessageToUser(targetUserId, testEmbed);
    console.log("🎉 SUKCES! Wiadomość testowa została wysłana na Twoje PV na Discordzie!");

  } catch (err) {
    console.error("Błąd:", err.message);
  }
}

const inputId = process.argv[2];
findOlczkuAndTest(inputId);
