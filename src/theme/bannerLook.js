// How a banner looks and moves, shared by the site view (Announcements.svelte) and the panel preview, so
// both always draw the same thing. The options come from the announcement's `displayOptions` JSON (the backend keeps
// only known keys, see AnnouncementDisplayOptions.kt); anything missing takes the default below.

export const SEPARATORS = {
  DOT: '•',
  SPARKLE: '✦',
  STAR: '★',
  DIAMOND: '◆',
  PIPE: '|',
  DASH: '—',
  ARROW: '»',
};

/** Ready-made gradients: [from, to]. Dark enough at both ends for white text. */
export const GRADIENTS = {
  OCEAN: ['#2563eb', '#0891b2'],
  BERRY: ['#db2777', '#7c3aed'],
  SUNSET: ['#ea580c', '#db2777'],
  FOREST: ['#15803d', '#0e7490'],
  CORAL: ['#e11d48', '#f97316'],
  AURORA: ['#059669', '#7c3aed'],
  NIGHT: ['#1e293b', '#4338ca'],
};

export const DEFAULTS = {
  marqueeSpeed: 60, // px per second
  rotateSeconds: 5,
  multiMode: 'ROTATE', // several contents: ROTATE (one after the other) or INLINE (side by side)
  // Marquee with several contents: the line starts filled (side by side: an endless loop with no gap; one after the
  // other: each text starts in view) instead of entering from the right edge.
  fullStart: false,
  separator: 'SPARKLE',
  customSeparator: '',
  background: 'ALERT', // ALERT | THEME_SOFT | THEME_SOLID | THEME_GRADIENT | GRADIENT | CUSTOM
  gradient: 'OCEAN',
  gradientFrom: '#2563eb',
  gradientTo: '#7c3aed',
  gradientAngle: 90,
  backgroundColor: '#1e293b',
  textColor: 'AUTO', // AUTO | CUSTOM
  textColorValue: '#ffffff',
};

/** The options of an announcement (`displayOptions` as JSON text, an object or nothing) merged over the defaults. */
export function parseOptions(raw) {
  let value = raw;
  if (typeof raw === 'string') {
    try {
      value = JSON.parse(raw);
    } catch {
      value = null;
    }
  }
  return { ...DEFAULTS, ...(value && typeof value === 'object' ? value : {}) };
}

export function separatorText(options) {
  if (options.separator === 'CUSTOM') return options.customSeparator?.trim() || SEPARATORS.SPARKLE;
  return SEPARATORS[options.separator] ?? SEPARATORS.SPARKLE;
}

function luminance(hex) {
  const m = /^#([0-9a-f]{6})$/i.exec(hex || '');
  if (!m) return 0;
  const [r, g, b] = [0, 2, 4].map((i) => {
    const c = parseInt(m[1].slice(i, i + 2), 16) / 255;
    return c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4;
  });
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

/** Black or white, whichever reads better on `hex`. */
export function contrastText(hex) {
  return luminance(hex) > 0.4 ? '#111827' : '#ffffff';
}

const HEX = /^#[0-9a-f]{6}$/i;

/** Bootstrap's link colour variables for a text colour, so links in the banner take that colour too. */
function linkVars(text) {
  const rgb = HEX.test(text)
    ? [1, 3, 5].map((i) => parseInt(text.slice(i, i + 2), 16)).join(', ')
    : 'var(--bs-body-color-rgb)';
  return `--bs-link-color-rgb: ${rgb}; --bs-link-hover-color-rgb: ${rgb};`;
}

/**
 * The banner's colours: `alert` true keeps Bootstrap's alert class (the classic look); otherwise `style` is the inline
 * style and `lightText` says the text is light (the close button then turns white).
 */
export function bannerLook(options) {
  const o = options;
  const angle = Number.isFinite(o.gradientAngle) ? o.gradientAngle : DEFAULTS.gradientAngle;
  let background = null;
  let border = 'transparent';
  let text = '#ffffff';
  let shadow = true;

  switch (o.background) {
    case 'THEME_SOFT':
      // A light tint of the theme's main colour over the page background: fits light and dark themes.
      background = 'color-mix(in srgb, var(--bs-primary) 16%, var(--bs-body-bg))';
      border = 'color-mix(in srgb, var(--bs-primary) 40%, transparent)';
      text = 'var(--bs-body-color)';
      shadow = false;
      break;
    case 'THEME_SOLID':
      background = 'var(--bs-primary)';
      break;
    case 'THEME_GRADIENT':
      // The theme colour to a darker, hue-shifted version of it; browsers without relative colours get the darker one.
      background = [
        `linear-gradient(${angle}deg, var(--bs-primary), color-mix(in srgb, var(--bs-primary) 60%, #000))`,
        `linear-gradient(${angle}deg, var(--bs-primary), oklch(from var(--bs-primary) calc(l - 0.1) c calc(h + 50)))`,
      ];
      break;
    case 'GRADIENT': {
      const [from, to] =
        o.gradient === 'CUSTOM'
          ? [HEX.test(o.gradientFrom) ? o.gradientFrom : DEFAULTS.gradientFrom, HEX.test(o.gradientTo) ? o.gradientTo : DEFAULTS.gradientTo]
          : (GRADIENTS[o.gradient] ?? GRADIENTS.OCEAN);
      background = `linear-gradient(${angle}deg, ${from}, ${to})`;
      // A custom gradient may be light: dark text then.
      if (o.gradient === 'CUSTOM' && (luminance(from) + luminance(to)) / 2 > 0.4) text = '#111827';
      break;
    }
    case 'CUSTOM': {
      const color = HEX.test(o.backgroundColor) ? o.backgroundColor : DEFAULTS.backgroundColor;
      background = color;
      text = contrastText(color);
      break;
    }
    default:
      background = null;
  }

  const customText = o.textColor === 'CUSTOM' && HEX.test(o.textColorValue) ? o.textColorValue : null;

  if (!background) {
    // The classic alert colours; only a custom text colour is laid over them.
    return {
      alert: true,
      style: customText ? `--ann-text: ${customText}; color: ${customText}; ${linkVars(customText)}` : '',
      lightText: customText ? luminance(customText) > 0.4 : false,
    };
  }

  const finalText = customText ?? text;
  const lightText = customText ? luminance(customText) > 0.4 : finalText === '#ffffff';
  const backgrounds = Array.isArray(background) ? background : [background];

  return {
    alert: false,
    style: [
      ...backgrounds.map((b) => `background: ${b};`),
      `border-color: ${border};`,
      `--ann-text: ${finalText};`,
      `color: ${finalText};`,
      linkVars(finalText),
      shadow && lightText ? 'text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);' : '',
    ].join(' '),
    lightText,
  };
}
