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
public class OffsetResponse<T> {
    List<T> contents;
    long offset;
    long limit;
    boolean hasNext;
    public static <T> OffsetResponse<T> of(
            List<T> contents,
            long offset,
            long limit
    ) {
        boolean hasNext = contents.size() > limit;
        List<T> pageContents = hasNext ? contents.subList(0, (int) limit) : contents;

        return OffsetResponse.<T>builder()
                .contents(pageContents)
                .offset(offset)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
}
