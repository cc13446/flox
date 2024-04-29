package com.cc.flox.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cc
 * @date 2024/4/29
 */
@AllArgsConstructor
@Getter
@Setter
public class HolderUtils<T> {
    private T holder;
}
