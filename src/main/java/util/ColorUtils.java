package util;

import com.hypixel.hytale.server.core.Message;
import java.awt.*;

public class ColorUtils {

    private static final Color[] MINECRAFT_COLORS = {
            new Color(0x000000), // 0
            new Color(0x0000AA), // 1
            new Color(0x00AA00), // 2
            new Color(0x00AAAA), // 3
            new Color(0xAA0000), // 4
            new Color(0xAA00AA), // 5
            new Color(0xFFAA00), // 6
            new Color(0xAAAAAA), // 7
            new Color(0x555555), // 8
            new Color(0x5555FF), // 9
            new Color(0x55FF55), // a
            new Color(0x55FFFF), // b
            new Color(0xFF5555), // c
            new Color(0xFF55FF), // d
            new Color(0xFFFF55), // e
            new Color(0xFFFFFF)  // f
    };

    private static Color getColorFromChar(char c) {
        int index = Character.digit(c, 16);

        if (index >= 0 && index < 16) {
            return MINECRAFT_COLORS[index];
        }
        return null;
    }

    private static boolean isFormatPrefix(char ch) {
        return ch == '&' || ch == '§';
    }

    private static Message segment(String s, Color color, boolean bold, boolean italic) {
        Message m = Message.raw(s);
        if (color != null) m = m.color(color);
        m = m.bold(bold);
        m = m.italic(italic);
        return m;
    }

    public static Message parseColorCodes(String text) {
        return parseColorCodes(text, true);
    }

    public static Message parseColorCodes(String text, boolean includeLinks) {
        if (text == null || text.isEmpty()) return Message.empty();

        // strip surrounding quotes
        if (text.length() >= 2) {
            if ((text.startsWith("'") && text.endsWith("'")) ||
                    (text.startsWith("\"") && text.endsWith("\""))) {
                text = text.substring(1, text.length() - 1);
            }
        }

        // fast path if no formatting needed
        if (!text.contains("&") && !text.contains("§") && (!includeLinks || !text.contains("["))) {
            return segment(text, Color.WHITE, false, false);
        }

        Message root = Message.empty();
        StringBuilder buf = new StringBuilder();

        Color currentColor = Color.WHITE;
        boolean isBold = false;
        boolean isItalic = false;

        int len = text.length();
        for (int i = 0; i < len; i++) {
            char ch = text.charAt(i);

            // check for markdown link pattern [text](url)
            if (includeLinks && ch == '[') {
                int closeBracket = text.indexOf(']', i);
                if (closeBracket > i) {
                    if (closeBracket + 1 < len && text.charAt(closeBracket + 1) == '(') {
                        int closeParen = text.indexOf(')', closeBracket + 1);
                        if (closeParen > closeBracket + 1) {

                            // flush buffer before link
                            if (!buf.isEmpty()) {
                                root = Message.join(root, segment(buf.toString(), currentColor, isBold, isItalic));
                                buf.setLength(0);
                            }

                            String linkText = text.substring(i + 1, closeBracket);
                            String url = text.substring(closeBracket + 2, closeParen);

                            // recursive parse for link text
                            Message linkMessage = parseColorCodes(linkText, true);

                            // apply link action and join
                            linkMessage = linkMessage.link(url);
                            root = Message.join(root, linkMessage);

                            i = closeParen;
                            continue;
                        }
                    }
                }
            }

            // handle color codes
            if (isFormatPrefix(ch) && i + 1 < len) {
                char code = text.charAt(i + 1);

                // flush current buffer
                if (!buf.isEmpty()) {
                    root = Message.join(root, segment(buf.toString(), currentColor, isBold, isItalic));
                    buf.setLength(0);
                }

                char lower = Character.toLowerCase(code);
                if (lower == 'r') {
                    currentColor = Color.WHITE;
                    isBold = false;
                    isItalic = false;
                } else if (lower == 'l') {
                    isBold = true;
                } else if (lower == 'o') {
                    isItalic = true;
                } else {
                    Color newColor = getColorFromChar(lower);
                    if (newColor != null) {
                        currentColor = newColor;
                    } else {
                        buf.append(ch).append(code);
                    }
                }

                i++;
                continue;
            }

            buf.append(ch);
        }

        // flush remaining text
        if (!buf.isEmpty()) {
            root = Message.join(root, segment(buf.toString(), currentColor, isBold, isItalic));
        }

        return root;
    }
}