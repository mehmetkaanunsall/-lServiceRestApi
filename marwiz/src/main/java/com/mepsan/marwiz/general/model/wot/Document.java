/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 08.04.2019 17:37:39
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.ArrayList;
import java.util.List;

public class Document {

    private String header;
    private List<Line> lines;

    public Document(String header) {
        this.header = header;
        this.lines = new ArrayList<>();
    }

    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(header);
        stringBuilder.append("\r\n");

        for (Line line : lines) {
            stringBuilder.append(line.getString());
            stringBuilder.append("\r\n");
        }
        return stringBuilder.toString();
    }

    public void addLine(Line line) {
        lines.add(line);
    }

}
