package util;

import com.hypixel.hytale.server.core.Message;

import java.awt.*;

public class ColorUtils {

    private static Color getColorFromChar(char c) {
        return switch (Character.toLowerCase(c)) {
            case '0' -> new Color(0x000000);
            case '1' -> new Color(0x0000AA);
            case '2' -> new Color(0x00AA00);
            case '3' -> new Color(0x00AAAA);
            case '4' -> new Color(0xAA0000);
            case '5' -> new Color(0xAA00AA);
            case '6' -> new Color(0xFFAA00);
            case '7' -> new Color(0xAAAAAA);
            case '8' -> new Color(0x555555);
            case '9' -> new Color(0x5555FF);
            case 'a' -> new Color(0x55FF55);
            case 'b' -> new Color(0x55FFFF);
            case 'c' -> new Color(0xFF5555);
            case 'd' -> new Color(0xFF55FF);
            case 'e' -> new Color(0xFFFF55);
            case 'f' -> new Color(0xFFFFFF);
            default -> null;
        };
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
        if (text == null || text.isEmpty()) return Message.empty();

        Message root = Message.empty();

        Color currentColor = Color.WHITE;
        boolean isBold = false;
        boolean isItalic = false;

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // formatting prefix with a following code
            if (isFormatPrefix(ch) && i + 1 < text.length()) {
                char code = text.charAt(i + 1);

                // flush what we have so far using the current styles
                if (!buf.isEmpty()) {
                    root = Message.join(root, segment(buf.toString(), currentColor, isBold, isItalic));
                    buf.setLength(0);
                }

                // apply code
                char lower = Character.toLowerCase(code);
                if (lower == 'r') {
                    currentColor = Color.WHITE;
                    isBold = false;
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

                i++; // skip the code char too
                continue;
            }

            // normal character
            buf.append(ch);
        }

        // flush remaining text
        if (!buf.isEmpty()) {
            root = Message.join(root, segment(buf.toString(), currentColor, isBold, isItalic));
        }

        return root;
    }
}
