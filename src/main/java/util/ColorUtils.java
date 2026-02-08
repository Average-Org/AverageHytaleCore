package util;

import com.hypixel.hytale.server.core.Message;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.Optional;

public final class ColorUtils {
    private ColorUtils() {
    }

    private static final char AMPERSAND_FORMAT_PREFIX = '&';
    private static final char SECTION_SIGN_FORMAT_PREFIX = '§';

    private static final Color[] COLOR_MAP = {
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

    /**
     * Gets the respective color from a hex character.
     *
     * @param hexCharacter Hex character
     * @return A color object, if valid, null otherwise
     */
    public static Optional<Color> getColorFromChar(char hexCharacter) {
        int colorIndex = Character.digit(hexCharacter, 16);
        if (colorIndex >= 0 && colorIndex < 16) {
            return Optional.of(COLOR_MAP[colorIndex]);
        }

        return Optional.empty();
    }

    private static boolean isFormatPrefix(char ch) {
        return ch == AMPERSAND_FORMAT_PREFIX || ch == SECTION_SIGN_FORMAT_PREFIX;
    }

    private static Message segment(String text, Color color, boolean bold, boolean italic) {
        Message message = Message.raw(text);
        if (color != null) {
            message = message.color(color);
        }
        return message.bold(bold).italic(italic);
    }

    /**
     * Parses color codes from a string.
     *
     * @param text The text to parse
     * @return A formatted Hytale message
     */
    public static Message parseColorCodes(String text) {
        return parseColorCodes(text, true);
    }

    /**
     * Parses color codes from a string, optionally including markdown links.
     *
     * @param text         The text to parse
     * @param includeLinks Whether to include markdown links
     * @return A formatted Hytale message
     */
    public static Message parseColorCodes(String text, boolean includeLinks) {
        if (text == null || text.isEmpty()) return Message.empty();

        // strip surrounding quotes
        text = stripSurroundingQuotes(text);

        // fast path if no formatting needed
        if (!text.contains(Character.toString(AMPERSAND_FORMAT_PREFIX)) && !text.contains(Character.toString(SECTION_SIGN_FORMAT_PREFIX))
                && (!includeLinks || !text.contains("["))) {
            return Message.raw(text);
        }

        Message root = Message.empty();
        StringBuilder buf = new StringBuilder();

        Color currentColor = Color.WHITE;
        boolean isBold = false;
        boolean isItalic = false;

        int textLength = text.length();
        for (int i = 0; i < textLength; i++) {
            char currentCharacter = text.charAt(i);

            // check for markdown link pattern [text](url)
            if (includeLinks && currentCharacter == '[') {
                int closeBracket = text.indexOf(']', i);
                if (closeBracket > i) {
                    if (closeBracket + 1 < textLength && text.charAt(closeBracket + 1) == '(') {
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
            if (isFormatPrefix(currentCharacter) && i + 1 < textLength) {
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
                    Optional<Color> newColor = getColorFromChar(lower);
                    if (newColor.isPresent()) {
                        currentColor = newColor.get();
                    } else {
                        buf.append(currentCharacter).append(code);
                    }
                }

                i++;
                continue;
            }

            buf.append(currentCharacter);
        }

        // flush remaining text
        if (!buf.isEmpty()) {
            root = Message.join(root, segment(buf.toString(), currentColor, isBold, isItalic));
        }

        return root;
    }

    @NonNullDecl
    private static String stripSurroundingQuotes(String text) {
        if (text.length() >= 2) {
            if ((text.startsWith("'") && text.endsWith("'")) ||
                    (text.startsWith("\"") && text.endsWith("\""))) {
                text = text.substring(1, text.length() - 1);
            }
        }
        return text;
    }
}