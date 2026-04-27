package com.garfild63.zanovo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
    public static void parse(String filename, Preparator preparator, boolean onlyFirstLine) {
        InputStream is = CSVParser.class.getResourceAsStream("/assets/" + filename);
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        List<String> args = new ArrayList<>();
        int x = 0;
        while (true) {
            int ch;
            try {
                ch = isr.read();
            } catch (IOException e) {
                ch = -1;
            }
            if (ch == ';' || ch == '\n') {
                args.add(sb.toString());
                sb = new StringBuilder();
                x++;
                if (ch == '\n') {
                    preparator.endLine(args);
                    args.clear();
                    x = 0;
                    if (onlyFirstLine) {
                        break;
                    }
                }
            } else if (ch == -1) {
                break;
            } else if (ch != '\r' && ch != 65279) {
                sb.append(preparator.prepareChar((char) ch, x));
            }
        }
    }

    public interface Preparator {
        void endLine(List<String> args);
        char prepareChar(char ch, int x);
    }
}
