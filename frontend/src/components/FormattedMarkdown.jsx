import React from 'react';

// Comprehensive, lightweight, safe Markdown renderer for BIS Assistant responses
export function FormattedMarkdown({ content }) {
  if (!content) return null;

  // Pre-process lines
  const lines = content.split('\n');
  const elements = [];
  let i = 0;

  const parseInline = (text) => {
    if (!text) return '';

    // First handle links [text](url)
    const linkRegex = /\[([^\]]+)\]\(([^)]+)\)/g;
    const partsWithLinks = [];
    let lLast = 0;
    let lMatch;
    while ((lMatch = linkRegex.exec(text)) !== null) {
      if (lMatch.index > lLast) {
        partsWithLinks.push(text.substring(lLast, lMatch.index));
      }
      partsWithLinks.push(
        <a
          key={`link-${lMatch.index}`}
          href={lMatch[2]}
          target="_blank"
          rel="noreferrer"
          style={{ color: 'var(--primary-600)', textDecoration: 'underline', fontWeight: 600 }}
        >
          {lMatch[1]}
        </a>
      );
      lLast = lMatch.index + lMatch[0].length;
    }
    if (lLast < text.length) {
      partsWithLinks.push(text.substring(lLast));
    }

    // Process bold and code inside each chunk
    return partsWithLinks.map((chunk, cIdx) => {
      if (typeof chunk !== 'string') return chunk;

      // Replace **bold** with <strong>
      const boldRegex = /\*\*(.*?)\*\*/g;
      const boldParts = [];
      let lastIndex = 0;
      let match;

      while ((match = boldRegex.exec(chunk)) !== null) {
        if (match.index > lastIndex) {
          boldParts.push(chunk.substring(lastIndex, match.index));
        }
        boldParts.push(
          <strong key={`bold-${cIdx}-${match.index}`} style={{ fontWeight: 700, color: 'var(--text-main)' }}>
            {match[1]}
          </strong>
        );
        lastIndex = match.index + match[0].length;
      }

      if (lastIndex < chunk.length) {
        boldParts.push(chunk.substring(lastIndex));
      }

      // Process inline code `code`
      return boldParts.map((part, pIdx) => {
        if (typeof part === 'string') {
          const codeRegex = /`([^`]+)`/g;
          const codeParts = [];
          let codeLast = 0;
          let codeMatch;
          while ((codeMatch = codeRegex.exec(part)) !== null) {
            if (codeMatch.index > codeLast) {
              codeParts.push(part.substring(codeLast, codeMatch.index));
            }
            codeParts.push(
              <code
                key={`code-${cIdx}-${pIdx}-${codeMatch.index}`}
                style={{
                  fontFamily: 'var(--font-mono)',
                  background: 'var(--bg-subtle)',
                  border: '1px solid var(--border-medium)',
                  padding: '0.15rem 0.4rem',
                  borderRadius: '4px',
                  fontSize: '0.86em',
                  color: 'var(--primary-700)'
                }}
              >
                {codeMatch[1]}
              </code>
            );
            codeLast = codeMatch.index + codeMatch[0].length;
          }
          if (codeLast < part.length) {
            codeParts.push(part.substring(codeLast));
          }
          return codeParts;
        }
        return part;
      });
    });
  };

  while (i < lines.length) {
    const line = lines[i];
    const trimmed = line.trim();

    if (!trimmed) {
      elements.push(<div key={`blank-${i}`} style={{ height: '0.35rem' }} />);
      i++;
      continue;
    }

    // Horizontal Rule: --- or ***
    if (trimmed === '---' || trimmed === '***' || trimmed === '___') {
      elements.push(
        <hr key={`hr-${i}`} style={{ border: 'none', borderTop: '1px solid var(--border-light)', margin: '0.75rem 0' }} />
      );
      i++;
      continue;
    }

    // Table detection: starts with | and contains |
    if (trimmed.startsWith('|') && trimmed.endsWith('|')) {
      const tableRows = [];
      let isHeader = true;
      
      while (i < lines.length && lines[i].trim().startsWith('|') && lines[i].trim().endsWith('|')) {
        const rowText = lines[i].trim();
        // Check if divider row like |---|---|
        if (/^\|[\s\-:|]+\|$/.test(rowText)) {
          isHeader = false;
          i++;
          continue;
        }

        const cells = rowText
          .slice(1, -1)
          .split('|')
          .map(cell => cell.trim());

        tableRows.push({ isHeader, cells });
        i++;
      }

      elements.push(
        <div key={`table-wrapper-${i}`} style={{ overflowX: 'auto', margin: '0.85rem 0', borderRadius: '8px', border: '1px solid var(--border-light)' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.88rem', background: 'var(--bg-surface)' }}>
            <tbody>
              {tableRows.map((r, rIdx) => (
                <tr
                  key={`tr-${rIdx}`}
                  style={{
                    background: r.isHeader ? 'var(--bg-subtle)' : rIdx % 2 === 0 ? 'var(--bg-surface)' : 'var(--bg-subtle)',
                    borderBottom: '1px solid var(--border-light)',
                    fontWeight: r.isHeader ? 700 : 400
                  }}
                >
                  {r.cells.map((cell, cIdx) => (
                    <td
                      key={`td-${rIdx}-${cIdx}`}
                      style={{
                        padding: '0.65rem 0.85rem',
                        borderRight: cIdx < r.cells.length - 1 ? '1px solid var(--border-light)' : 'none',
                        color: r.isHeader ? 'var(--primary-900)' : 'var(--text-main)',
                        textAlign: 'left'
                      }}
                    >
                      {parseInline(cell)}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      );
      continue;
    }

    // Heading 4: ####
    if (trimmed.startsWith('#### ')) {
      elements.push(
        <h5
          key={`h4-${i}`}
          style={{
            fontSize: '0.92rem',
            fontWeight: 800,
            color: 'var(--primary-700)',
            marginTop: '0.5rem',
            marginBottom: '0.15rem'
          }}
        >
          {parseInline(trimmed.substring(5))}
        </h5>
      );
      i++;
      continue;
    }

    // Heading 3: ###
    if (trimmed.startsWith('### ')) {
      elements.push(
        <h4
          key={`h3-${i}`}
          style={{
            fontSize: '1.05rem',
            fontWeight: 800,
            color: 'var(--text-main)',
            marginTop: '0.65rem',
            marginBottom: '0.2rem'
          }}
        >
          {parseInline(trimmed.substring(4))}
        </h4>
      );
      i++;
      continue;
    }

    // Heading 2: ##
    if (trimmed.startsWith('## ')) {
      elements.push(
        <h3
          key={`h2-${i}`}
          style={{
            fontSize: '1.18rem',
            fontWeight: 800,
            color: 'var(--primary-800)',
            marginTop: '0.85rem',
            marginBottom: '0.3rem'
          }}
        >
          {parseInline(trimmed.substring(3))}
        </h3>
      );
      i++;
      continue;
    }

    // Heading 1: #
    if (trimmed.startsWith('# ')) {
      elements.push(
        <h2
          key={`h1-${i}`}
          style={{
            fontSize: '1.3rem',
            fontWeight: 800,
            color: 'var(--primary-900)',
            marginTop: '1rem',
            marginBottom: '0.35rem'
          }}
        >
          {parseInline(trimmed.substring(2))}
        </h2>
      );
      i++;
      continue;
    }

    // Bullet point: - or *
    if (trimmed.startsWith('- ') || trimmed.startsWith('* ')) {
      elements.push(
        <div
          key={`bullet-${i}`}
          style={{
            display: 'flex',
            alignItems: 'flex-start',
            gap: '0.6rem',
            paddingLeft: '0.5rem',
            fontSize: '0.94rem'
          }}
        >
          <span style={{ color: 'var(--primary-600)', fontWeight: 'bold', fontSize: '1.1rem', lineHeight: 1 }}>•</span>
          <div style={{ flex: 1 }}>{parseInline(trimmed.substring(2))}</div>
        </div>
      );
      i++;
      continue;
    }

    // Numbered list: 1. or 2.
    const numMatch = trimmed.match(/^(\d+)\.\s+(.*)/);
    if (numMatch) {
      elements.push(
        <div
          key={`num-${i}`}
          style={{
            display: 'flex',
            alignItems: 'flex-start',
            gap: '0.6rem',
            paddingLeft: '0.5rem',
            fontSize: '0.94rem'
          }}
        >
          <span
            style={{
              fontFamily: 'var(--font-mono)',
              fontWeight: 700,
              color: 'var(--primary-700)',
              fontSize: '0.85rem',
              minWidth: '20px'
            }}
          >
            {numMatch[1]}.
          </span>
          <div style={{ flex: 1 }}>{parseInline(numMatch[2])}</div>
        </div>
      );
      i++;
      continue;
    }

    // Regular paragraph
    elements.push(
      <p key={`p-${i}`} style={{ fontSize: '0.95rem', color: 'var(--text-main)' }}>
        {parseInline(line)}
      </p>
    );
    i++;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', lineHeight: 1.65 }}>
      {elements}
    </div>
  );
}
