package com.rainchat.rlib.commands.help;

import java.util.ArrayList;
import java.util.List;

public final class CommandUtils {

    public static String[] pagination(String[] book, int pageLength, int pageNumber) {
        int maxPages = (int) Math.ceil(book.length / (double) pageLength);
        if (pageNumber < 0) {
            pageNumber = 0;
        } else if (pageNumber > maxPages) {
            pageNumber = maxPages;
        }

        List<String> page = new ArrayList<>();
        for (int i = 0; i < pageLength; i++) {
            int index = pageLength * pageNumber + i;
            if (index >= book.length) break;

            page.add(book[index]);
        }

        return page.toArray(new String[0]);
    }

    public static String[] pagination(List<String> book, int pageLength, int pageNumber) {
        return pagination(book.toArray(new String[0]), pageLength, pageNumber);
    }

}
