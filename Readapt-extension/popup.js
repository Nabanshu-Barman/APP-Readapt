// popup.js -- dyslexia-only, exact presets, and "Preset" label/value on left, with slider/label reflecting last applied preset

const dom = {
  lastUpdated: document.getElementById("lastUpdated"),
  settingsDump: document.getElementById("settingsDump"),
  noSettingsMsg: document.getElementById("noSettingsMsg"),
  modeBadge: document.getElementById("modeBadge"),
  overlayRadio: document.getElementById("overlayMode"),
  inlineRadio: document.getElementById("inlineMode"),
  inlineModeLabel: document.getElementById("inlineModeLabel"),
  modeSection: document.getElementById("modeSection"),
  presetSlider: document.getElementById("presetSlider"),
  presetLabel: document.getElementById("presetLabel")
};

// Exact preset settings as provided by user
const PRESETS = {
  "1":   { "mode": "dyslexia", "source": "preset", "preset": 1,    "fontSize": 24,   "fontFamily": "system-ui, sans-serif", "letterSpacing": 0.16, "wordSpacing": 0.22,   "lineSpacing": 1.95,   "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "1.25":{ "mode": "dyslexia", "source": "preset", "preset": 1.25, "fontSize": 24.75,"fontFamily": "system-ui, sans-serif", "letterSpacing": 0.2,  "wordSpacing": 0.2775, "lineSpacing": 2.025,  "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "1.5": { "mode": "dyslexia", "source": "preset", "preset": 1.5,  "fontSize": 25.5, "fontFamily": "system-ui, sans-serif", "letterSpacing": 0.24, "wordSpacing": 0.335,  "lineSpacing": 2.1,    "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "1.75":{ "mode": "dyslexia", "source": "preset", "preset": 1.75, "fontSize": 26.25,"fontFamily": "system-ui, sans-serif", "letterSpacing": 0.28, "wordSpacing": 0.3925, "lineSpacing": 2.175,  "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "2":   { "mode": "dyslexia", "source": "preset", "preset": 2,    "fontSize": 27,   "fontFamily": "system-ui, sans-serif", "letterSpacing": 0.32, "wordSpacing": 0.45,   "lineSpacing": 2.25,   "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "2.25":{ "mode": "dyslexia", "source": "preset", "preset": 2.25, "fontSize": 28,   "fontFamily": "system-ui, sans-serif", "letterSpacing": 0.3775,"wordSpacing":0.55,   "lineSpacing": 2.3375, "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "2.5": { "mode": "dyslexia", "source": "preset", "preset": 2.5,  "fontSize": 29,   "fontFamily": "system-ui, sans-serif", "letterSpacing": 0.435,"wordSpacing": 0.65,   "lineSpacing": 2.425,  "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "2.75":{ "mode": "dyslexia", "source": "preset", "preset": 2.75, "fontSize": 30,   "fontFamily": "system-ui, sans-serif", "letterSpacing": 0.4925,"wordSpacing":0.75,   "lineSpacing": 2.5125, "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": false },
  "3":   { "mode": "dyslexia", "source": "preset", "preset": 3,    "fontSize": 31,   "fontFamily": "system-ui, sans-serif", "letterSpacing": 0.55, "wordSpacing": 0.85,   "lineSpacing": 2.6,    "contrast": { "fg": "#111", "bg": "#fff" }, "dyslexiaHighlights": true },
};

// Only allow these steps
const PRESET_VALUES = Object.keys(PRESETS).map(Number).sort((a,b)=>a-b);

function fmtTime(ts) {
  if (!ts) return "—";
  try {
    const d = new Date(ts);
    return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  } catch {
    return "—";
  }
}

function loadConfig(cb) {
  chrome.runtime.sendMessage({ type: "READAPT_GET_SETTINGS" }, data => {
    cb(data || {});
  });
}

function render(data) {
  const settings = data.readaptSettings;
  const cfg = data.readaptConfig || { mode: "overlay" };

  dom.overlayRadio.checked = cfg.mode === "overlay";
  dom.inlineRadio.checked = cfg.mode === "inline";

  dom.settingsDump.style.display = settings ? "block" : "none";
  dom.noSettingsMsg.style.display = settings ? "none" : "block";

  dom.modeBadge.style.display = "inline-flex";
  dom.modeBadge.textContent = "DYSLEXIA";

  dom.inlineModeLabel.textContent = "DYSLEXIA";
  dom.inlineModeLabel.dataset.visible = "true";

  dom.lastUpdated.textContent = fmtTime(settings?.timestamp);

  if (settings) {
    dom.settingsDump.textContent = JSON.stringify({
      preset: settings.preset,
      fontSize: settings.fontSize,
      fontFamily: settings.fontFamily,
      letterSpacing: settings.letterSpacing,
      wordSpacing: settings.wordSpacing,
      lineSpacing: settings.lineSpacing,
      contrast: settings.contrast,
      dyslexiaHighlights: settings.dyslexiaHighlights
    }, null, 2);
  } else {
    dom.settingsDump.textContent = "No settings cached.";
  }
}

function saveMode(mode) {
  chrome.runtime.sendMessage({
    type: "READAPT_SET_CONFIG",
    config: { mode }
  });
}

function savePreset(presetValue) {
  // Use only allowed preset values
  presetValue = clampPreset(presetValue);
  const presetStr = String(presetValue);
  const settings = { ...PRESETS[presetStr], timestamp: Date.now() };
  chrome.runtime.sendMessage({ type: "READAPT_SAVE_SETTINGS", payload: settings }, () => {
    loadConfig(render);
  });
}

// Clamp and snap to nearest allowed preset value
function clampPreset(val) {
  // Snap to nearest step
  let snapped = PRESET_VALUES[0];
  let minDelta = Math.abs(val - PRESET_VALUES[0]);
  for (let i = 1; i < PRESET_VALUES.length; ++i) {
    const d = Math.abs(val - PRESET_VALUES[i]);
    if (d < minDelta) {
      snapped = PRESET_VALUES[i];
      minDelta = d;
    }
  }
  return snapped;
}

function wireEvents() {
  dom.overlayRadio.addEventListener("change", () => {
    if (dom.overlayRadio.checked) saveMode("overlay");
  });
  dom.inlineRadio.addEventListener("change", () => {
    if (dom.inlineRadio.checked) saveMode("inline");
  });

  dom.presetSlider.addEventListener("input", (e) => {
    let val = parseFloat(e.target.value);
    val = clampPreset(val);
    dom.presetSlider.value = val; // Snap slider to nearest allowed step
    dom.presetLabel.textContent = `Preset ${val.toFixed(2)}`;
    savePreset(val);
  });
}

function init() {
  // On load, get the last used preset and set the slider and label accordingly
  loadConfig((data) => {
    let preset = 2;
    if (data && data.readaptSettings && typeof data.readaptSettings.preset !== "undefined") {
      preset = Number(data.readaptSettings.preset);
    }
    preset = clampPreset(preset);
    dom.presetSlider.value = preset;
    dom.presetLabel.textContent = `Preset ${preset.toFixed(2)}`;
    wireEvents();
    render(data);
  });
}

init();