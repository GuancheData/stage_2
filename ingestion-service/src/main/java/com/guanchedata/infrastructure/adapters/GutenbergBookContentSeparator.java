package com.guanchedata.infrastructure.adapters;

public class GutenbergBookContentSeparator {
    private final String bookStart = "*** START OF THE PROJECT GUTENBERG EBOOK";
    private final String bookEnd = "*** END OF THE PROJECT GUTENBERG EBOOK";

    public String[] separateContent(String content){
        int startIndex = content.indexOf(bookStart);
        int endIndex = content.indexOf(bookEnd);

        if (startIndex < 0 || endIndex < 0 || startIndex > endIndex){
            throw new IllegalArgumentException();
        }

        String header = content.substring(0, startIndex);
        String body = content.substring(startIndex + bookStart.length(), endIndex);
        return new String[]{header, body};
    }
}
