package com.sqlpro.l7cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class L7CacheMessage {
    private String sender;
    private String context;
}
