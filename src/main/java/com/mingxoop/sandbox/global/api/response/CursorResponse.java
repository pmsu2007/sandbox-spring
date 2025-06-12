package com.mingxoop.sandbox.global.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CursorResponse<T> {
    List<T> contents;
    String nextCursor;
    long limit;
    boolean hasNext;
    public static <T> CursorResponse<T> of(
            List<T> contents,
            String nextCursor,
            long limit
    ) {
        boolean hasNext = contents.size() > limit;
        List<T> pageContents = hasNext ? contents.subList(0, (int) limit) : contents;

        return CursorResponse.<T>builder()
                .contents(pageContents)
                .nextCursor(hasNext ? nextCursor : null)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
}
